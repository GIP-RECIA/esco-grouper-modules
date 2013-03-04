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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.esco.grouper.domain.beans.GrouperDTO;
import org.esco.grouper.services.GrouperWSClient;
import org.jasig.portal.groups.EntityGroupImpl;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.groups.IGroupMember;

/**
 * Decorator used to add dynamically some Grouper groups.
 * It is also used to handle a downcast problem in the ancestor class.
 * @author GIP RECIA - A. Deman
 * 27 mars 08
 *
 */
public class ESCODynEntityGroupDecorator extends EntityGroupImpl {

	/** Cache duration for the grouper requests. */
	private static long cacheDuration = ESCOConstants.DEFAULT_CACHE_DURATION;

	/** Logger. */
	//private static final Log LOGGER = LogFactory.getLog(EntityGroupStoreFactory.class);
	private static final Log LOGGER = LogFactory.getLog(ESCODynEntityGroupDecorator.class);

	/** Web service used to access Grouper informations. */
	private static final GrouperWSClient GROUPER_WS = GrouperWSClient.instance();

	/** Grouper groups to load. */
	private String[] grouperGroups;

	/** Cache for member groups retrieved from grouper. */
	private Set<IEntityGroup> grouperMemberGroups;

	/** Cache for grouper member groups keys. */
	private Set<String> grouperMemberGroupKeys;

	/** Time stamp used to manage the cache for the grouper groups. */
	private long grouperGroupsTimeStamp;

	/**
	 * Constructor for ESCODynEntityGroupDecorator.
	 * @param groupKey The group key.
	 * @param entityType The type of the underlying entities.
	 * @param grouperGroups The Grouper groups to load.
	 */
	public ESCODynEntityGroupDecorator(final String groupKey,
			@SuppressWarnings("unchecked") final Class entityType,
			final String[] grouperGroups) {
		super(groupKey, entityType);
		this.grouperGroups = grouperGroups;
	}

	/**
	 * Constructor for ESCODynEntityGroupDecorator.
	 * @param groupKey The group key.
	 * @param entityType The type of the underlying entities.
	 * @param creatorID The id of the creator.
	 * @param groupName the name of the group.
	 * @param description The description of the group.
	 * @param grouperGroups The Grouper groups to load.
	 */
	public ESCODynEntityGroupDecorator(final String groupKey,
			@SuppressWarnings("unchecked") final Class entityType,
			final String creatorID,
			final String groupName,
			final String description,
			final String[] grouperGroups) {
		super(groupKey, entityType);
		this.grouperGroups = grouperGroups;
		setCreatorID(creatorID);
		setName(groupName);
		setDescription(description);
	}

	/**
	 * Constructor for ESCODynEntityGroupDecorator without any Grouper groups to load.
	 * @param groupKey The group key.
	 * @param entityType The type of the underlying entities.
	 * @param creatorID The id of the creator.
	 * @param groupName the name of the group.
	 * @param description The description of the group.
	 */
	public ESCODynEntityGroupDecorator(final String groupKey,
			@SuppressWarnings("unchecked") final Class entityType,
			final String creatorID,
			final String groupName,
			final String description) {
		super(groupKey, entityType);
		setCreatorID(creatorID);
		setName(groupName);
		setDescription(description);
	}

	/**
	 * Retrieves the grouper groups to load.
	 * @return The Grouper groups.
	 */
	protected Set<IEntityGroup> getGrouperMemberGroups() {
		updateGrouperMemberGroups();
		return grouperMemberGroups;
	}


	/**
	 * Gives an iterator through the groups with the grouper groups.
	 * @return Iterator
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected  Iterator getMemberGroups() throws GroupsException {
		if (!hasGrouperGroups()) {
			return super.getMemberGroups();
		}
		return new MergedIterators(super.getMemberGroups(),
				getGrouperMemberGroups().iterator());
	}

	/**
	 * Updates the cache (if needed) for the groups retrieved from grouper.
	 */
	protected void updateGrouperMemberGroups() {
		synchronized (this) {

			final long currentTime = System.currentTimeMillis();

			if ((currentTime - grouperGroupsTimeStamp) > cacheDuration) {

				grouperMemberGroups = new HashSet<IEntityGroup>(ESCOConstants.INITIAL_CAPACITY);

				// Retrieves the Grouper groups associated to the uPortal group
				for (String grouperGroup : grouperGroups) {
					GrouperDTO gInfos = GROUPER_WS.find(grouperGroup);

					if (gInfos == null) {
						final StringBuffer sb = new StringBuffer("Unable to load Grouper group (not found): ");
						sb.append(grouperGroup);
						LOGGER.warn(sb);
					} else {

						final ESCOEntityGroupFactory fact = ESCOEntityGroupFactory.instance();
						IEntityGroup groupToAdd = fact.createEntityGroup(gInfos);
						grouperMemberGroups.add(groupToAdd);
						grouperMemberGroupKeys = new HashSet<String>(grouperMemberGroups.size());
						for (IEntityGroup group : grouperMemberGroups) {
							grouperMemberGroupKeys.add(group.getKey());
						}
					}
				}

				if (LOGGER.isDebugEnabled()) {
					final StringBuffer sb =
						new StringBuffer("updateGrouperMemberGroupKeys for group ");
					sb.append(this);

					sb.append(" grouper groups : ");
					sb.append(grouperGroups);
					sb.append(": ");
					sb.append(grouperMemberGroups);
					LOGGER.debug(sb);
				}

				grouperGroupsTimeStamp = currentTime;
			}
		}
	}

	/**
	 * Retrieves recursively all the members of the group.
	 * @param s The set that contains the already retrieved members.
	 * @return The updated set of members.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.EntityGroupImpl#primGetAllMembers(java.util.Set)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected java.util.Set primGetAllMembers(final Set s) throws GroupsException {
		final Iterator groups = getMemberGroups();
		final Iterator uPortalEntities = getMemberEntities();
		while (groups.hasNext()) {
			final IEntityGroup group = (IEntityGroup) groups.next();
			s.add(group);
			final Iterator members = group.getAllMembers();
			while (members.hasNext()) {
				final IGroupMember member = (IGroupMember) members.next();
				s.add(member);
			}

		}

		while (uPortalEntities.hasNext()) {
			s.add(uPortalEntities.next());
		}

		return s;

	}

	/**
	 * Retrieves recursively all the member entities of the group.
	 * @param s The set that contains the already retrieved member entities.
	 * @return The updated set of member entities.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.EntityGroupImpl#primGetAllEntities(java.util.Set)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected java.util.Set primGetAllEntities(final Set s) throws GroupsException {
		final Iterator groups = getMemberGroups();
		final Iterator uPortalEntities = getMemberEntities();
		while (groups.hasNext()) {
			final IEntityGroup group = (IEntityGroup) groups.next();
			final Iterator members = group.getAllEntities();
			while (members.hasNext()) {
				s.add(members.next());
			}
		}

		while (uPortalEntities.hasNext()) {
			s.add(uPortalEntities.next());
		}
		return s;
	}

	/**
	 * Initialization of the cache duration.
	 * @param newCacheDuration The new value in seconds for the validity of the grouper
	 * requests.
	 */
	public static void initializeCacheDuration(final long newCacheDuration) {
		cacheDuration = newCacheDuration * ESCOConstants.THOUSAND;
	}

	/**
	 * Checks if <code>GroupMember</code> gm is a member of this.
	 * @return boolean
	 * @param gm IGroupMember
	 */
	@Override
	public boolean contains(final IGroupMember gm) throws GroupsException {

		// First checks the initial members of the group.
		if (super.contains(gm)) {
			return true;
		}

		// Tests if the group is member of a grouper subgroup.
		if (hasGrouperGroups()) {
			return getGrouperMemberGroupKeys().contains(gm.getKey());
		}
		return false;
	}

	/**
	 * Gives the grouper groups keys.
	 * @return The keys of the grouper groups associated to this.
	 */
	private Set<String> getGrouperMemberGroupKeys() {
		updateGrouperMemberGroups();
		return grouperMemberGroupKeys;
	}

	/**
	 * @return The string representation of this instance.
	 * @see org.jasig.portal.groups.EntityGroupImpl#toString()
	 */
	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("[Dyn(");
		if (hasGrouperGroups()) {
			sb.append(Arrays.toString(grouperGroups));
		} else {
			sb.append("No Grouper group");
		}
		sb.append(", ");
		sb.append(super.toString());
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Tests if there is Grouper groups to load in this group.
	 * @return True if there is grouper groups to load in the group.
	 */
	public boolean hasGrouperGroups() {
		if (grouperGroups == null) {
			return false;
		}
		return grouperGroups.length > 0;
	}
}
