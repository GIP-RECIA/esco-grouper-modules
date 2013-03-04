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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.groups.ComponentGroupServiceDescriptor;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntityGroupStore;
import org.jasig.portal.groups.IEntityGroupStoreFactory;

/**
 * Factory for the ESCORDBMEntityStore.
 * @author GIP RECIA - A. Deman
 * 10 mars 08
 *
 */
public class ESCOReferenceEntityGroupStoreFactory implements IEntityGroupStoreFactory {

    /** Logger. */
    private static final Log LOGGER = LogFactory.getLog(ESCOReferenceEntityGroupStoreFactory.class);

    /** File used to load the grouper groups. */
    private static final String ESCO_GROUP_LOAD = "properties/groups/esco-GroupLoad.xml";

    /** Key to retrieve the grouper request cache duration. */
    private static final String CACHE_DURATION_KEY = "grouper.requests.cache.duration";

    /** groups load information. */
    private static Properties grouperMapping;




    /**
     * Constructor for ESCOReferenceEntityGroupStoreFactory.
     */
    public ESCOReferenceEntityGroupStoreFactory() {
        super();

        // Loads the mapping for the Grouper groups.
        if (grouperMapping == null) {
            grouperMapping = new Properties();
            try {
                final ClassLoader cl = getClass().getClassLoader();
                final InputStream is  = cl.getResourceAsStream(ESCO_GROUP_LOAD);
                if (is == null) {
                    LOGGER.error("Error loading (from classpath) " + ESCO_GROUP_LOAD);
                }
                grouperMapping.loadFromXML(is);

                // Handles the grouper request cache if needed.
                final String cacheDurationValue = grouperMapping.getProperty(CACHE_DURATION_KEY);
                if (cacheDurationValue != null) {
                    try {
                        final long newCacheDuration = Long.parseLong(cacheDurationValue);
                        grouperMapping.remove(CACHE_DURATION_KEY);
                        ESCODynEntityGroupDecorator.initializeCacheDuration(newCacheDuration);
                        ESCOEntityGroupFactory.instance().initializeCacheDuration(newCacheDuration);

                    } catch (NumberFormatException e) {
                        LOGGER.warn("Error while setting the grouper requests cache."
                                + "Property file: " + ESCO_GROUP_LOAD
                                + " - property: " + CACHE_DURATION_KEY
                                + " - value: " + cacheDurationValue);
                    }

                    // Initialization of the factory in order to be able to retrieve uPortal memberships too.
                    ESCOEntityGroupFactory.instance().initializeMapping(grouperMapping);

                }
            } catch (IOException e) {
                LOGGER.error(e, e);
            } catch (NullPointerException e) {
                LOGGER.error(e, e);
            }
        }

        if (grouperMapping == null) {
            LOGGER.error("Unable to load " + ESCO_GROUP_LOAD);
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Creation of an instance of ");
            sb.append(getClass().getName());
            sb.append(" - mapping ");
            sb.append(grouperMapping);
            LOGGER.debug(sb);
        }
    }

    /**
     * Return an instance of the group store implementation.
     * @return IEntityGroupStore
     * @exception GroupsException
     */
    public IEntityGroupStore newGroupStore() throws GroupsException {
        return new ESCORDBMEntityGroupStore(grouperMapping);
    }

    /**
     * Return an instance of the group store implementation.
     * @param svcDescriptor
     * @return IEntityGroupStore
     * @exception GroupsException
     */
    public IEntityGroupStore newGroupStore(final ComponentGroupServiceDescriptor svcDescriptor)
    throws GroupsException {
        return newGroupStore();
    }

}
