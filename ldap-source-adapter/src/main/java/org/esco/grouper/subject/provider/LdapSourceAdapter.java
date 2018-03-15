/**
 * Copyright © 2008 GIP-RECIA (https://www.recia.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esco.grouper.subject.provider;

import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.net.ssl.SSLSocketFactory;

import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import edu.internet2.middleware.subject.SubjectNotUniqueException;
import edu.internet2.middleware.subject.provider.BaseSourceAdapter;
import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.SearchFilter;
import edu.vt.middleware.ldap.pool.DefaultLdapFactory;
import edu.vt.middleware.ldap.pool.LdapPoolConfig;
import edu.vt.middleware.ldap.pool.SoftLimitLdapPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Ldap source adapter.  Configuration is from a properties file
 */

public class LdapSourceAdapter extends BaseSourceAdapter {

	private static Log log = LogFactory.getLog(LdapSourceAdapter.class);

	private String nameAttributeName = null;
	private String subjectIDAttributeName = null;
	private String descriptionAttributeName = null;
	private String subjectTypeString = null;
	private String localDomain = null;
	private String propertiesFile = null;
	private SoftLimitLdapPool ldapPool;
	private boolean initialized = false;
	protected HashMap<String, Search> localSearches = new HashMap();

	private boolean multipleResults = false;

	private String[] allAttributeNames;

	public LdapSourceAdapter() {
		super();
	}

	public LdapSourceAdapter(String id, String name) {
		super(id, name);
	}

	// initialization

	/**
	 * {@inheritDoc}
	 */
	public void init() {
		log.debug("ldap source init");

		Properties props = initParams();

		nameAttributeName = getStringProperty(props,"Name_AttributeType");
		subjectIDAttributeName = getStringProperty(props,"SubjectID_AttributeType");
		descriptionAttributeName = getStringProperty(props,"Description_AttributeType");

		propertiesFile = getStringProperty(props,"ldapProperties_file");
		String mr = getStringProperty(props,"Multiple_Results");
		if (mr!=null && (mr.equalsIgnoreCase("yes")||mr.equalsIgnoreCase("true"))) multipleResults = true;

		Set attributeNameSet = this.getAttributes();
		allAttributeNames = new String[3+attributeNameSet.size()];
		allAttributeNames[0] = nameAttributeName;
		allAttributeNames[1] = subjectIDAttributeName;
		allAttributeNames[2] = descriptionAttributeName;
		int i = 0;
		for (Iterator it = attributeNameSet.iterator(); it.hasNext(); allAttributeNames[3+i++]= (String) it.next());

		initializeLdap();
	}

	private void initializeLdap() {

		log.debug("ldap initializeLdap");

		// all ldap config from the ldap properties file
		log.debug("reading properties file " + propertiesFile);

		final ClassLoader cl = getClass().getClassLoader();
		final InputStream is  = cl.getResourceAsStream(propertiesFile);
		if (is == null) {
			log.error("Error loading (from classpath) " + propertiesFile);
		}
		LdapConfig ldapConfig = LdapConfig.createFromProperties(is);
		log.debug("from properties file " + propertiesFile + " got " + ldapConfig);

		// including config for the pem cert mode
		Map<String, Object> props = ldapConfig.getEnvironmentProperties();

		Set<String> ps = props.keySet();
		for (Iterator it = ps.iterator(); it.hasNext(); log.debug(".. key = " + it.next()));

		String cafile = (String)props.get("pemCaFile");
		String certfile = (String)props.get("pemCertFile");
		String keyfile = (String)props.get("pemKeyFile");
		if (cafile!=null && certfile!=null && keyfile!=null) {
			log.debug("using the PEM socketfactory: ca=" + cafile + ", cert=" + certfile + ", key=" + keyfile);
			LdapPEMSocketFactory sf = new LdapPEMSocketFactory(cafile, certfile, keyfile);
			SSLSocketFactory ldapSocketFactory = sf.getSocketFactory();
			ldapConfig.setSslSocketFactory(ldapSocketFactory);
		} else {
			log.debug("using the default socketfactory");
		}

		DefaultLdapFactory factory = new DefaultLdapFactory(ldapConfig);
		LdapPoolConfig poolConfig = new LdapPoolConfig();

		try {
			ldapPool = new SoftLimitLdapPool(factory);
			ldapPool.initialize();
			initialized = true;
		} catch (Exception e) {
			log.debug("ldappool error = " + e);
		}
		log.debug("ldap initialize done");
	}


	// Public methods

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Subject getSubject(String id, boolean exceptionIfNull) throws SubjectNotFoundException,SubjectNotUniqueException {
		Subject subject = null;
		Search search = getLocalSearch("searchSubject");
		if (search == null) {
			log.error("searchType: \"searchSubject\" not defined.");
			return subject;
		}
		try {
			Attributes attributes = getLdapUnique( search, id, allAttributeNames   );
			subject = createSubject(attributes);
			if (subject == null && exceptionIfNull) {
				throw new SubjectNotFoundException("Subject " + id + " not found.");
			}
		} catch (SubjectNotFoundException e) {
			throw new SubjectNotFoundException("Subject " + id + " not found.");
		}
		return subject;
	}

	/**
	 * {@inheritDoc}
	 * @deprecated
	 */
	@Deprecated
	@Override
	public Subject getSubject(String id) throws SubjectNotFoundException, SubjectNotUniqueException {
		return this.getSubject(id, true);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Subject getSubjectByIdentifier(String id, boolean exceptionIfNull) throws SubjectNotFoundException,SubjectNotUniqueException {
		Subject subject = null;
		Search search = getLocalSearch("searchSubjectByIdentifier");
		if (search == null) {
			log.error("searchType: \"searchSubjectByIdentifier\" not defined.");
			return subject;
		}
		if (localDomain != null) {
			int atpos;
			if ((atpos=id.indexOf("@" + localDomain))>0) {
				log.debug("looking at id=" + id);
				id = id.substring(0, atpos);
				log.debug("converted to id=" + id);
			}
		}
		try {
			Attributes attributes = getLdapUnique( search, id, allAttributeNames );
			subject = createSubject(attributes);
			if (subject == null && exceptionIfNull) {
				throw new SubjectNotFoundException("Subject " + id + " not found.");
			}
		} catch (SubjectNotFoundException e) {
			throw new SubjectNotFoundException("Subject " + id + " not found.");
		}
		search = getLocalSearch("searchSubjectByIdentifierAttributes");
		if (search==null) ((LdapSubject)subject).setAttributesGotten(true);
		return subject;
	}

	/**
	 * {@inheritDoc}
	 * @deprecated
	 */
	@Deprecated
	@Override
	public Subject getSubjectByIdentifier(String id) throws SubjectNotFoundException, SubjectNotUniqueException {
		return this.getSubjectByIdentifier(id, true);
	}

	protected void setLocalSearches(HashMap<String, Search> searches1) {
		this.localSearches = searches1;
	}

	protected HashMap<String, Search> getLocalSearches() {
		return this.localSearches;
	}

	protected Search getLocalSearch(String searchType) {
		HashMap searches1 = this.getLocalSearches();
		return (Search)searches1.get(searchType);
	}

	public void loadSearch(Search search) {
		if(log.isDebugEnabled()) {
			log.debug("Loading search: " + (search == null?null:search.getSearchType()));
		}

		this.localSearches.put(search.getSearchType(), search);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set search(String searchValue) {
		Comparator cp = new org.esco.grouper.subject.provider.LdapComparator();
		TreeSet result = new TreeSet(cp);
		Search search = getLocalSearch("search");
		if (search == null) {
			log.error("searchType: \"search\" not defined.");
			return result;
		}
		Search searchA = getLocalSearch("searchAttributes");
		boolean noAttrSearch = true;
		if (searchA!=null) noAttrSearch = false;

		Iterator<SearchResult> ldapResults = getLdapResults(search,searchValue, allAttributeNames);
		if (ldapResults == null) {
			return result;
		}
		while ( ldapResults.hasNext()) {
			SearchResult si = (SearchResult) ldapResults.next();
			Attributes attributes = si.getAttributes();
			Subject subject = createSubject(attributes);
			if (noAttrSearch) ((LdapSubject)subject).setAttributesGotten(true);
			result.add(subject);
		}

		log.debug("set has " + result.size() + " subjects");
		if (result.size()>0) log.debug("first is " + ((Subject)result.first()).getName());
		return result;
	}

	private Subject createSubject( Attributes attributes) {
		String name = "";
		String subjectID = "";
		String description = "";

		if (attributes==null) {
			log.debug("ldap create subject with null attrs");
			return (null);
		}
		try {
			Attribute attribute = attributes.get(subjectIDAttributeName);
			if (attribute == null) {
				log.error("No value for LDAP attribute \"" + subjectIDAttributeName + "\". It is Grouper attribute \"SubjectID\".");
				return null;
			}
			subjectID   = ((String)attribute.get()).toLowerCase();
			attribute = attributes.get(nameAttributeName);
			if (attribute == null) {
				log.debug("No immedaite value for attribute \"" + nameAttributeName + "\". Will look later.");
			} else {
				name = (String) attribute.get();
			}
			attribute = attributes.get(descriptionAttributeName);
			if (attribute == null) {
				log.debug("No immedaite value for attribute \"" + descriptionAttributeName + "\". Will look later.");
			} else {
				description   = (String) attribute.get();
			}
		} catch (NamingException ex) {
			log.error("LDAP Naming Except: " + ex.getMessage(), ex);
		}
		LdapSubject subject = new LdapSubject(subjectID,  name, description, this.getSubjectType().getName(), this.getId());

		// add the attributes

		Map myAttributes = new HashMap();
		try {
			for (NamingEnumeration e = attributes.getAll(); e.hasMore();) {
				Attribute attr = (Attribute) e.next();
				String attrName = attr.getID();
				// skip the basic ones
				if (attrName.equals(nameAttributeName)) continue;
				if (attrName.equals(subjectIDAttributeName)) continue;
				if (attrName.equals(descriptionAttributeName)) continue;
				Set values = new HashSet();
				for (NamingEnumeration en = attr.getAll(); en.hasMore(); ) {
					Object value =  en.next();
					values.add(value.toString());
				}
				myAttributes.put(attrName, values);
			}
			subject.setAttributes(myAttributes);
		} catch (NamingException e ) {
			log.error("Naming error: " + e);
		}

		return subject;
	}

	protected String getStringProperty(Properties props, String prop) {
		String value = props.getProperty(prop);
		if (value==null) {
			log.error("Property '" + prop + "' is not defined!");
		}
		return (value);
	}


	/**
	 * Try to get more attributes for the argument subject. (from name)
	 */
	protected Map getAllAttributes(LdapSubject subject) {
		Map attributes = new HashMap();
		log.debug("getAllAttributes for " + subject.getName());
		Search search = getLocalSearch("searchSubjectAttributes");
		if (search == null) {
			log.error("searchType: \"searchSubjectAttributes\" not defined.");
			return attributes;
		}

		try {
			Attributes ldapAttributes = getLdapUnique(search,subject.getName(), allAttributeNames);
			for (NamingEnumeration e = ldapAttributes.getAll(); e.hasMore();) {
				Attribute attr = (Attribute) e.next();
				String attrName = attr.getID();

				// special case the basic ones
				if (attrName.equals(subjectIDAttributeName)) continue;  // already have
				if (attrName.equals(nameAttributeName)) continue; // already have
				if (attrName.equals(descriptionAttributeName)) {
					subject.setDescription((String)attr.get());
					continue;
				}

				Set values = new HashSet();
				for (NamingEnumeration en = attr.getAll(); en.hasMore(); ) {
					Object value =  en.next();
					values.add(value.toString());
				}
				attributes.put(attrName, values);
			}
			subject.setAttributes(attributes);
		} catch (SubjectNotFoundException ex ) {
			log.error("SubjectNotFound: "+ subject.getId() +" " + ex.getMessage(), ex);
		} catch (SubjectNotUniqueException ex ) {
			log.error("SubjectNotUnique: "+ subject.getId() +" " + ex.getMessage(), ex);
		} catch (NamingException ex ) {
			log.error("LDAP Naming Except: " + ex.getMessage(), ex);
		}
		return attributes;
	}

	protected Iterator<SearchResult> getLdapResults(Search search, String searchValue, String[] attributeNames) {
		Ldap ldap = null;
		String filter = null;
		Iterator<SearchResult> results = null;
		int cp;
		String aff = null;

		if (!initialized) initializeLdap();

		if ((cp=searchValue.indexOf(',')) >0 ) {
			int lb, rb;
			if ( (lb=searchValue.indexOf('['))>cp && (rb=searchValue.indexOf(']'))>lb ) {
				aff = searchValue.substring(lb+1, rb);
				searchValue = searchValue.substring(0, lb);
				// log.debug("first, last [" + aff + "] search: " + searchValue);
				filter = search.getParam("affiliationfilter");
			} else {
				// log.debug("first, last search: " + searchValue);
				filter = search.getParam("firstlastfilter");
			}
			if (filter==null) {
				log.error("Search filter not found for search type:  " + search.getSearchType());
				return results;
			}
			String last = searchValue.substring(0, cp);
			String first = searchValue.substring(cp+1);
			if (last!=null) filter = filter.replaceAll("%LAST%", escapeSearchFilter(last));
			if (first!=null) filter = filter.replaceAll("%FIRST%", escapeSearchFilter(first));
			if (aff!=null) filter = filter.replaceAll("%AFFILIATION%", escapeSearchFilter(aff));
		} else {
			// simple search
			filter = search.getParam("filter");
			if (filter==null) {
				log.error("Search filter not found for search type:  " + search.getSearchType());
				return results;
			}
			filter = filter.replaceAll("%TERM%", escapeSearchFilter(searchValue));
		}
		log.debug("searchType: " + search.getSearchType() + " filter: " + filter);

		try  {
			ldap =  (Ldap) ldapPool.checkOut();
			results = ldap.search(new SearchFilter(filter), attributeNames );
		} catch (NamingException ex) {
			log.error("Ldap NamingException: " + ex.getMessage(), ex);
		} catch (Exception ex) {
			log.error("Ldap Exception: " + ex.getMessage(), ex);

		} finally {
			if (ldap != null) {
				try {
					ldapPool.checkIn(ldap);
				} catch (Exception e) {
					log.error("Could not return Ldap object back to pool", e);
				}
			}
		}
		return results;
	}


	protected Attributes getLdapUnique( Search search, String searchValue, String[] attributeNames)
	throws SubjectNotFoundException,SubjectNotUniqueException  {
		Attributes attributes = null;
		Iterator<SearchResult> results = getLdapResults(search, searchValue, attributeNames);

		if (results == null || !results.hasNext()) {
			String errMsg = "No results: " + search.getSearchType() + " filter:" + search.getParam("filter") + " searchValue: " + searchValue;
			throw new SubjectNotFoundException( errMsg);
		}

		SearchResult si = ( SearchResult )results.next( );
		attributes = si.getAttributes();
		if ( results.hasNext()) {
			si = (SearchResult) results.next();
			if (!multipleResults) {
				String errMsg ="Search is not unique:" + si.getName() + "\n";
				throw new SubjectNotUniqueException( errMsg );
			}
			Attributes attr = si.getAttributes();
			NamingEnumeration<? extends Attribute> n = attr.getAll();
			try {
				while (n.hasMore()) {
					Attribute a = n.next();
					log.debug("checking attribute " + a.getID());
					if (attributes.get(a.getID())==null) {
						log.debug("adding " + a.getID());
						attributes.put(a);
					}
				}
			} catch (NamingException e) {
				log.error("ldap excp: " + e);
			}
		}
		return attributes;
	}

	/**
	 * Escape a search filter to prevent LDAP injection.
	 * From http://www.owasp.org/index.php/Preventing_LDAP_Injection_in_Java
	 *
	 * @param filter
	 * @return escaped filter
	 */
	protected String escapeSearchFilter(String filter) {
		//From RFC 2254
		String escapedStr = new String(filter);
		escapedStr = escapedStr.replaceAll("\\\\","\\\\5c");
		// We want people to be able to use wildcards.
		// escapedStr = escapedStr.replaceAll("\\*","\\\\2a");
		escapedStr = escapedStr.replaceAll("\\(","\\\\28");
		escapedStr = escapedStr.replaceAll("\\)","\\\\29");
		escapedStr = escapedStr.replaceAll("\\"+Character.toString('\u0000'), "\\\\00");
		return escapedStr;
	}
	/**
	 * @see edu.internet2.middleware.subject.Source#checkConfig()
	 */
	public void checkConfig() {
	}
	/**
	 * @see edu.internet2.middleware.subject.Source#printConfig()
	 */
	public String printConfig() {
		String message = "sources.xml ldap source id:   " + this.getId() + ": " + propertiesFile;
		return message;
	}
}
