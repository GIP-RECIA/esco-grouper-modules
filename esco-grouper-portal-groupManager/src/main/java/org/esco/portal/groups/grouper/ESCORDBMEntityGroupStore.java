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

import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.groups.EntityGroupImpl;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.groups.RDBMEntityGroupStore;

/**
 * Adapter of the uPortal RDBMEntityGroupStore used to add some Grouper groups
 * as subgroups.
 * This Group store works with a map which associates some uPortal groups
 * to one or several stems in grouper.
 * When an uPortal group is associated to a grouper stems a decorator is used to
 * extends the reference implementation of EntityGroup in order to retrieve the grouper
 * groups from the stem.
 *
 * @author GIP RECIA - A. Deman
 * 10 mars 08
 *
 */
public class ESCORDBMEntityGroupStore extends RDBMEntityGroupStore {

    /** Logger. */
    //private static final Log LOGGER = LogFactory.getLog(EntityGroupStoreFactory.class);
	private static final Log LOGGER = LogFactory.getLog(ESCORDBMEntityGroupStore.class);

    /** Groups load information.  keys are uPortal group names, values are comma separated lists of
     * grouper stems.*/
    private Properties grouperMapping;

    /**
     * Constructor for ESCORDBMEntityGroupStore.
     * @param grouperMapping The mapping describing how the groups are loaded.
     */
    public ESCORDBMEntityGroupStore(final Properties grouperMapping) {
        this.grouperMapping = grouperMapping;
        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Creation of ");
            sb.append(getClass().getSimpleName());
            LOGGER.debug(sb);
        }
    }

    /**
     * Find and return an instance of the group.
     * @param groupID the group ID
     * @return org.jasig.portal.groups.IEntityGroup
     */
    @Override
    public IEntityGroup find(final String groupID) throws GroupsException {
        return decorate(super.find(groupID));
    }

    /**
     * Decorates a group if needed.
     * @param group The group to decorate.
     * @return The decorated group if some grouper grouper groups have to be loaded in it.
     */
    private IEntityGroup decorate(final IEntityGroup group) {

        if (group == null) {
            return group;
        }

        final String groupName = group.getName();
        String eltsToLoad = null;

        if (groupName != null) {
            eltsToLoad = grouperMapping.getProperty(groupName);
        }

        // Retrieves the name of the Grouper groups to load.
        String[] grouperGroups = null;

        if (eltsToLoad != null) {
	            grouperGroups = eltsToLoad.split(ESCOConstants.SEP);
        }
        final EntityGroupImpl decoratedGroup = new ESCODynEntityGroupDecorator(group.getKey(),
                group.getLeafType(),
                group.getCreatorID(),
                group.getName(),
                group.getDescription(),
                grouperGroups);

        return decoratedGroup;
    }

    /**
     * Decorates the groups associated to an iterator.
     * @param groups The iterator over the groups to decorate.
     * @return The iterator over the decorated groups.
     */
    private Iterator<IEntityGroup> decorate(@SuppressWarnings("unchecked") final Iterator groups) {
        final Set<IEntityGroup> decoratedGroups = new HashSet<IEntityGroup>(ESCOConstants.INITIAL_CAPACITY);
        while (groups.hasNext()) {
            final IEntityGroup groupToAdd = decorate((IEntityGroup) groups.next());
            decoratedGroups.add(groupToAdd);
        }
        return decoratedGroups.iterator();
    }

    /**
     * Find the containing groups for an entity and decorates them if needed.
     * @param ent The entity.
     * @return The (possibly) decorated groups containing the entity.
     * @throws GroupsException
     * @see org.jasig.portal.groups.RDBMEntityGroupStore#findContainingGroups(org.jasig.portal.groups.IEntity)
     */
    @Override
    public Iterator<IEntityGroup> findContainingGroups(final IEntity ent) throws GroupsException {
        return decorate(super.findContainingGroups(ent));
    }


    /**
     * Find the containing groups for a given group and decorates them if needed.
     * @param group The group.
     * @return The (possibly) decorated groups containing the group.
     * @throws GroupsException
     * @see org.jasig.portal.groups.RDBMEntityGroupStore#findContainingGroups(org.jasig.portal.groups.IEntity)
     */
    @Override
    public Iterator<IEntityGroup> findContainingGroups(final IEntityGroup group) throws GroupsException {
        return decorate(super.findContainingGroups(group));
    }


    /**
     * Find the IUserGroups that are members of the group.
     * If these groups are associated to grouper stems, then a decorator is used
     * to handle the subgroups retrieved from these stems.
     * @param group org.jasig.portal.groups.IEntityGroup
     * @return java.util.Iterator
     */
    @Override
    public Iterator<IEntityGroup> findMemberGroups(final IEntityGroup group) throws GroupsException {
        return decorate(super.findMemberGroups(group));
    }
}
