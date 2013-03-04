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


package org.esco.portal.groups.grouper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.esco.grouper.domain.beans.GrouperDTO;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.groups.IGroupConstants;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.services.GroupService;

/**
 * @author GIP RECIA - A. Deman
 * 17 mars 08
 *
 */
public class ESCOEntityGroupFactory {

    /** Logger. */
    private static final Log LOGGER = LogFactory.getLog(ESCOEntityGroupFactory.class);


    /** Empty group lit. */
    private static final List<IEntityGroup> EMPTY_GROUP_LIST = new ArrayList<IEntityGroup>();

    /** Singleton. */
    private static final ESCOEntityGroupFactory INSTANCE = new ESCOEntityGroupFactory();

    /** The grouper mapping to use. */
    private Properties grouperMapping = new Properties();

    /** uPortal groups (where grouper groups are loaded into) by grouper group or stems.  */
    private Map<String, IEntityGroup> uPortalAncestorsForGrouper = new HashMap<String, IEntityGroup>();

    /**
     * Constructor for ESCOEntityGroupFactory.
     */
    protected ESCOEntityGroupFactory() {
        /* protected */
    }

    /**
     * Initialization of the cache duration.
     * @param newCacheDuration The new value in seconds for the validity of the grouper
     * requests.
     */
    public void initializeCacheDuration(final long newCacheDuration) {
        ESCOEntityGroupImpl.initializeCacheDuration(newCacheDuration);
    }

    /**
     * Initialization of the mapping.
     * @param newGrouperMapping The grouper mapping, where keys are uPortalGroups
     * and values comma separated list of Grouper stems.
     */
    public void initializeMapping(final Properties newGrouperMapping) {
        this.grouperMapping = newGrouperMapping;
        for (Object keyObj : newGrouperMapping.keySet()) {
            final String key = (String) keyObj;
            EntityIdentifier[] ids = GroupService.getCompositeGroupService().searchForGroups(key,
                    IGroupConstants.IS, IPerson.class);

            if (ids.length == 0) {
                // Error: no uPortal group found.
                final StringBuffer sb = new StringBuffer("Error during grouper mapping initialization. ");
                sb.append("The mapping used to load Grouper groups is: ");
                sb.append(newGrouperMapping);
                sb.append(". No uPortal group found for: ");
                sb.append(key);
                LOGGER.error(sb);
            } else if (ids.length > 1) {
                //  Error: more than one uPortal group found.
                final StringBuffer sb = new StringBuffer("Error during grouper mapping initialization. ");
                sb.append("The mapping used to load Grouper groups is: ");
                sb.append(newGrouperMapping);
                sb.append(". Too much uPortal groups found for: ");
                sb.append(key);
                sb.append(" => ");
                sb.append(Arrays.toString(ids));
                LOGGER.error(sb);
            } else {
                // Valid case: one uPortal group found.
                final IEntityGroup group = GroupService.findGroup(ids[0].getKey());
                if (group != null) {
                    final String stemsList = grouperMapping.getProperty(key);
                    if (stemsList == null) {
                        LOGGER.error("Error while retrieving mapping (stems list) for: " + key);
                    } else {
                        final String[] stems = stemsList.split(ESCOConstants.SEP);
                        for (String stem : stems) {
                            uPortalAncestorsForGrouper.put(stem, group);
                        }
                    }

                } else {
                    final StringBuffer sb = new StringBuffer("Error during grouper mapping initialization. ");
                    sb.append("The mapping used to load Grouper groups is: ");
                    sb.append(newGrouperMapping);
                    sb.append(". Unable to find IentityGroup for id " + ids[0]);
                    LOGGER.error(sb);
                }
            }

            if (LOGGER.isDebugEnabled()) {
                final StringBuffer sb = new StringBuffer("Group Mapping initialization. The mapping used is ");
                sb.append(newGrouperMapping);
                sb.append(" Thre retrived uPortal groups are: ");
                sb.append(uPortalAncestorsForGrouper);
                LOGGER.debug(sb);
            }

        }
    }


    /**
     * Gives the singleton.
     * @return The available instance.
     */
    public static ESCOEntityGroupFactory instance() {
        return INSTANCE;
    }

    /**
     * Creates an entity group from a group description.
     * @param groupInfo The group description.
     * @return The entity group instance.
     */
    public IEntityGroup createEntityGroup(final GrouperDTO groupInfo) {


        final ESCOEntityGroupImpl group =  new ESCOEntityGroupImpl(groupInfo);

        // Loads the uPortal groups if needed.
        for (String groupOrStem : uPortalAncestorsForGrouper.keySet()) {
            if (groupInfo.getKey().equals(groupOrStem)) {
                IEntityGroup uPortalGroup = uPortalAncestorsForGrouper.get(groupOrStem);
                group.loadDinamicallyIntoUportalGroup(uPortalGroup);
            }
        }

        return group;
    }

    /**
     * Creates entity groups from group descriptions.
     * @param groupInfos The group descriptions.
     * @return The list of entity group.
     */
    public List<IEntityGroup> createEntityGroups(final GrouperDTO[] groupInfos) {
        if (groupInfos.length == 0) {
            return EMPTY_GROUP_LIST;
        }
        final List<IEntityGroup> result = new ArrayList<IEntityGroup>(groupInfos.length);
        for (GrouperDTO gInfo : groupInfos) {
            result.add(createEntityGroup(gInfo));
        }
        return result;
    }

}
