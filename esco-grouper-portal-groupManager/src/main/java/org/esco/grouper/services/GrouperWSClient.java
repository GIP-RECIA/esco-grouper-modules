/**
 *   Copyright (C) 2008  GIP RECIA (Groupement d'Intérêt Public REgion
 *   Centre InterActive)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>
 */



package org.esco.grouper.services;

import org.esco.grouper.domain.beans.GrouperDTO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.mysql.jdbc.log.Log;

/**
 * Web service client for grouper.
 * (written from the client example of the ESUP-COMMON framework)
 * @author GIP RECIA - A. Deman
 * 28 nov. 07
 *
 */
public class GrouperWSClient implements InitializingBean {

	/** The configuration file where Spring beans are defined. */
	//private static final String SPRING_CONFIG_FILE = "/properties/esco-grouperClient.xml";

	/** The name of the client bean.*/
	//private static final String BEAN_NAME = "GrouperServiceExposer";

	/** Used to return empty arrays. */
	private static final GrouperDTO[] EMPTY_GROUPER_INFORMATIONS_ARRAY = {};

	/** Singleton. */
	private static GrouperWSClient instance;

	/**The client bean.*/
	//@Autowired
	private IGrouperAPIExposer exposer = new GrouperAPIExposerImpl();

	/**
	 * Constructor for GrouperWSClient.
	 */
	private GrouperWSClient() {
		/* Use the factory method. */
	}

	/**
	 * Gives the singleton.
	 * @return The singleton.
	 */
	public static GrouperWSClient instance() {
		if (instance == null) {
			instance = new GrouperWSClient();
		}
		return instance;
	}

	/**
	 * Gives the access to the remote service.
	 * @return the remote service.
	 */
	private IGrouperAPIExposer getRemoteService() {
		return exposer;
	}

	/**
	 * Tests if a subject is member of a given group.
	 * @param groupName The name of the group.
	 * @param subjectId The id of the subject.
	 * @return True if the subject is a member of the group.
	 */
	public boolean hasMember(final String groupName, final String subjectId) {
		return getRemoteService().hasMember(groupName, subjectId);
	}

	/**
	 * Tests if a subject is a deep member of a given group.
	 * @param groupName The name of the group.
	 * @param subjectId The id of the subject.
	 * @return True if the subject is a member of the group.
	 */
	public boolean hasDeepMember(final String groupName, final String subjectId) {
	    return getRemoteService().hasDeepMember(groupName, subjectId);
	}

	/**
	 * Returns an instance of the IEntityGroup from the data store.
	 * @param key The key of the group to search for.
	 * @return The group if it can be found, null otherwise.
	 */
	public GrouperDTO find(final String key) {
		return getRemoteService().findGroupOrStem(key);
	}


	/**
	 * Gives the root groups from a given stem.
	 * @param key The name of the stem.
	 * @return The list of the groups in the specified stem its child stems.
	 */
	public GrouperDTO[] getAllRootGroupsFromStem(final String key) {
		final GrouperDTO[] result = getRemoteService().getAllRootGroupsFromStem(key);
		if (result != null) {
			return result;
		}
		return EMPTY_GROUPER_INFORMATIONS_ARRAY;
	}

	/**
	 * Gives the entities members of the group.
	 * @param key The key of the group.
	 * @return The entities member of the group.
	 * @see org.esco.grouper.services.IGrouperAPIExposer
	 * #getMemberSubjects(java.lang.String)
	 */
	public GrouperDTO[] getMemberEntities(final String key) {

		final GrouperDTO[] result = getRemoteService().getMemberSubjects(key);
		if (result != null) {
			return result;
		}
		return EMPTY_GROUPER_INFORMATIONS_ARRAY;
	}

	/**
	 * Gives all the entities members of the group.
	 * @param key The key of the group.
	 * @return  All the entities member of the group.
	 * @see org.esco.grouper.services.IGrouperAPIExposer
	 * #getMemberSubjects(java.lang.String)
	 */
	public GrouperDTO[] getAllMemberEntities(final String key) {

	    final GrouperDTO[] result = getRemoteService().getAllMemberSubjects(key);
	    if (result != null) {
	        return result;
	    }
	    return EMPTY_GROUPER_INFORMATIONS_ARRAY;
	}

	/**
	 * Gives the subgroups members of the group.
	 * @param key The key of the group.
	 * @return The members of the group.
	 * @see org.esco.grouper.services.IGrouperAPIExposer
	 * #getMemberGroups(java.lang.String)
	 */
	public GrouperDTO[] getMemberGroups(final String key) {

		final GrouperDTO[] result = getRemoteService().getMemberGroups(key);
		if (result != null) {
			return result;
		}
		return EMPTY_GROUPER_INFORMATIONS_ARRAY;
	}


    /**
     * Gives All th groups members of the group.
     * @param key The key of the group.
     * @return All the subgroups.
     * @see org.esco.grouper.services.IGrouperAPIExposer
     * #getMemberGroups(java.lang.String)
     */
    public GrouperDTO[] getAllMemberGroups(final String key) {

        final GrouperDTO[] result = getRemoteService().getAllMemberGroups(key);
        if (result != null) {
            return result;
        }
        return EMPTY_GROUPER_INFORMATIONS_ARRAY;
    }

    /**
     * Gives All the members of the group : groups and entities.
     * @param key The key of the group.
     * @return All the subgroups and entities that are member of the group
     * directly or indirectly.
     * @see org.esco.grouper.services.IGrouperAPIExposer
     * #getMemberGroups(java.lang.String)
     */
    public GrouperDTO[] getAllMembers(final String key) {

        final GrouperDTO[] result = getRemoteService().getAllMembers(key);
        if (result != null) {
            return result;
        }
        return EMPTY_GROUPER_INFORMATIONS_ARRAY;
    }


	/**
	 * Gives the group descriptions of the groups to which a given group belongs to.
	 * @param key The key of the considered group/stem.
	 * @return The list of the groups which the group identified by key belongs to,
	 * or the parent stem in the case of a stem.
	 * @see org.esco.grouper.services.IGrouperAPIExposer
	 * #getMembershipsForGroupOrStem(java.lang.String)
	 */
	public GrouperDTO[] getMembershipsForGroup(final String key) {
		final GrouperDTO[] result = getRemoteService().getMembershipsForGroupOrStem(key);
		if (result != null) {
			return result;
		}
		return EMPTY_GROUPER_INFORMATIONS_ARRAY;
	}


	/**
	 * Gives an iterator through the group description of the groups to which a given subject belongs to.
	 * @param subjectKey The key of the considered subject.
	 * @return The list of the groups which the subject identified by subjectKey belongs to.
	 */
	public GrouperDTO[] getMembershipsForSubjects(final String subjectKey) {
		final GrouperDTO[] result = getRemoteService().getMembershipsForSubject(subjectKey);
		if (result != null) {
			return result;
		}
		return EMPTY_GROUPER_INFORMATIONS_ARRAY;
	}

	/**
	 * Search groups whose name matches the query string according to the specified method.
	 * @param query The part of the name of the group.
	 * @param method The method used to perform the comparison.
	 * @return The array of groups descriptions whose name match query according
	 * to the method of comparison.
	 * @see org.esco.grouper.services.IGrouperAPIExposer
	 * #searchForGroupsOrStems(java.lang.String, int)
	 */
	public GrouperDTO[] searchForGroups(final String query, final int method) {

		final GrouperDTO[] result = getRemoteService().searchForGroupsOrStems(query, method);
		if (result != null) {
			return result;
		}
		return EMPTY_GROUPER_INFORMATIONS_ARRAY;
	}

	/**
	 * Search subjects whose name matches the query string according to the specified method.
	 * @param query The part of the name of the subject.
	 * @param method The method used to perform the comparison.
	 * @return The array of subject id whose name match query according
	 * to the method of comparison.
	 * @see org.esco.grouper.services.IGrouperAPIExposer
	 * #searchForSubjects(java.lang.String, int)
	 */
	public String[] searchForSubjects(final String query, final int method) {
		return getRemoteService().searchForSubjects(query, method);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(exposer, "Le bean exposer n'est pas correctement initialisé !");
	}

	/**
	 * @param args
	 */
/*	public static void main(final String[] args) {

		final String arrow = "   ===>";

		final GrouperWSClient client = GrouperWSClient.instance();


		final String key = "esco:ENT_Groupes:subGroup1";
		GrouperDTO gi = client.find(key);
		client.find(key);
		System.out.println("\nGroup found for " + key + ":");
		System.out.println(arrow + gi);


		final String skey = "Aeg00000";
		GrouperDTO[] ms = client.getMembershipsForSubjects(skey);
		System.out.println("\nMemberships for " + skey + ":");
		for (GrouperDTO gd : ms) {
			System.out.println(arrow + gd);
		}

		final String gkey0 = "esco:ENT_Groupes";
		GrouperDTO[] gms = client.getMembershipsForGroup(gkey0);
		System.out.println("\nMemberships for " + gkey0 + ":");
		for (GrouperDTO gd : gms) {
			System.out.println(arrow + gd);
		}

		final String gkey1 = "esco:ENT_Groupes:subGroup1";
		gms = client.getMembershipsForGroup(gkey1);
		System.out.println("\nMemberships for " + gkey1 + ":");
		for (GrouperDTO gd : gms) {
			System.out.println(arrow + gd);
		}

//		final String gkey2 = "esco:ENT_Groupes:etb1:Etb1_Eleves";
//		String[] mbers = client.getMemberSubjects(gkey2);
//		System.out.println("\nMembers of " + gkey2 + ":");
//		for (String s : mbers) {
//			System.out.println(arrow + s);
//		}

		final String q0 = "esco";
		GrouperDTO[] gis = client.searchForGroups(q0, IGroupConstants.STARTS_WITH);
		System.out.println("\nGroups found for query " + q0 + ", method STARTS_WITH:");

		for (GrouperDTO gInfo : gis) {
			System.out.println(arrow + gInfo);
		}

		final String q1 = "esco:admin:";
		gis = client.searchForGroups(q1, IGroupConstants.STARTS_WITH);
		System.out.println("\nGroups found for query " + q1 + ", method STARTS_WITH:");

		for (GrouperDTO gInfo : gis) {
			System.out.println(arrow + gInfo);
		}

		final String q2 = "etb";
		gis = client.searchForGroups(q2, IGroupConstants.CONTAINS);
		System.out.println("\nGroups found for query " + q2 + ", method CONTAINS:");

		for (GrouperDTO gInfo : gis) {
			System.out.println(arrow + gInfo);
		}

		final String q3 = "b1";
		gis = client.searchForGroups(q3, IGroupConstants.ENDS_WITH);
		System.out.println("\nGroups found for query " + q0 + ", method ENDS_WITH:");

		for (GrouperDTO gInfo : gis) {
			System.out.println(arrow + gInfo);
		}

		final String q4 = "esco:admin:local:admin_etb2";
		gis = client.searchForGroups(q4, IGroupConstants.IS);
		System.out.println("\nGroups found for query " + q4 + ", method IS:");

		for (GrouperDTO gInfo : gis) {
			System.out.println(arrow + gInfo);
		}


		final String q5 = "Aeg00000";
		String[] sids = client.searchForSubjects(q5, IGroupConstants.IS);
		System.out.println("\nSubjects found for query " + q5 + ", method IS:");

		for (String id : sids) {
			System.out.println(arrow + id);
		}

		final String q6 = "Aeg";
		sids = client.searchForSubjects(q6, IGroupConstants.STARTS_WITH);
		System.out.println("\nSubjects found for query " + q6 + ", method STARTS_WITH:");

		for (String id : sids) {
			System.out.println(arrow + id);
		}

		final String q7 = "0";
		sids = client.searchForSubjects(q7, IGroupConstants.ENDS_WITH);
		System.out.println("\nSubjects found for query " + q7 + ", method ENDS_WITH:");

		for (String id : sids) {
			System.out.println(arrow + id);
		}

		final String q8 = "09";
		sids = client.searchForSubjects(q8, IGroupConstants.CONTAINS);
		System.out.println("\nSubjects found for query " + q8 + ", method CONTAINS:");

		for (String id : sids) {
			System.out.println(arrow + id);
		}

		System.out.println("\n<<<<<<<<<< END >>>>>>>>>>>");
	}*/
}
