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

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.groups.ComponentGroupServiceDescriptor;
import org.jasig.portal.groups.EntityGroupImpl;
import org.jasig.portal.groups.GroupMemberImpl;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.groups.ReferenceIndividualGroupService;

/**
 * Extends the ReferenceIndividualGroupService in order to handle the down cast issues.
 * @author GIP RECIA - A. Deman
 * 20 mai 08
 *
 */
public class ESCOReferenceIndividualGroupService extends ReferenceIndividualGroupService {

	private static final Log log = LogFactory.getLog(ESCOReferenceIndividualGroupService.class);

    /**
     * ESCOReferenceIndividualGroupService constructor.
     */
    public ESCOReferenceIndividualGroupService() {
        super();
    }

    /**
     * ESCOReferenceIndividualGroupService constructor.
     * @param svcDescriptor
     * @throws GroupsException

     */
    public ESCOReferenceIndividualGroupService(final ComponentGroupServiceDescriptor svcDescriptor)
    throws GroupsException {
        super(svcDescriptor);
    }


    /**
     * Remove the back pointers of the group members of the deleted group.  Then
     * update the cache to invalidate copies on peer servers.
     *
     * @param group ILockableEntityGroup
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void synchronizeGroupMembersOnDelete(final IEntityGroup group) throws GroupsException {
        GroupMemberImpl gmi = null;

        if (group instanceof EntityGroupImpl) {
            for (Iterator it = group.getMembers(); it.hasNext();) {
                final Object next = it.next();
                if (next instanceof GroupMemberImpl) {
                    gmi = (GroupMemberImpl) next;
                    gmi.removeGroup(group);
                    if (cacheInUse()) {
                        cacheUpdate(gmi);
                    }
                }
            }
        }
    }

    /**
     * Overrides the ancestor method to address  a down cast issue.
     * Adjust the back pointers of the updated group members to either add or remove
     * the parent group.  Then update the cache to invalidate copies on peer servers.
     * @param group ILockableEntityGroup
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void synchronizeGroupMembersOnUpdate(final IEntityGroup group) throws GroupsException {
        if (group instanceof EntityGroupImpl) {
            EntityGroupImpl egi = (EntityGroupImpl) group;
            GroupMemberImpl gmi = null;

            for (Iterator it = egi.getAddedMembers().values().iterator(); it.hasNext();) {
                final Object next = it.next();
                if (next instanceof GroupMemberImpl) {
                    gmi = (GroupMemberImpl) next;
                    gmi.addGroup(egi);
                    if (cacheInUse()) {
                        cacheUpdate(gmi);
                    }
                }
            }

            for (Iterator it = egi.getRemovedMembers().values().iterator(); it.hasNext();) {
                final Object next = it.next();
                if (next instanceof GroupMemberImpl) {
                    gmi = (GroupMemberImpl) next;
                    gmi.removeGroup(egi);
                    if (cacheInUse()) {
                        cacheUpdate(gmi);
                    }
                }
            }
        }
    }

}
