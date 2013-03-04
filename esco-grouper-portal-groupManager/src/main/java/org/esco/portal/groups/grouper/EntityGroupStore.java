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
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.esco.grouper.domain.beans.GrouperDTO;
import org.esco.grouper.services.GrouperWSClient;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.groups.EntityImpl;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.groups.IEntityGroupStore;
import org.jasig.portal.groups.IGroupMember;
import org.jasig.portal.groups.ILockableEntityGroup;
import org.jasig.portal.security.IPerson;

/**
 * Class used to retrieve implementation of IEntityGroup based on Internet2 grouper.
 * The Communication with grouper is performed via a web service.
 * @author GIP RECIA - A. Deman
 * 28 nov. 07
 *
 */
public class EntityGroupStore implements IEntityGroupStore {

	/** Logger. */
	//private static final Log LOGGER = LogFactory.getLog(EntityGroupStoreFactory.class);
	private static final Log LOGGER = LogFactory.getLog(EntityGroupStore.class);

	/** Web service used to access Grouper informations. */
	private static final GrouperWSClient GROUPER_WS = GrouperWSClient.instance();

	/**
	 * Package protected constructor.
	 * Constructor for EntityGroupStore.
	 */
	EntityGroupStore() { /* Package protected.*/ }

	/**
	 * Tests if a group contains a member.
	 * @param group The group to use.
	 * @param member The member to find in the group.
	 * @return True if the member is found in the group.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroupStore#contains
	 * (org.jasig.portal.groups.IEntityGroup, org.jasig.portal.groups.IGroupMember)
	 */
	public boolean contains(final IEntityGroup group, final IGroupMember member)
	throws GroupsException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Check if the group " + group.getName() + " contains member " + member.getKey());
		}
		@SuppressWarnings("unchecked")
		Iterator itr =  null;
		if (member.isGroup()) {
			itr = findMemberGroups(group);
		} else {
			itr = findEntitiesForGroup(group);
		}


		while (itr.hasNext()) {
			if (member.equals(itr.next())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * unsupported ooperation.
	 * @param group The group.
	 * @see org.jasig.portal.groups.IEntityGroupStore#delete(org.jasig.portal.groups.IEntityGroup)
	 */
	public void delete(final IEntityGroup group) {
		final StringBuffer sb = new StringBuffer(getClass().getSimpleName());
		sb.append(": Method delete() not supported.");
		throw new UnsupportedOperationException(sb.toString());
	}

	/**
	 * Find a Group.
	 * @param key The Full Qualified Name of the group to retrieve the group (e:g: etc:wheel)
	 * @return The Group if found null otherwise.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroupStore#find(java.lang.String)
	 */
	public IEntityGroup find(final String key) throws GroupsException {


		final GrouperDTO gd = GROUPER_WS.find(key);
		IEntityGroup group = null;
		if (gd != null) {
			group = ESCOEntityGroupFactory.instance().createEntityGroup(gd);
		}
		if (LOGGER.isDebugEnabled()) {
			final StringBuffer sb = new StringBuffer("Try to find group ");
			sb.append(key);
			sb.append(" => ");
			sb.append(gd);
			LOGGER.debug(sb.toString());
		}
		return group;
	}

	/**
	 * Returns an Iterator over the Collection of IEntityGroups that the IGroupMember belongs to.
	 * @param gm The considered group member.
	 * @return an iterator.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroupStore#findContainingGroups(org.jasig.portal.groups.IGroupMember)
	 */
	public Iterator<IEntityGroup> findContainingGroups(final IGroupMember gm) throws GroupsException {

		GrouperDTO[] groupsInfos = null;


		if (gm.isGroup()) {
			// Finds the groups that contains the group pointed by gm.
			groupsInfos = GROUPER_WS.getMembershipsForGroup(((IEntityGroup) gm).getLocalKey());
		} else {
			// Finds the groups that contains this entity pointed by gm.
			groupsInfos = GROUPER_WS.getMembershipsForSubjects(gm.getKey());
		}


		final List<IEntityGroup> groups = ESCOEntityGroupFactory.instance().createEntityGroups(groupsInfos);

		if (LOGGER.isDebugEnabled()) {

			final StringBuffer sb = new StringBuffer(">>Find Containing groups for ");
			if (gm.isEntity()) {
				sb.append(" entity ");
			} else {
				sb.append(" group ");
			}
			sb.append(gm.getKey());
			sb.append(": ");
			for (GrouperDTO info : groupsInfos) {
				sb.append(info.getKey());
				sb.append(" ");
			}
			LOGGER.debug(sb.toString());
		}

		return groups.iterator();
	}

	/**
	 * Iterator over the Collection of IEntities that are members of this IEntityGroup.
	 * @param group The considered group.
	 * @return An iterator through the members (i.e. leaves, not subgroups) of the group and its subgroups.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroupStore#findEntitiesForGroup(org.jasig.portal.groups.IEntityGroup)
	 */
	public Iterator<IEntity> findEntitiesForGroup(final IEntityGroup group)
	throws GroupsException {

		final GrouperDTO[] gInfos = GROUPER_WS.getMemberEntities(group.getLocalKey());

		final List<IEntity> members = new ArrayList<IEntity>(gInfos.length);
		for (GrouperDTO gInfo : gInfos) {
				members.add(new EntityImpl(gInfo.getKey(), IPerson.class));
		}


		if (LOGGER.isDebugEnabled()) {
			final StringBuffer sb = new StringBuffer("Entities for group ");
			sb.append(group.getLocalKey());
			sb.append(": ");

			sb.append(Arrays.toString(gInfos));
			sb.append(" ");
			LOGGER.debug(sb.toString());
		}

		return members.iterator();
	}

	/**
	 * Gives all the groups which are member of the group.
	 * @param group The group.
	 * @return An iterator through the groups which are members of the
	 * specified group/.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroupStore#findMemberGroups(org.jasig.portal.groups.IEntityGroup)
	 */
	public Iterator<IEntityGroup> findMemberGroups(final IEntityGroup group) throws GroupsException {
		GrouperDTO[] gInfos = GROUPER_WS.getMemberGroups(group.getLocalKey());

		final List<IEntityGroup> members = ESCOEntityGroupFactory.instance().createEntityGroups(gInfos);

		if (LOGGER.isDebugEnabled()) {
			final StringBuffer sb = new StringBuffer("Entities for group ");
			sb.append(group.getLocalKey());
			sb.append(": ");

			sb.append(Arrays.toString(gInfos));
			sb.append(" ");
			LOGGER.debug(sb.toString());
		}

		return members.iterator();

	}

	/**
	 * Unsupported method.
	 * @param key The key of the group.
	 * @return ILockableEntityGroup
	 * @see org.jasig.portal.groups.IEntityGroupStore#findLockable(java.lang.String)
	 */

	public ILockableEntityGroup findLockable(final String key) {
		final StringBuffer sb = new StringBuffer(getClass().getSimpleName());
		sb.append(": Method findLockable() not supported.");
		throw new UnsupportedOperationException(sb.toString());
	}

	/**
	 * Returns a String[] containing the keys of IEntityGroups
	 * that are members of this IEntityGroup.
	 * @param group The group where the members are searched.
	 * @return The keys of the groups which are members of the specified group.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroupStore
	 * #findMemberGroupKeys(org.jasig.portal.groups.IEntityGroup)
	 */
	public String[] findMemberGroupKeys(final IEntityGroup group) throws GroupsException {
		List<String> keys = new ArrayList<String>();
		final Iterator<IEntityGroup> it = findMemberGroups(group);
		while (it.hasNext()) {
			IEntityGroup eg = it.next();
			keys.add(eg.getKey());
		}
		return keys.toArray(new String[keys.size()]);
	}

	/**
	 * Unsupported method.
	 * @param entityType The type of entity.
	 * @return A new instance.
	 * @see org.jasig.portal.groups.IEntityGroupStore#newInstance(java.lang.Class)
	 */
	public IEntityGroup newInstance(@SuppressWarnings("unchecked") final Class entityType) {
		final StringBuffer sb = new StringBuffer(getClass().getSimpleName());
		sb.append(": Method newInstance() not supported.");
		throw new UnsupportedOperationException(sb.toString());
	}

	/**
	 * Search groups whose name matches the query string according to the specified method.
	 * @param query The part of the name of the group.
	 * @param method The method used to perform the comparison.
	 * @param leaftype The type of groups.
	 * @return The array of groups descriptions whose name match query according
	 * to the method of comparison.
	 * @see org.jasig.portal.groups.IEntityGroupStore#searchForGroups(java.lang.String, int, java.lang.Class)
	 */
	public EntityIdentifier[] searchForGroups(final String query, final int method,
			@SuppressWarnings("unchecked") final Class leaftype) {
		final GrouperDTO[] allGroupsInfos = GROUPER_WS.searchForGroups(query, method);
		final EntityIdentifier[] ids = new EntityIdentifier[allGroupsInfos.length];
		int index = 0;
		for (GrouperDTO groupInfos : allGroupsInfos) {
			ids[index++] = new EntityIdentifier(groupInfos.getKey(), IEntityGroup.class);
		}
		return ids;
	}

	/**
	 * Unsupported method.
	 * @param group The group to update.
	 * @see org.jasig.portal.groups.IEntityGroupStore#update(org.jasig.portal.groups.IEntityGroup)
	 */
	public void update(final IEntityGroup group) {
		final StringBuffer sb = new StringBuffer(getClass().getSimpleName());
		sb.append(": Method update() not supported.");
		throw new UnsupportedOperationException(sb.toString());
	}

	/**
	 * Unsupported method.
	 * @param group The group to update.
	 * @see org.jasig.portal.groups.IEntityGroupStore#updateMembers(org.jasig.portal.groups.IEntityGroup)
	 */
	public void updateMembers(final IEntityGroup group) {
		final StringBuffer sb = new StringBuffer(getClass().getSimpleName());
		sb.append(": Method updateMembers() not supported.");
		throw new UnsupportedOperationException(sb.toString());
	}
}
