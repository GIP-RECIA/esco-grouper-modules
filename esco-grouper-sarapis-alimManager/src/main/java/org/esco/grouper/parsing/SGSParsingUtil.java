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
/**
 *
 */
package org.esco.grouper.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import edu.internet2.middleware.grouper.Stem;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.esco.grouper.dao.GrouperDAO;
import org.esco.grouper.domain.beans.EvaluableString;
import org.esco.grouper.domain.beans.GroupOrFolderDefinition;
import org.esco.grouper.domain.beans.GroupOrFolderDefinitionsManager;
import org.esco.grouper.domain.beans.MembersDefinition;
import org.esco.grouper.domain.beans.PersonType;
import org.esco.grouper.domain.beans.PrivilegeDefinition;
import org.esco.grouper.domain.beans.TemplateElement;
import org.esco.grouper.exceptions.UnknownTemplateElementTempateElement;
import org.esco.grouper.utils.GrouperSessionUtil;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Content handler used read the defintion of groups, folders, administration elements, etc,
 * for the Sarapis Group Service (SGS).
 * @author GIP RECIA - A. Deman
 * 28 juil. 08
 *
 */
public class SGSParsingUtil extends org.xml.sax.helpers.DefaultHandler implements EntityResolver {

    /** Constant use to go up in the path. */
    private static final String UP_IN_PATH = "..:";

    /** Constants to use the same path. */
    private static final String CURRENT_PATH = ".";

    /** Constants to use the same path (escaped for replacements). */
    private static final String ESC_CURRENT_PATH = "\\.";

    /** User tag. */
    private static final String USER_TAG = "user";

    /** Folder tag. */
    private static final String FOLDER_TAG = "folder";

    /** Folder template tag. */
    private static final String FOLDER_TEMPL_TAG = "folder-template";

    /** Group tag. */
    private static final String GROUP_TAG = "group";

    /** Group template tag. */
    private static final String GROUP_TEMPL_TAG = "group-template";

    /** Privilege tag. */
    private static final String PRIVILEGE_TAG = "privilege";

    /** Member of tag. */
    private static final String MEMBER_OF_TAG = "member-of";

    /** Members  tag. */
    private static final String MEMBERS_TAG = "members";

    /** Template element tag. */
    private static final String TEMPLATE_ELT_TAG = "template-element";

    /** Delete empty folders tag. */
    private static final String DEL_EMPTY_FOLD_TAG = "delete-empty-folders";

    /** Delete empty groups tag. */
    private static final String DEL_EMPTY_GROUPS_TAG = "delete-empty-groups";

    /** Delete empty groups tag. */
    private static final String FORCE_PRIV_TAG = "force-privileges";

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(SGSParsingUtil.class);

    /** Locator. */
    private Locator locator;

    /** Recursive administration paths. */
    private Set<PrivilegeDefinition> recPrivileges = new HashSet<PrivilegeDefinition>();

    /** Starting path of recursive privileges. */
    private Map<String, List<PrivilegeDefinition>> startOfRecPrivileges =
        new HashMap<String, List<PrivilegeDefinition>>();

    /** The group or folder definitions manager. */
    private GroupOrFolderDefinitionsManager definitionsManager;

    /** The attribute handler. */
    private SGSAttributeHandler attributeHandler = new SGSAttributeHandler();

    /** The current group or folder folder under construction. */
    private GroupOrFolderDefinition current;

    /** The containing path. */
    private String currentPath = "";

    /** The URI of the xml file that contains the definitions for the folder, groups, administration
     * privileges and memberships. */
    private String definitionsFileURI;

    /** The grouper session util to parameter. */
    private GrouperSessionUtil grouperSessionUtil;

    /** The Grouper dao instance to parameter. */
    private GrouperDAO grouperDAO;

    /**
     * Builds an instance of SGSParsingUtil.
     */
    public SGSParsingUtil() {
        super();
        locator = new LocatorImpl();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creation of a ContentHandler: " + getClass().getSimpleName() + ".");
        }
    }

    /**
     * Checks the data injection.
     * @throws Exception
     */
    @PostConstruct
    public void afterPropertiesSet() throws Exception {

        Validate.notNull(this.grouperSessionUtil,
                "property grouperSessionUtil of class " + this.getClass().getName()
                        + " can not be null.");

        Validate.notNull(this.grouperDAO,
                "property grouperUtil of class " + this.getClass().getName()
                + " can not be null.");

        Validate.notNull(this.definitionsManager,
                "property definitionManager of class " + this.getClass().getName()
                + " can not be null.");

        Validate.notNull(this.definitionsFileURI,
                "property definitionsFileURI of class " + this.getClass().getName()
                + " can not be null.");
    }

    /**
     * Error method.
     * @param e The initial exception.
     * @throws SAXException The initial exception.
     */
    @Override
    public void error(final SAXParseException e) throws SAXException {
        LOGGER.debug(e, e);
        throw e;
    }



    /**
     * Receives notification of the end of a document.
     * The validity of the path references, for administration or memebrships are checked
     * at this step.
     * @throws SAXException
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    @Override
    public void endDocument() throws SAXException {
        LOGGER.debug("End of parsing.");
        final List<String> errors = definitionsManager.checksPathsReferences();
        if (!errors.isEmpty()) {
            for (String error : errors) {
                LOGGER.fatal(error);
            }
            throw new SAXParseException("Error(s) detected while checking the validity of "
                    + "the paths references (" + errors.size()
                    + " error(s) detected). See the log file for more details.",
                    locator);
        }

    }

    /**
     * Receives notification of the end of an element.
     * @param nameSpaceURI the Namespace URI, or the empty string if the
     * element has no Namespace URI or if Namespace
     * processing is not being performed.
     * @param localName localName the local name (without prefix), or the
     * empty string if Namespace processing is not being
     * performed.
     * @param qualifiedName The qualified XML name (with prefix), or the
     * empty string if qualified names are not available.
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String nameSpaceURI,
            final String localName,
            final String qualifiedName) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Closing Tag: " + localName);
        }
        if (FOLDER_TAG.equals(localName) || FOLDER_TEMPL_TAG.equals(localName)) {

            // Removes the recursive administrating path for wich
            // this folder is the starting point.
            final List<PrivilegeDefinition> recPrivilegesForCurrent = startOfRecPrivileges.get(currentPath);
            if (recPrivilegesForCurrent != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Removing recursive administrating path for "
                            + currentPath + ": " + recPrivilegesForCurrent + ".");
                }
                for (PrivilegeDefinition rp : recPrivilegesForCurrent) {
                    recPrivileges.remove(rp);
                }
                startOfRecPrivileges.remove(currentPath);
            } else if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No recursiveprivileges path for "
                        + currentPath + ".");
            }

            // Returns to the previous path.
            final int index = currentPath.lastIndexOf(Stem.DELIM);
            if (index > 0) {
                currentPath = currentPath.substring(0, index);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Returning to the previous path: " + currentPath);
            }

        }
    }

    /**
     * Resolve relative path.
     * @param tag The current processed tag.
     * @param path The  path to resolve.
     * @return The resolved path.
     * @throws SAXException If the path can't be resolved.
     */
    protected String resolvePath(final String tag, final String path)
    throws SAXException {


        // Resolves the paths with ../../../reference/to/a/folder/
        if (path.startsWith(UP_IN_PATH)) {

            // Error the current path is not set.
            String relativePath = path;
            String currentReference = current.getPath();
            while (relativePath.startsWith(UP_IN_PATH)) {

                final int index = currentReference.lastIndexOf(Stem.DELIM);

                // Error can't go up.
                if (index < 0) {
                    final String msg = "Error while resolving path: " + path
                    + " - too much \"" + UP_IN_PATH + "\"(Tag " + tag + " - line " + locator.getLineNumber() + ").";
                    LOGGER.fatal(msg);
                    throw new SAXException(msg);
                }
                currentReference = currentReference.substring(0, index);
                relativePath = relativePath.replaceFirst(UP_IN_PATH, "");
            }
            if (!relativePath.startsWith(Stem.DELIM)) {
                relativePath = Stem.DELIM + relativePath;
            }
            return currentReference + relativePath;
        }

        // The path is relative and referes to the current path.
        if (path.startsWith(CURRENT_PATH)) {

            String resolvedPath = path.replaceFirst(CURRENT_PATH, current.getPath());
            return resolvedPath.replaceAll(ESC_CURRENT_PATH, "");
        }


        // The path is absolute.
        return path;
    }

    /**
     * Setter for the object for locating the origin of SAX document events.
     * @param locator The new locator to use.
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    @Override
    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;

    }

    /**
     * Start of the document.
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    @Override
    public void startDocument() {
        LOGGER.debug("Starting the parsing.");
    }

    /**
     * Registers a template element.
     */
    protected void handleTemplateElement() {
        if (attributeHandler.getDefaultValue() == null) {
            // Registers a template element without default value.
            TemplateElement.registerTemplateElement(attributeHandler.getKey());
        } else {
            // Registers a template element with a default value.
            TemplateElement.registerTemplateElement(attributeHandler.getKey(),
                    attributeHandler.getDefaultValue());
        }
    }

    /**
     * Handles the privilege informations.
     * @throws SAXException If there is a parsing exception.
     * @throws UnknownTemplateElementTempateElement If the definition of the
     * privilege used a template element wich is not defined.
     */
    protected void handlePrivileges() throws SAXException,
    UnknownTemplateElementTempateElement {

        // Checks the path.
        if (attributeHandler.getPath() == null || "".equals(attributeHandler.getPath())) {
            handleAttributeError(PRIVILEGE_TAG, SGSAttributeHandler.PATH_ATTR);
        }

        // Adds privilege to the current group or folder definition.
        final String resolvedPath = resolvePath(PRIVILEGE_TAG, attributeHandler.getPath());
        final PrivilegeDefinition privilege =
            new PrivilegeDefinition(attributeHandler.getRight(), new EvaluableString(resolvedPath));
        current.addPrivilege(privilege);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding privilege: " +  privilege
                    + " to the group or folder: " + current.getPath());
        }

        // The privilege must registered as a recursive one.
        if (attributeHandler.getRecursive()) {

            // Error if the recursive administring path is already defined.
            if (recPrivileges.contains(privilege)) {
                String originalStart = "";
                final Iterator<String> iter = startOfRecPrivileges.keySet().iterator();
                boolean found = false;
                while (iter.hasNext() && !found) {
                    final String start = iter.next();
                    final List<PrivilegeDefinition> recPriv = startOfRecPrivileges.get(start);
                    if (recPriv.contains(privilege)) {
                        originalStart = start;
                        found = true;
                    }
                }

                final String msg = "Encounters recursive privilege " + privilege
                        + " for: " + currentPath
                        + " which is already defined for: "
                        + originalStart
                        + " (line " + locator.getLineNumber() + ").";
                LOGGER.error(msg);
                throw new SAXParseException(msg, locator);
            }

            recPrivileges.add(privilege);
            List<PrivilegeDefinition> privilegesForCurrent = startOfRecPrivileges.get(currentPath);

            if (privilegesForCurrent == null) {
                privilegesForCurrent = new ArrayList<PrivilegeDefinition>();
                startOfRecPrivileges.put(currentPath, privilegesForCurrent);
            }
            privilegesForCurrent.add(privilege);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Registering starting point " + currentPath
                        + " for recursive administrating path: " + attributeHandler.getPath() + ".");
            }
        }
    }

    /**
     * Handles the definition of who has to be a member of a group.
     * @throws SAXParseException If there is a parsing error.
     */
    protected void handleMembersRule() throws SAXParseException {

        // The tag members-of should contains :
        // Required : Type = ALL | TEACHER | STUDENT | TOS | ADMINISTRATIVE | PARENT
        // Optional : distribution-by = %aTemplateElement%
        if (attributeHandler.getType() == null) {
            handleAttributeError(MEMBERS_TAG,
                    SGSAttributeHandler.TYPE_ATTR,
                    PersonType.values(),
                    null);
        }

        if (attributeHandler.getDistributionBy() != null) {
            if (!TemplateElement.isTemplateElement(attributeHandler.getDistributionBy())) {
                handleAttributeError(MEMBERS_TAG,
                        SGSAttributeHandler.DISTRIB_BY_ATTR,
                        TemplateElement.getAvailableTemplateElements().toArray(),
                        attributeHandler.getDistributionBy());
            }
        }

        // Adds the definition of the members.
        final String templateKey = attributeHandler.getDistributionBy();
        final TemplateElement templateElt = TemplateElement.getAvailableTemplateelementByKey(templateKey);
        final MembersDefinition membersDef = new MembersDefinition(attributeHandler.getType(), templateElt);
        current.addMembersDefinition(membersDef);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding members definition " +  membersDef
                    + " to the group: " + current.getPath());
        }
    }

    /**
     * Handles a membership definition.
     * @throws UnknownTemplateElementTempateElement If the definition of the membership refers to
     * an undifined template elment.
     * @throws SAXException If an error occures during the parsing process.
     */
    protected void handleMembership() throws UnknownTemplateElementTempateElement, SAXException {

        // Checks that the current definition denotes a group.
        if (current.isFolder()) {
            handleAttributeError(MEMBER_OF_TAG, SGSAttributeHandler.PATH_ATTR,
                    "!!! Not allowed for a folder", null);
        }

        // Cheks the path this group is member of.
        if (attributeHandler.getPath() == null || "".equals(attributeHandler.getPath())) {
            handleAttributeError(MEMBER_OF_TAG, SGSAttributeHandler.PATH_ATTR);
        }

        // Handles relative paths.
        final String resolvedPath = resolvePath(MEMBER_OF_TAG, attributeHandler.getPath());
        current.addContainingGroupPath(resolvedPath, attributeHandler.getConditionGroupExt());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding containing group path " +  resolvedPath + "with condition " + attributeHandler.getConditionGroupExt()
                    + " to the group: " + current.getPath());
        }
    }

    /**
     * Handles the definition of a (template) group or (template) folder.
     * @param localName the local name (without prefix), or the
     * empty string if Namespace processing is not being
     * performed.
     * @throws SAXParseException If there is a parsing error.
     * @throws UnknownTemplateElementTempateElement If the definition use an undefined template element.
     */
    protected void handleGroupOrFolder(final String localName)
    throws SAXParseException, UnknownTemplateElementTempateElement {

        // Checks that the attributes are valid.
        if ("".equals(attributeHandler.getExtension())) {
            handleAttributeError(localName, SGSAttributeHandler.EXT_ATTR, "", attributeHandler.getExtension());
        }
        if ("".equals(attributeHandler.getDisplayExtension())) {
            handleAttributeError(localName, SGSAttributeHandler.DISP_EXT_ATTR, "",
                    attributeHandler.getExtension());
        }
        if ("".equals(attributeHandler.getDescription())) {
            handleAttributeError(localName, SGSAttributeHandler.DESC_ATTR, "", attributeHandler.getExtension());
        }

        final boolean isFolder = FOLDER_TAG.equals(localName) || FOLDER_TEMPL_TAG.equals(localName);

        current = new GroupOrFolderDefinition(isFolder,
                attributeHandler.getPreexisting(),
                attributeHandler.getCreate(),
                currentPath,
                attributeHandler.getExtension(),
                attributeHandler.getDisplayExtension(),
                attributeHandler.getDescription());

        // Adds the recursive administrating path if needed.
        for (PrivilegeDefinition recPrivilege : recPrivileges) {
            current.addPrivilege(recPrivilege);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Adding recursive privilege: "
                        + recPrivilege + " to the group or folder: "
                        + current.getPath());
            }
        }

        // Registers the defintion to the  defintion manager.
        definitionsManager.registerDefinition(current);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Folder creation: " + current);
        }

        if (isFolder) {
            currentPath = current.getPath();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("New current path " + currentPath);
            }
        }
    }

    /**
     * Recieves the notification of the start of an element.
     * @param uri the Namespace URI, or the empty string if the
     * element has no Namespace URI or if Namespace
     * processing is not being performed.
     * @param localName the local name (without prefix), or the
     * empty string if Namespace processing is not being
     * performed.
     * @param qName the qualified name (with prefix), or the
     * empty string if qualified names are not available.
     * @param atts the attributes attached to the element.  If
     * there are no attributes, it shall be an empty
     * Attributes object.  The value of this object after
     * startElement returns is undefined.
     * @throws org.xml.sax.SAXException any SAX exception, possibly
     * wrapping another exception.
     * @see org.xml.sax.ContentHandler
     * #startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri,
            final String localName,
            final String qName,
            final Attributes atts) throws SAXException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Opening TAG = " + localName);
        }

        attributeHandler.reset();
        attributeHandler.handleAttributes(locator, atts);
        try {
            if (USER_TAG.equals(localName)) {
                // User for the Grouper sessions.
                LOGGER.debug("The user " + attributeHandler.getUid()
                        + " will be used for the Groupers sessions.");
                grouperSessionUtil.setSubjectId(attributeHandler.getUid());


            } else if (DEL_EMPTY_FOLD_TAG.equals(localName)) {

                // Deletion of the empty folders.
                LOGGER.debug("Setting delete empty folders to: " + attributeHandler.getValue());
                grouperDAO.setDeleteEmptyFolders(attributeHandler.getValue());

            } else if (DEL_EMPTY_GROUPS_TAG.equals(localName)) {

                // Deletion of the empty groups.
                LOGGER.debug("Setting delete empty groups to: " + attributeHandler.getValue());
                grouperDAO.setDeleteEmptyGroups(attributeHandler.getValue());

            } else if (FORCE_PRIV_TAG.equals(localName)) {

                // Force privileges flag.
                LOGGER.debug("Setting force privileges to: " + attributeHandler.getValue());
                grouperDAO.setForcePrivileges(attributeHandler.getValue());

            } else if (TEMPLATE_ELT_TAG.equals(localName)) {

                // Definition of a template element.
                handleTemplateElement();

            } else if (PRIVILEGE_TAG.equals(localName)) {

                // Definition of an administration privilege.
                handlePrivileges();

            } else if (MEMBERS_TAG.equalsIgnoreCase(localName)) {
                // Definition of the members of a group.
                handleMembersRule();
            } else if (MEMBER_OF_TAG.equals(localName)) {

                // Definition of a membership of a group.
                handleMembership();
            } else if (FOLDER_TAG.equals(localName) || FOLDER_TEMPL_TAG.equals(localName)
                    || GROUP_TAG.equals(localName) || GROUP_TEMPL_TAG.equals(localName)) {

                // Definition of a group, a template group, a folder or a template folder.
                handleGroupOrFolder(localName);
            }
        } catch (UnknownTemplateElementTempateElement e) {

            // Error on a template element.
            final String msg = "Error while parsing a string with template element - Tag: "
                + localName + " - line: " + locator.getLineNumber();
            LOGGER.fatal(msg);
            throw new SAXParseException(msg, locator, e);
        }
    }

    /**
     * Handles an attribute error.
     * @param tag The current tag.
     * @param attribute The attribute.
     * @throws SAXParseException The thrown exception.
     */
    private void handleAttributeError(final String tag,
            final String attribute) throws SAXParseException {
        handleAttributeError(tag, attribute, "", null);
    }

    /**
     * Handles an attribute error.
     * @param tag The current tag.
     * @param attribute The attribute.
     * @param legalValues The legal values for the attribute.
     * @param actualValue The actual value of the attribute.
     * @throws SAXParseException The thrown exception.
     */
    private void handleAttributeError(final String tag,
            final String attribute,
            final Object[] legalValues,
            final String actualValue) throws SAXParseException {
        String legalValuesStr = null;
        if (legalValues != null) {
            legalValuesStr = Arrays.toString(legalValues);
        }
        handleAttributeError(tag, attribute, legalValuesStr, actualValue);
    }

    /**
     * Handles an attribute error.
     * @param tag The current tag.
     * @param attribute The attribute.
     * @param legalValues The string that represents the legal values for the attribute.
     * @param actualValue The actual value of the attribute.
     * @throws SAXParseException The thrown exception.
     */
    private void handleAttributeError(final String tag,
            final String attribute,
            final String legalValues,
            final String actualValue) throws SAXParseException {
        String msg = "Tag: " + tag
        + " - Path of the tag: " + current.getPath()
        + " - Invalid value for the attribute {" + attribute + "}"
        + " (line " + locator.getLineNumber() + ")";

        if (legalValues != null && !"".equals(legalValues)) {
            msg += " - Legal values are: " + legalValues;
        }

        if (actualValue != null) {
            msg += " - Actual value is: " + actualValue;
        }
        msg +=  ".";

        LOGGER.error(msg);
        throw new SAXParseException(msg, locator);
    }

    /**
     * Resolves an external entity.
     * @param publicId The public identifer, or null if none is available.
     * @param systemId The system identifier provided in the XML document.
     * @return The new input source.
     * @throws SAXException
     * @throws IOException
     * @see org.xml.sax.helpers.DefaultHandler#resolveEntity(java.lang.String, java.lang.String)
     */
    @Override
    public InputSource resolveEntity(final String publicId, final String systemId)
    throws IOException, SAXException {

        if (systemId == null) {
            return null;
        }
        if (systemId.startsWith("classpath:") && systemId.endsWith(".dtd")) {
            final String dtdFile = systemId.substring(systemId.indexOf(':') + 1);
            LOGGER.debug("Try to load the DTD from the classpath: " + dtdFile + ".");
            final InputStream dtdIS = getClass().getClassLoader().getResourceAsStream(dtdFile);
            if (dtdIS == null) {
                LOGGER.fatal("Unable to load the DTD from the classpath: " + dtdFile);
            }
            return new InputSource(dtdIS);
        }
        return super.resolveEntity(publicId, systemId);
    }

    /**
     * Starts the parsing process.
     * @throws SAXException If there is an error during the parsing (invalid group defintion for instance).
     * @throws IOException  If there is an IO error.
     */
    public void parse() throws IOException, SAXException {

        XMLReader saxReader = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        saxReader.setFeature("http://xml.org/sax/features/validation", true);
        saxReader.setContentHandler(this);
        saxReader.setErrorHandler(this);
        saxReader.setEntityResolver(this);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("---------------------------------");
            LOGGER.info("Parsing definition file: ");
            LOGGER.info(definitionsFileURI);
            LOGGER.info("---------------------------------");
        }
        InputStream iStream = getClass().getClassLoader().getResourceAsStream(definitionsFileURI);

        if (iStream == null) {
            LOGGER.fatal("Unable to load (from classpath) file: " + definitionsFileURI + ".");
        }


        //final InputSource is =
        saxReader.parse(new InputSource(iStream));

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("---------------------------------");
            LOGGER.info("Definition file parsed.");
            LOGGER.info("---------------------------------");
        }
    }

    /**
     * Getter for definitionsManager.
     * @return definitionsManager.
     */
    public GroupOrFolderDefinitionsManager getDefinitionsManager() {
        return definitionsManager;
    }

    /**
     * Setter for definitionsManager.
     * @param definitionsManager the new value for definitionsManager.
     */
    public void setDefinitionsManager(final GroupOrFolderDefinitionsManager definitionsManager) {
        this.definitionsManager = definitionsManager;
    }

    /**
     * Getter for definitionsFileURI.
     * @return definitionsFileURI.
     */
    public String getDefinitionsFileURI() {
        return definitionsFileURI;
    }

    /**
     * Setter for definitionsFileURI.
     * @param definitionsFileURI the new value for definitionsFileURI.
     */
    public void setDefinitionsFileURI(final String definitionsFileURI) {
        this.definitionsFileURI = definitionsFileURI;
    }

    /**
     * Getter for grouperSessionUtil.
     * @return grouperSessionUtil.
     */
    public GrouperSessionUtil getGrouperSessionUtil() {
        return grouperSessionUtil;
    }

    /**
     * Setter for grouperSessionUtil.
     * @param grouperSessionUtil the new value for grouperSessionUtil.
     */
    public void setGrouperSessionUtil(final GrouperSessionUtil grouperSessionUtil) {
        this.grouperSessionUtil = grouperSessionUtil;
    }

    /**
     * Getter for grouperDAO.
     * @return grouperDAO.
     */
    public GrouperDAO getGrouperDAO() {
        return grouperDAO;
    }

    /**
     * Setter for grouperDAO.
     * @param grouperDAO the new value for grouperDAO.
     */
    public void setGrouperDAO(final GrouperDAO grouperDAO) {
        this.grouperDAO = grouperDAO;
    }


}
