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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.InvalidNameException;
import javax.naming.Name;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.esco.grouper.domain.beans.GrouperDTO;
import org.esco.grouper.services.GrouperWSClient;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.EntityTypes;
import org.jasig.portal.groups.CompositeEntityIdentifier;
import org.jasig.portal.groups.EntityImpl;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.groups.IGroupMember;
import org.jasig.portal.groups.IIndividualGroupService;
import org.jasig.portal.security.IPerson;

/**
 * Implementation of an IEntityGroup for the Grouper groups.
 *
 * @author GIP RECIA - A. Deman
 * 28 mars 08
 *
 */
public class ESCOEntityGroupImpl implements IEntityGroup, Serializable {

	/** Serial version UID. */
	private static final long serialVersionUID = -3972774817212994931L;

	/** Logger. */
	private static final Log LOGGER = LogFactory.getLog(ESCOEntityGroupImpl.class);

	/** Web service used to access Grouper informations. */
    private static final GrouperWSClient GROUPER_WS = GrouperWSClient.instance();

	/** Default ID for the creator of the groups.*/
	private static final String DEFAULT_CREATOR_ID = "GROUPER";

	/** Cache duration for the grouper requests. */
	private static long cacheDuration = ESCOConstants.DEFAULT_CACHE_DURATION;

	/** Description of the group. */
	private String description;

	/** Name of the group. */
	private String name;

	/** Id of the creator of the group. */
	private String creatorId = DEFAULT_CREATOR_ID;

	/** Identifier of the group. */
	private CompositeEntityIdentifier identifier;

	/** Grouper group service. */
	private IIndividualGroupService localGroupService;

	/** Type of the underlying entity. */
	private Class< ? > entityType;

	/** Time stamp used to manage the grouper cache for the members (groups and entities). */
	private long membersTimeStamp;

	/** Time stamp used to manage the membership cache. */
	private long membershipsTimeStamp;

	/** Cache for the member groups. */
	private Set<IEntityGroup> memberGroups;

	/** Cache for the member group keys. */
	private Set<String> memberGroupKeys;

	/** Cache for the memberships. */
	private Set<IEntityGroup> memberships;

	/** Cache for the membership keys. */
	private Set<String> membershipKeys;

	/** Cache for the member entities. */
	private Set<IEntity> memberEntities;

	/** Cache for the member entity keys. */
	private Set<String> memberEntityKeys;

	/** uPortal containing group. */
	private Set<IEntityGroup> uPortalContainingGroups;

	/** uPortal containing group. */
	private Set<String> uPortalContainingGroupsKeys;

	/**
	 * Constructor for ESCOEntityGroupImpl.
	 * @param groupKey The key of the group.
	 * @param entityType The type of the underlying entity.
	 * @param name  The name of the group.
	 * @param description The description of the group.
	 */
	public ESCOEntityGroupImpl(final String groupKey,
			final Class< ? > entityType,
			final String name,
			final String description) {
		identifier = new CompositeEntityIdentifier(groupKey, EntityTypes.GROUP_ENTITY_TYPE);
		this.name = name;
		this.description = description;
		this.entityType = entityType;
		setLocalGroupService(ESCOGrouperGroupServiceFinder.instance().getGrouperService());
	}

	/**
	 * Constructor for ESCOEntityGroupImpl.
	 * @param grouperInfos The grouper DTO retrieved from Grouper.
	 */
	public ESCOEntityGroupImpl(final GrouperDTO grouperInfos) {

	        this(grouperInfos.getKey(), IPerson.class, grouperInfos.getName(), grouperInfos.getDescription());

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
	 * Upates the cache for the members, if needed.
	 */
	private void updateRetrievedMembers() {
		synchronized (this) {

			long currentTime = System.currentTimeMillis();


			if ((currentTime - membersTimeStamp) > cacheDuration) {

				// New instances of cache are created because the old ones may be still in used
				// externally (via an iterator for instance.
				memberGroups = new HashSet<IEntityGroup>(ESCOConstants.INITIAL_CAPACITY);
				memberGroupKeys = new HashSet<String>(ESCOConstants.INITIAL_CAPACITY);
				memberEntities = new HashSet<IEntity>(ESCOConstants.INITIAL_CAPACITY);
				memberEntityKeys = new HashSet<String>(ESCOConstants.INITIAL_CAPACITY);

				// Retrives all the members of the group.s
				@SuppressWarnings("unchecked")
				Iterator iter = getLocalGroupService().findMembers(this);

				// Dispatch entities and groups in the good cache.
				while (iter.hasNext()) {
					Object o = iter.next();
					final IGroupMember entity = (IGroupMember) o;

					if (entity.isGroup()) {
						memberGroups.add((IEntityGroup) entity);
						memberGroupKeys.add(entity.getKey());
					} else {
						memberEntities.add((IEntity) entity);
						memberEntityKeys.add(entity.getKey());
					}
				}

				if (LOGGER.isDebugEnabled()) {
					final StringBuffer sb = new StringBuffer("Updating retrieved members for ");
					sb.append(this);
					sb.append(" - Member groups: ");
					sb.append(memberGroups);
					sb.append(" - Member entities: ");
					sb.append(memberEntities);
					sb.append(" - Time stamp : ");
					sb.append(membersTimeStamp);
					sb.append(" => ");
					sb.append(currentTime);
					LOGGER.debug(sb);
				}

				membersTimeStamp = currentTime;
			}
		}
	}

	/**
	 * Updates the cache for the memberships if needed.
	 */
	private void updateRetrievedMemberships() {
		synchronized (this) {

			long currentTime = System.currentTimeMillis();

			if ((currentTime - membershipsTimeStamp) > cacheDuration) {

				// New instance are created so that old ones can still bee in used in an other class,
				// whithout error.
				memberships = new HashSet<IEntityGroup>(ESCOConstants.INITIAL_CAPACITY);
				membershipKeys = new HashSet<String>(ESCOConstants.INITIAL_CAPACITY);

				// Retrieves all the memberships.
				@SuppressWarnings("unchecked")
				final Iterator iter = getLocalGroupService().findContainingGroups(this);
				while (iter.hasNext()) {
					IEntityGroup group = (IEntityGroup) iter.next();
					memberships.add(group);
					membershipKeys.add(group.getKey());
				}

				// If the group is loaded into uPortal groups, the memberships are added.
				if (hasUPortalContainingGroup()) {
				   memberships.addAll(uPortalContainingGroups);
				   membershipKeys.addAll(uPortalContainingGroupsKeys);
				}

				if (LOGGER.isDebugEnabled()) {
					final StringBuffer sb = new StringBuffer("Updating retrieved memberships for ");
					sb.append(this);
					sb.append(" - Memberhips: ");
					sb.append(memberships);
					sb.append(" - Time stamp : ");
					sb.append(membershipsTimeStamp);
					sb.append(" => ");
					sb.append(currentTime);
					LOGGER.debug(sb);
				}

				membershipsTimeStamp = currentTime;
			}
		}
	}

	/**
	 * Tests if the group has to be loaded in an uPortal group.
	 * @return True if this group has an uPortal group ancestor.
	 */
	private boolean hasUPortalContainingGroup() {
	    if (uPortalContainingGroups == null) {
	        return false;
	    }
	    return uPortalContainingGroups.size() > 0;
	}

	/**
	 * Loads dynamically this group into an uPortal group.
	 * @param uPortalContainingGroup The uPortal where this group has to be loaded into.
	 */
	public void loadDinamicallyIntoUportalGroup(final IEntityGroup uPortalContainingGroup) {
	    if (uPortalContainingGroups == null) {
	        uPortalContainingGroups = new HashSet<IEntityGroup>();
	        uPortalContainingGroupsKeys = new HashSet<String>();
	    }
	    uPortalContainingGroups.add(uPortalContainingGroup);
	    uPortalContainingGroupsKeys.add(uPortalContainingGroup.getKey());
	}

	/**
	 * Not supported.
	 * @param arg0
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroup#addMember(org.jasig.portal.groups.IGroupMember)
	 */
	public void addMember(final IGroupMember arg0) throws GroupsException {
		//Not supported.
	}

	/**
	 * Not supported.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroup#delete()
	 */
	public void delete() throws GroupsException {
		// Not supported.
	}

	/**
	 * @return an integer hash code for the receiver
	 * @see java.util.Hashtable
	 */
	@Override
	public int hashCode() {
		return getKey().hashCode();
	}

	/**
	 * Test if an object is equal to this one.
	 * @param o The object ot test.
	 * @return True if the object is equal to this instance.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof ESCOEntityGroupImpl)) {
			return false;
		}
		final ESCOEntityGroupImpl escoEGI = (ESCOEntityGroupImpl) o;
		return escoEGI.getKey().equals(getKey());
	}

	/**
	 * Gives the String representation of this instance.
	 * @return The String representation of this instance.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "#("
		+ getKey() + ", " + getName() + ", " + getDescription() + ")";
	}

	/**
	 * Getter for creatorID.
	 * @return creatorID.
	 * @see org.jasig.portal.groups.IEntityGroup#getCreatorID()
	 */
	public String getCreatorID() {
		return creatorId;
	}

	/**
	 * Getter for description.
	 * @return description.
	 * @see org.jasig.portal.groups.IEntityGroup#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gives the local key.
	 * @return The local key.
	 * @see org.jasig.portal.groups.IEntityGroup#getLocalKey()
	 */
	public String getLocalKey() {
		return  getIdentifier().getLocalKey();
	}

	/**
	 * Getter for name.
	 * @return name.
	 * @see org.jasig.portal.groups.IEntityGroup#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gives the service name.
	 * @return The service name.
	 * @see org.jasig.portal.groups.IEntityGroup#getServiceName()
	 */
	public Name getServiceName() {
		return getIdentifier().getServiceName();
	}

	/**
	 * Tests if the group is editable.
	 * @return True if the group is editable.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroup#isEditable()
	 */
	public boolean isEditable() throws GroupsException {
		return getLocalGroupService().isEditable(this);
	}

	/**
	 * Not supported.
	 * @param arg0
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroup#removeMember(org.jasig.portal.groups.IGroupMember)
	 */
	public void removeMember(final IGroupMember arg0) throws GroupsException {
		/* Not supported. */
	}

	/**
	 * Setter for creatorID.
	 * @param creatorId The new value for creatorID.
	 * @see org.jasig.portal.groups.IEntityGroup#setCreatorID(java.lang.String)
	 */
	public void setCreatorID(final String creatorId) {
		this.creatorId = creatorId;
	}

	/**
	 * Setter for description.
	 * @param description The new value for description.
	 * @see org.jasig.portal.groups.IEntityGroup#setDescription(java.lang.String)
	 */
	public void setDescription(final String description) {
		this.description = description;

	}

	/**
	 * Setter for local group service.
	 * @param localGroupService The new value for localGroupService.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroup
	 * #setLocalGroupService(org.jasig.portal.groups.IIndividualGroupService)
	 */
	public void setLocalGroupService(final IIndividualGroupService localGroupService)
	throws GroupsException {
			this.localGroupService = localGroupService;
			try {
				getIdentifier().setServiceName(localGroupService.getServiceName());
			} catch (InvalidNameException e) {
				LOGGER.error("Problem setting service name", e);
				throw new GroupsException("Problem setting service name", e);
			}
	}

	/**
	 * Getter for localGroupService.
	 * @return localGroupService.
	 */
	protected IIndividualGroupService getLocalGroupService() {
		return localGroupService;
	}

	/**
	 * Setter for name.
	 * @param name The new value for name.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroup#setName(java.lang.String)
	 */
	public void setName(final String name) throws GroupsException {
		this.name = name;
	}

	/**
	 * Updates this groups (not used as theses groups are read only).
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroup#update()
	 */
	public void update() throws GroupsException {
		getLocalGroupService().updateGroup(this);
	}

	/**
	 * Updates members (not used as theses groups are read only).
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroup#updateMembers()
	 */
	public void updateMembers() throws GroupsException {
		getLocalGroupService().updateGroupMembers(this);
	}

	/**
	 * Test if a group member belongs to this group.
	 * @param gm The group member to test.
	 * @return True if the group member belongs to this group.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IGroupMember#contains(org.jasig.portal.groups.IGroupMember)
	 */
	public boolean contains(final IGroupMember gm) throws GroupsException {
		if (getMemberEntityKeys().contains(gm.getKey())) {
			return true;
		}
		return getMemberGroupKeys().contains(gm.getKey());
	}

	/**
	 * Test if a given groupMember belongs to this group directly or indirectly.
	 * @param gm The groupMeber to test.
	 * @return True if the groupMemeber denotes a membership to this group.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IGroupMember#deepContains(org.jasig.portal.groups.IGroupMember)
	 */
	public boolean deepContains(final IGroupMember gm) throws GroupsException {
	    return GROUPER_WS.hasDeepMember(getLocalKey(), gm.getKey());

	}

	/**
	 * Gives all the groups that contain this group.
	 * @return The groups that contain this group as a direct or indirect member.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IGroupMember#getAllContainingGroups()
	 */
	public Iterator<IGroupMember> getAllContainingGroups() throws GroupsException {

	    LOGGER.warn("[ESCO INFORMATION] >>>>>>>>> appel de la methode ESCOEntityGroupImpl.getAllContainingGroups() pour le groupe " + getLocalKey());

		final Set<IGroupMember> containingGroups = new HashSet<IGroupMember>();
		getAllContainingGroupsRecursively(this, containingGroups);

		return containingGroups.iterator();
	}

	/**
	 * Gives all the groups that contain a given group.
	 * @param gm  The current group.
	 * @param containingGroups The Set of containing groups.
	 */
	private void getAllContainingGroupsRecursively(final IGroupMember gm,
			final Set<IGroupMember> containingGroups) {
		@SuppressWarnings("unchecked")
		final Iterator iter = gm.getContainingGroups();

		while (iter.hasNext()) {
			IGroupMember newGm = (IGroupMember) iter.next();
			containingGroups.add(gm);
			getAllContainingGroupsRecursively(newGm, containingGroups);
		}
	}

	/**
	 * Gives all entities contained in this group.
	 * @return An iterator over the entities that are direct member of this group.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IGroupMember#getAllEntities()
	 */
	public Iterator<IEntity> getAllEntities() throws GroupsException {

	    final GrouperDTO[] gInfos = GROUPER_WS.getAllMemberEntities(getLocalKey());

        final List<IEntity> members = new ArrayList<IEntity>(gInfos.length);
        for (GrouperDTO gInfo : gInfos) {
            members.add(new EntityImpl(gInfo.getKey(), IPerson.class));
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("All entities for group ");
            sb.append(getLocalKey());
            sb.append(": ");

            sb.append(Arrays.toString(gInfos));
            sb.append(" ");
            LOGGER.debug(sb.toString());
        }

        return members.iterator();



//		final Set<IEntity> allEntities = new HashSet<IEntity>(ESCOConstants.INITIAL_CAPACITY);
//		final Set<IGroupMember> treatedGroups = new HashSet<IGroupMember>(ESCOConstants.INITIAL_CAPACITY);
//		final Stack<IGroupMember> groupsToTreat = new Stack<IGroupMember>();
//		groupsToTreat.push(this);
//
//		while (!groupsToTreat.isEmpty()) {
//
//			final IGroupMember groupToTreat = groupsToTreat.pop();
//
//			if (!treatedGroups.contains(groupToTreat)) {
//
//				@SuppressWarnings("unchecked")
//				final Iterator iter = groupToTreat.getMembers();
//				while (iter.hasNext()) {
//					final IGroupMember currentGM = (IGroupMember) iter.next();
//
//					if (currentGM.isGroup()) {
//						groupsToTreat.add(currentGM);
//					} else if (currentGM.isEntity()) {
//						allEntities.add((IEntity) currentGM);
//					}
//				}
//				treatedGroups.add(groupToTreat);
//			}
//		}
//		return allEntities.iterator();
	}

	/**
	 * Gives all the members of this group.
	 * @return An iterator over all the groups or entities that are direct or indirect
	 * members of this group.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IGroupMember#getAllMembers()
	 */
	public Iterator<IGroupMember> getAllMembers() throws GroupsException {
	    GrouperDTO[] membersInfos = GROUPER_WS.getAllMembers(getLocalKey());
	    final Set<IGroupMember> allMembers = new HashSet<IGroupMember>(ESCOConstants.INITIAL_CAPACITY);
	    for (GrouperDTO memberInfos : membersInfos) {
	        if (memberInfos.denotesSubject()) {
	            allMembers.add(new EntityImpl(memberInfos.getKey(), IPerson.class));
	        } else {
	            allMembers.add(ESCOEntityGroupFactory.instance().createEntityGroup(memberInfos));
	        }
	    }
	    return allMembers.iterator();
//		final Set<IGroupMember> allMembers = new HashSet<IGroupMember>(ESCOConstants.INITIAL_CAPACITY);
//		final Set<IGroupMember> treatedGroups = new HashSet<IGroupMember>(ESCOConstants.INITIAL_CAPACITY);
//		final Stack<IGroupMember> groupsToTreat = new Stack<IGroupMember>();
//		groupsToTreat.push(this);
//
//		while (!groupsToTreat.isEmpty()) {
//
//			final IGroupMember groupToTreat = groupsToTreat.pop();
//
//			if (!treatedGroups.contains(groupToTreat)) {
//
//				@SuppressWarnings("unchecked")
//				final Iterator iter = groupToTreat.getMembers();
//				while (iter.hasNext()) {
//					final IGroupMember currentGM = (IGroupMember) iter.next();
//					allMembers.add(currentGM);
//					if (currentGM.isGroup()) {
//						groupsToTreat.add(currentGM);
//					}
//				}
//				treatedGroups.add(groupToTreat);
//			}
//		}
//		return allMembers.iterator();
	}

	/**
	 * Gives all the groups that contains directly this group.
	 * @return The direct ancestors of this group.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IGroupMember#getContainingGroups()
	 */
	public Iterator<IEntityGroup> getContainingGroups() throws GroupsException {
	    if (LOGGER.isDebugEnabled()) {
	        final StringBuffer sb = new StringBuffer(" getContainingGroups for: ");
	        sb.append(this);
	        sb.append(" => ");
	        sb.append(getMemberships());

	    }
		return getMemberships().iterator();
	}

	/**
	 * Gives an iterator over the entities that are direct members
	 * of this group.
	 * @return The direct member entities.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IGroupMember#getEntities()
	 */
	public Iterator<IEntity> getEntities() throws GroupsException {
		return getMemberEntities().iterator();
	}

	/**
	 * Getter for member groups.
	 * @return member groups after updating the cache.
	 */
	protected Set<IEntityGroup> getMemberGroups() {
		updateRetrievedMembers();
		return memberGroups;
	}

	/**
	 * Getter for memberGroupKeys.
	 * @return memberGroupKeys after updating the cache.
	 */
	protected Set<String> getMemberGroupKeys() {
		updateRetrievedMembers();
		return memberGroupKeys;
	}

	/**
	 * Getter for memberEntities.
	 * @return memberEntities after uptating the cache.
	 */
	protected Set<IEntity> getMemberEntities() {
		updateRetrievedMembers();
		return memberEntities;
	}

	/**
	 * Getter for memberEntityKeys.
	 * @return memberEntitiKeys after updation the cache.
	 */
	protected Set<String> getMemberEntityKeys() {
		updateRetrievedMembers();
		return memberEntityKeys;
	}

	/**
	 * Getter for memberships.
	 * @return memberships after updating the cache.
	 */
	protected Set<IEntityGroup> getMemberships() {
		updateRetrievedMemberships();
		return memberships;
	}

	/**
	 * Getter for membershipKeys.
	 * @return membershipKeys after updating the cache.
	 */
	protected Set<String> getMembershipKeys() {
		updateRetrievedMemberships();
		return membershipKeys;
	}

	/**
	 * Getter for entityType.
	 * @return entityType.
	 * @see org.jasig.portal.groups.IGroupMember#getEntityType()
	 */
	public Class< ? > getEntityType() {
		return entityType;
	}

	/**
	 * Getter for identifier.
	 * @return identifier.
	 */
	protected CompositeEntityIdentifier getIdentifier() {
		return identifier;
	}

	/**
	 * Gives the key of this group.
	 * @return return the key of this group.
	 * @see org.jasig.portal.groups.IGroupMember#getKey()
	 */
	public String getKey() {
		return getIdentifier().getKey();
	}

	/**
	 * Gives the type of the leaves for the group tree.
	 * @return  The type of the leaves.
	 * @see org.jasig.portal.groups.IGroupMember#getLeafType()
	 */
	public Class< ? > getLeafType() {
		return entityType;
	}

	/**
	 * Search for a group by name.
	 * @param groupName The name of the group to look for.
	 * @return The group if found, null otherwise.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IGroupMember#getMemberGroupNamed(java.lang.String)
	 */
	public IEntityGroup getMemberGroupNamed(final String groupName) throws GroupsException {
		final Iterator<IEntityGroup> iter = getMemberGroups().iterator();
		while (iter.hasNext()) {
			IEntityGroup g = iter.next();
			if (g.getName().equals(groupName)) {
				return g;
			}
		}
		return null;
	}

	/**
	 * Gives the members of the group.
	 * @return The members of the group, both entity and groups.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IGroupMember#getMembers()
	 */
	@SuppressWarnings("unchecked")
	public Iterator getMembers() throws GroupsException {
		return new MergedIterators(getMemberEntities().iterator(), getMemberGroups().iterator());
	}

	/**
	 * Gives the type of the entity.
	 * @return The type of the entity.
	 * @see org.jasig.portal.groups.IGroupMember#getType()
	 */
	@SuppressWarnings("unchecked")
	public Class getType() {
		return getIdentifier().getType();
	}

	/**
	 * Gives the underlying identifier.
	 * @return The underlying identifier.
	 * @see org.jasig.portal.groups.IGroupMember#getUnderlyingEntityIdentifier()
	 */
	public EntityIdentifier getUnderlyingEntityIdentifier() {
		return identifier;
	}

	/**
	 * Tests if this group has members.
	 * @return True if the group has members.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IGroupMember#hasMembers()
	 */
	public boolean hasMembers() throws GroupsException {
		if (!getMemberGroups().isEmpty()) {
			return true;
		}
		return !getMemberEntities().isEmpty();
	}

	/**
	 * Test if a group member is member of this group ether directly or indirectly.
	 * @param gm The group member to test.
	 * @return True if the group member is member of this group directly or not.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IGroupMember#isDeepMemberOf(org.jasig.portal.groups.IGroupMember)
	 */
	public boolean isDeepMemberOf(final IGroupMember gm) throws GroupsException {
		if (isMemberOf(gm)) {
			return true;
		}
		return gm.deepContains(this);
	}

	/**
	 * @return false.
	 * @see org.jasig.portal.groups.IGroupMember#isEntity()
	 */
	public boolean isEntity() {
		return false;
	}

	/**
	 * @return true.
	 * @see org.jasig.portal.groups.IGroupMember#isGroup()
	 */
	public boolean isGroup() {
		return true;
	}

	/**
	 * Test if a groupMember belongs directly to this group.
	 * @param gm The groupMember considered.
	 * @return True if the groupMember denotes a direct member to this group.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IGroupMember#isMemberOf(org.jasig.portal.groups.IGroupMember)
	 */
	public boolean isMemberOf(final IGroupMember gm) throws GroupsException {
		return getMembershipKeys().contains(gm.getKey());
	}

	/**
	 * Getter for identifier.
	 * @return identifier.
	 * @see org.jasig.portal.IBasicEntity#getEntityIdentifier()
	 */
	public EntityIdentifier getEntityIdentifier() {
		return identifier;
	}

}
