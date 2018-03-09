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
package org.esco.grouper.domain.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.esco.grouper.cache.SGSCache;

/**
 * Manager for the groups or folders definitions.
 * The Evaluation of the templates is performed in this class.
 * @author GIP RECIA - A. Deman
 * 31 juil. 08
 *
 */
public class GroupOrFolderDefinitionsManager implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 3388637657107893254L;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(GroupOrFolderDefinition.class);

    /** Preexisting definitions. */
    private Map<String, GroupOrFolderDefinition> preexistingDefinitnonsByPath =
        new HashMap<String, GroupOrFolderDefinition>();

    /** Definitions to create which are not template. */
    private List<GroupOrFolderDefinition> gofToCreate = new Vector<GroupOrFolderDefinition>();

    /** Definitions to create which are template. */
    private List<GroupOrFolderDefinition> gofTemplateToCreate = new Vector<GroupOrFolderDefinition>();

    /** All the groups and folder definitions, by path. */
    private Map<String, GroupOrFolderDefinition> definitionsByPath =
        new HashMap<String, GroupOrFolderDefinition>();

    /** Stores the group or folder definition by members definition.*/
    private Map<MembersDefinition, List<GroupOrFolderDefinition>> definitionsByMembersDefinitions;

    /** The Cache for the Group Service. */
    private SGSCache cache = SGSCache.instance();

    /**
     * Builds an instance of GroupOrFolderDefinitionsManager.
     */
    public GroupOrFolderDefinitionsManager() {
        LOGGER.info("Initialization of the manager for the groups or folders defintions");
    }

    /**
     * Stores a group or folder definition.
     * @param definition The group or folder definition to store.
     */
    public void registerDefinition(final GroupOrFolderDefinition definition) {
        final String path = definition.getPath();
        definitionsByPath.put(path, definition);
        if (definition.isPreexisting())  {
            preexistingDefinitnonsByPath.put(definition.getPath(), definition);
        } else if (definition.isCreate()) {
            if (definition.isTemplate()) {
                gofTemplateToCreate.add(definition);
            } else {
                gofToCreate.add(definition);
            }
        }
    }

    /**
     * Gives the groups or folders definitions to create event if they have no members.
     * @return The Iterator over the groups or folder definitions.
     */
    public Iterator<GroupOrFolderDefinition> getGroupsOrFoldersToCreate() {
        return gofToCreate.iterator();
    }

    /**
     * Gives the groups or folders templates to create event if they have no members.
     * @param values the values used for the template evaluation.
     * @return The Iterator over the groups or folder templates.
     */
    public Iterator<GroupOrFolderDefinition> getGroupsOrFoldersTemplatesToCreate(final String...values) {
        Set<GroupOrFolderDefinition> definitions = new HashSet<GroupOrFolderDefinition>(gofTemplateToCreate.size());
        for (GroupOrFolderDefinition definition : gofTemplateToCreate) {
            definitions.add(definition.evaluateTemplate(values));
        }
        return definitions.iterator();
    }

    /**
     * Returns an iterator over the preexisting definitions.
     * @return An iterator over the preexisting definitions
     * of groups or folders.
     */
    public Iterator<GroupOrFolderDefinition> preexistingDefinitions() {
        return preexistingDefinitnonsByPath.values().iterator();
    }

    /**
     * Tests if a path denotes a preexisting definition.
     * @param path The path to test.
     * @return True if the path is associated to a preexisting definition.
     */
    public boolean isPreexistingDefinition(final String path) {
        return preexistingDefinitnonsByPath.containsKey(path);
    }

    /**
     * Gives the groups defined as templates for a type of members
     * and the values of some attributes.
     * @param type The type of the member of the groups.
     * @param attributes The attributes of the member of the groups which are used
     * for the templates evaluations.
     * @return An iterator over the groups.
     */
    public Iterator<GroupOrFolderDefinition> getMembershipsForTemplates(final PersonType type,
            final String...attributes) {

        if (definitionsByMembersDefinitions == null) {
            initilizeDefinitionsByTypeOfMembers();
        }

        // Tries to retrieves the memebrships from the cache.
        Set<GroupOrFolderDefinition> memberships = cache.getMembershipsForTemplates(type, attributes);
        if (memberships != null) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Memberships for templates retrieved from cache for type " + type
                        + ": " + Arrays.toString(attributes) + ": "
                        + memberships);
            }
            return memberships.iterator();
        }

        // The memberships has to be evaluated.
        memberships = new HashSet<GroupOrFolderDefinition>();
        final Iterator<GroupOrFolderDefinition> defsIt = getMemberships(type, attributes);


        while (defsIt.hasNext()) {

            final GroupOrFolderDefinition def = defsIt.next();

            // Only template definitions are handled.
            if (def.isTemplate()) {
                memberships.add(def.evaluateTemplate(attributes));
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Memberships for template groups evaluated for " + type
                    + " " + Arrays.toString(attributes) + ": "
                    + memberships);
        }

        cache.cacheMemberships(memberships, type, attributes);

        return memberships.iterator();
    }

    /**
     * Gives the groups for a type and The values of some attributes.
     * @param type The type of the member of the groups.
     * @param attributes The attributes of the member of the groups which are used
     * for the templates evaluations.
     * @return An iterator over the groups.
     */
    public Iterator<GroupOrFolderDefinition> getMemberships(final PersonType type,
            final String...attributes) {

        if (definitionsByMembersDefinitions == null) {
            initilizeDefinitionsByTypeOfMembers();
        }



        // Tries to retrieves the memebrships from the cache.
        Set<GroupOrFolderDefinition> memberships = cache.getMemberships(type, attributes);
        if (memberships != null) {

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Memberships retrieved from cache for type " + type
                        + ": " + Arrays.toString(attributes) + ": "
                        + memberships);
            }
            return memberships.iterator();
        }

        // The memberships has to be evaluated.
        memberships = new HashSet<GroupOrFolderDefinition>();

        // Builds the possible members definitions to use as key for retrieving
        // the effectively defined ones.
        final List<MembersDefinition> mbDefs = new ArrayList<MembersDefinition>();
        mbDefs.add(new MembersDefinition(type));
        final int nbElts = Math.min(attributes.length, TemplateElement.countAvailableTemplateElements());
        for (int i = 0; i < nbElts; i++) {
            // The corresponding attribute to a template element is not null or there is
            // a default value.
            if (!"".equals(attributes[i]) || TemplateElement.getAvailableTemplateElement(i).hasDefaultValue()) {
                mbDefs.add(new MembersDefinition(type, TemplateElement.getAvailableTemplateElement(i)));
            }
        }


        for (MembersDefinition mbDef : mbDefs) {
            final List<GroupOrFolderDefinition> gofDefs = definitionsByMembersDefinitions.get(mbDef);

            if (gofDefs == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No memberships for members of type " + mbDef + ".");
                }
            } else {
                for (GroupOrFolderDefinition globalDef : gofDefs) {

                    if (!globalDef.isTemplate()) {
                        memberships.add(globalDef);
                    } else {
                        memberships.add(globalDef.evaluateTemplate(attributes));
                    }
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Memberships evaluated for " + mbDef
                            + " " + Arrays.toString(attributes) + ": "
                            + memberships);
                }
            }
        }
        cache.cacheMemberships(memberships, type, attributes);

        return memberships.iterator();
    }

    /**
     * Retrieves a definition by the path of the group or folder.
     * @param path The path of the group or folder.
     * @param values The values used to evaluate templates.
     * @return The group or folder definition.
     */
    public GroupOrFolderDefinition getDefinition(final String path, final String...values) {
        final GroupOrFolderDefinition def = definitionsByPath.get(path);
        if (def.isTemplate()) {
            return def.evaluateTemplate(values);
        }
        return def;
    }

    /**
     * Initializes the map of the groups definition by type of members.
     */
    protected void initilizeDefinitionsByTypeOfMembers() {

        definitionsByMembersDefinitions = new HashMap<MembersDefinition, List<GroupOrFolderDefinition>>();

        for (GroupOrFolderDefinition gofd : definitionsByPath.values()) {

            // Stores the definition by type of subject for their members
            // definition.
            for (int i = 0; i < gofd.countMembersDefinitions(); i++) {

                List<GroupOrFolderDefinition> defs = definitionsByMembersDefinitions.get(gofd.getMembersDefiniton(i));

                if (defs == null) {
                    defs = new ArrayList<GroupOrFolderDefinition>();
                    definitionsByMembersDefinitions.put(gofd.getMembersDefiniton(i), defs);
                }
                defs.add(gofd);
            }
        }
    }

    /**
     * Checks the paths references.
     * @return The List of error messages (empty if there is no error).
     */
    public List<String> checksPathsReferences() {

        // Error messages.
        final List<String> errorsMsg = new ArrayList<String>();

        // Browses all the definition to checks the path.
        for (GroupOrFolderDefinition gofd : definitionsByPath.values()) {

            String typeOfDef;
            if (gofd.isFolder()) {
                typeOfDef = "folder";
            } else {
                typeOfDef = "group";
            }

            // Checks the administrating groups paths
            for (int i = 0; i < gofd.countPrivileges(); i++) {
                final String path = gofd.getPrivilege(i).getPath().getString();
                final GroupOrFolderDefinition ref = definitionsByPath.get(path);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Finilizing definition for " + gofd.getPath() + ".");
                }

                if (ref == null) {
                    // The path referes to a group that is not defined.
                    final String msg = "Privileges definition - Invalid group path for the " + typeOfDef + ": "
                    + gofd.getPath() + " - Can't find a group definition for the group path: "
                    + path + ".";
                    errorsMsg.add(msg);
                } else if (ref.isFolder()) {
                    // The administrating path denotes a folder.
                    final String msg = "Privileges definition - Invalid group path for the " + typeOfDef + ": "
                    + gofd.getPath() + " - It should be a group definition but it is a FOLDER defintion: "
                    + path + ".";
                    errorsMsg.add(msg);
                }
            }

            // Checks the containing groups paths
            if (gofd.isFolder()) {
                // A folder can'be a member of a group
                if (gofd.countContainingGroupsPaths() > 0) {
                    final String msg = "The folder: " + gofd.getPath()
                    + " can't be member of a group (only groups can).";
                    errorsMsg.add(msg);

                }
            } else {

                for (int i = 0; i < gofd.countContainingGroupsPaths(); i++) {
                    final String containingPath = gofd.getContainingGroupPath(i).getEvaluableString().getString();
                    final GroupOrFolderDefinition ref = definitionsByPath.get(containingPath);

                    if (ref == null) {
                        // The path referes to a group that is not defined.
                        final String msg = "Invalid containing group path for the group: "
                            + gofd.getPath() + " - Can't find a group definition for the containing group path: "
                            + containingPath + ".";
                        errorsMsg.add(msg);
                    } else if (ref.isFolder()) {
                        // The administrating path denotes a folder.
                        final String msg = "Invalid containing group path for the group: "
                            + gofd.getPath() + " - It should be a group definition but it is a FOLDER defintion: "
                            + containingPath + ".";
                        errorsMsg.add(msg);
                    } else if (gofd.equals(ref)) {

                        // The administrating path denotes the same group.
                        final String msg = "The containing group path for the group: "
                            + gofd.getPath() + " references to itself.";
                        errorsMsg.add(msg);
                    }
                }
            }
        }
        return errorsMsg;
    }
}

