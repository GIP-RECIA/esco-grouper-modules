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

/**
 *
 */
package org.esco.grouper.services;


import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.GroupFinder;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.Member;
import edu.internet2.middleware.grouper.MemberFinder;
import edu.internet2.middleware.grouper.Membership;
import edu.internet2.middleware.grouper.Stem;
import edu.internet2.middleware.grouper.Stem.Scope;
import edu.internet2.middleware.grouper.StemFinder;
import edu.internet2.middleware.grouper.SubjectFinder;
import edu.internet2.middleware.grouper.exception.GroupNotFoundException;
import edu.internet2.middleware.grouper.exception.MemberNotFoundException;
import edu.internet2.middleware.grouper.exception.QueryException;
import edu.internet2.middleware.grouper.exception.StemNotFoundException;
import edu.internet2.middleware.grouper.filter.GroupNameFilter;
import edu.internet2.middleware.grouper.filter.GrouperQuery;
import edu.internet2.middleware.grouper.filter.QueryFilter;
import edu.internet2.middleware.grouper.filter.StemNameFilter;
import edu.internet2.middleware.grouper.misc.Owner;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import edu.internet2.middleware.subject.SubjectNotUniqueException;
import edu.internet2.middleware.subject.provider.SubjectTypeEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.esco.grouper.domain.beans.GroupOrStem;
import org.esco.grouper.domain.beans.GrouperDTO;
import org.esco.grouper.exceptions.EscoGrouperException;
import org.esco.grouper.utils.GrouperSessionUtil;
import org.jasig.portal.groups.IGroupConstants;
import org.springframework.beans.factory.InitializingBean;

/**
 * Implementation used to expose some methods of the grouper API.
 * @author GIP RECIA - A. Deman
 * 26 nov. 07
 */
public class GrouperAPIExposerImpl implements IGrouperAPIExposer, InitializingBean {

    /** Id for the subject used to manage the Grouper sessions. */
    private static final String GRP_SUBJ_SESS_ID = "GrouperSystem";

    /** Logger. */
    private static final Log LOGGER = LogFactory.getLog(GrouperAPIExposerImpl.class);

    /** used to create sessions. */
    private GrouperSessionUtil sessionUtil = new GrouperSessionUtil(GRP_SUBJ_SESS_ID);

    /**
     * Constructor for GrouperAPIExposerImpl.
     */
    public GrouperAPIExposerImpl() {
        /*  */
    }

    /**
     * Creates an instance of GrouperDTO.
     * @param g The group used to build the instance.
     * @return The instance.
     */
    private GrouperDTO fetchGrouperData(final Group g) {
        return GrouperDTO.createGroupDTO(g.getParentStem().getName(),
                g.toMember().getImmediateMemberships().isEmpty(),
                g.getName(),
                g.getDisplayExtension(),
                g.getDescription());
    }

    /**
     * Creates an instance of GrouperDTO.
     * @param s The stem used to build the instance.
     * @return The instance.
     */
    private GrouperDTO fetchGrouperData(final Stem s) {
        String parentStem = "";
        if (!s.isRootStem()) {
            try {
                parentStem = s.getParentStem().getName();
            } catch (StemNotFoundException e) {
                LOGGER.error(e, e);
            }
        }

        return GrouperDTO.createStemDTO(parentStem,
                s.isRootStem(),
                s.getName(),
                s.getDisplayExtension(),
                s.getDescription());
    }

    /**
     * Creates an instance of GrouperDTO.
     * @param s The subject used to build the instance.
     * @return The instance.
     */
    private GrouperDTO fetchGrouperData(final Subject s) {
        return GrouperDTO.createSubjectDTO(s.getId(), s.getName(), s.getDescription());
    }

    /**
     * Creates an instance of GrouperDTO.
     * @param gos The group or stem used to build the instance.
     * @return The instance.
     */
    private GrouperDTO fetchGrouperData(final GroupOrStem gos) {
        if (gos.isGroup()) {
            return fetchGrouperData(gos.asGroup());
        }
        return fetchGrouperData(gos.asStem());
    }

    /**
     * Utility used to retrieve Group information from a memberships set.
     * @param memberships The memberships.
     * @param parentStem The parent stem in the case of a group.
     * @return The array of GrouperDTO
     */
    private GrouperDTO[] fetchGrouperData(@SuppressWarnings("rawtypes") final Set memberships, final Stem parentStem) {
        final List<GrouperDTO> list = new ArrayList<GrouperDTO>(memberships.size());
        if (parentStem != null) {
            list.add(fetchGrouperData(parentStem));
        }
        for (final Object o : memberships) {
            final Membership m = (Membership) o;
            try {
                list.add(fetchGrouperData(m.getGroup()));
            } catch (GroupNotFoundException e) {
                LOGGER.error(e, e);
            }
        }
        return list.toArray(new GrouperDTO[list.size()]);
    }

    /**
     * Gives a subject.
     * @param key The key of the subject to find.
     * @return The subject if found.
     */
    private Subject fetchSubject(final String key) {
        Subject s = null;
        try {
            s = SubjectFinder.findById(key, true);
        } catch (SubjectNotFoundException e) {
            LOGGER.info(e);
        } catch (SubjectNotUniqueException e) {
            LOGGER.error(e, e);
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Searching for subject: ");
            sb.append(key);
            sb.append(" result:");
            sb.append(s);
            LOGGER.debug(sb);
        }
        return s;
    }

    /**
     * Retrieves a specified group if exists.
     * @param session The grouper session.
     * @param groupName the name of the group to retrieve.
     * @return The group if found, null otherwise.
     */
    @SuppressWarnings("unused")
    private Group fetchGroup(final GrouperSession session, final String groupName) {

        Group g = null;

        try {
            g = GroupFinder.findByName(session, groupName, true);
        } catch (GroupNotFoundException e) {
            LOGGER.error(e, e);
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Searching for group: ");
            sb.append(groupName);
            sb.append(" result: ");
            sb.append(g);
            LOGGER.debug(sb);
        }
        return g;
    }


    /**
     * Retrieves a specified stem if exists.
     * @param session The grouper session.
     * @param stemName the name of the stem to retrieve.
     * @return The stem if found, null otherwise.
     */
    private Stem fetchStem(final GrouperSession session, final String stemName) {

        Stem s = null;

        try {
            s = StemFinder.findByName(session, stemName, true);
        } catch (StemNotFoundException e) {
            LOGGER.error(e, e);
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Searching for group: ");
            sb.append(stemName);
            sb.append(" result:");
            sb.append(s);
            LOGGER.debug(sb);
        }
        return s;
    }

    /**
     * Retrieves a specified group or stem if exists.
     * @param session The grouper session.
     * @param name the name of the group or stem to retrieve.
     * @return The GroupOrStem instance if found, null otherwise.
     */
    private GroupOrStem fetchGroupOrStem(final GrouperSession session, final String name) {

        GroupOrStem gos = null;

        try {
            final Group g = GroupFinder.findByName(session, name, true);
            gos = new GroupOrStem(g);
        } catch (GroupNotFoundException gnf) {
            try {
                final Stem s = StemFinder.findByName(session, name, true);
                gos = new GroupOrStem(s);
            } catch (StemNotFoundException snf) {
                LOGGER.error("Unable to find group or stem for the name: " + name);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Searching for group or stem: ");
            sb.append(name);
            sb.append(" result: ");
            sb.append(gos);
            LOGGER.debug(sb);
        }
        return gos;
    }



    /**
     * Converts a subject to a member object.
     * @param session The grouper session.
     * @param subject The subject to convert.
     * @return The member.
     */
    private Member fetchMember(final GrouperSession session, final Subject subject) {

        final Member m = MemberFinder.findBySubject(session, subject, true);

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Searching for member: ");
            sb.append(subject);
            sb.append(" result:");
            sb.append(m);
            LOGGER.debug(sb);
        }

        return m;
    }

    /**
     * Gives all the subjects that are members of this group.
     * @param group The target group.
     * @param session The grouper session.
     * @return All the subjects members of the group.
     */
    private Set<GrouperDTO> getAllMemberSubjects(final GrouperSession session, final Group group) {

        final Set<GrouperDTO> allMemberSubjects = new HashSet<GrouperDTO>();

        @SuppressWarnings("rawtypes")
        final Set members = group.getEffectiveMemberships();
        for (Object o : members) {
            try {
                final Member mb = ((Membership) o).getMember();
                if (mb.getSubjectType().equals(SubjectTypeEnum.PERSON)) {
                    allMemberSubjects.add(fetchGrouperData(mb.getSubject()));
                }
            } catch (MemberNotFoundException e) {
                LOGGER.error(e, e);
            } catch (SubjectNotFoundException e) {
                LOGGER.error(e, e);
            }
        }
        return allMemberSubjects;
    }

    /**
     * Gives all the groups that are members of this group.
     * @param group The target group.
     * @param session The grouper session.
     * @return All the groups members of the group.
     */
    private Set<GrouperDTO> getAllMemberGroups(final GrouperSession session, final Group group) {

        final Set<GrouperDTO> allMemberGroups = new HashSet<GrouperDTO>();

        @SuppressWarnings("rawtypes")
        final Set members = group.getEffectiveMemberships();
        for (Object o : members) {
            try {
                final Member mb = ((Membership) o).getMember();
                if (mb.getSubjectType().equals(SubjectTypeEnum.GROUP)) {
                    allMemberGroups.add(fetchGrouperData(mb.toGroup()));
                }
            } catch (MemberNotFoundException e) {
                LOGGER.error(e, e);
            } catch (GroupNotFoundException e) {
                LOGGER.error(e, e);
            }
        }

        return allMemberGroups;
    }


    /**
     * Gives all the  members of this group.
     * @param group The target group.
     * @param session The grouper session.
     * @return All the members of the group.
     */
    private Set<GrouperDTO> getAllMembers(final GrouperSession session, final Group group) {

        final Set<GrouperDTO> allMembers = new HashSet<GrouperDTO>();

        @SuppressWarnings("rawtypes")
        final Set members = group.getEffectiveMemberships();
        for (Object o : members) {
            try {
                final Member mb = ((Membership) o).getMember();
                if (mb.getSubjectType().equals(SubjectTypeEnum.PERSON)) {
                    allMembers.add(fetchGrouperData(mb.getSubject()));
                } else if (mb.getSubjectType().equals(SubjectTypeEnum.GROUP)) {
                    allMembers.add(fetchGrouperData(mb.toGroup()));
                }
            } catch (MemberNotFoundException e) {
                LOGGER.error(e, e);
            } catch (SubjectNotFoundException e) {
                LOGGER.error(e, e);
            } catch (GroupNotFoundException e) {
            LOGGER.error(e, e);
            }
        }
        return allMembers;
    }

    /**
     * Gives all the groups that are members of the stem.
     * @param stem The key of the target stem.
     * @param session The grouper session.
     * @return All the groups members of the stem.
     */
    private Set<GrouperDTO> getAllMemberGroups(final GrouperSession session, final Stem stem) {

        final Set<GrouperDTO> allMemberGroups = new HashSet<GrouperDTO>();

        // Adds all the member groups.
        final Set<Group> childGroups = stem.getChildGroups(Scope.SUB);
        for (Group g : childGroups) {
            allMemberGroups.addAll(getAllMemberGroups(session, g));
            allMemberGroups.add(fetchGrouperData(g));
        }

        // Add all The child Stems.
        final Set<Stem> childStems = stem.getChildStems(Scope.SUB);
        for (Stem s : childStems) {
            allMemberGroups.add(fetchGrouperData(s));
        }
        return allMemberGroups;
    }

    /**
     * Gives all the subject that are members of the stem.
     * @param stem The key of the target stem.
     * @param session The grouper session.
     * @return All the subjects members of the stem.
     */
    private Set<GrouperDTO> getAllMemberSubjects(final GrouperSession session, final Stem stem) {

        final Set<GrouperDTO> allMemberSubjects = new HashSet<GrouperDTO>();

        // Retrieves all the subjects members of the child groups.
        final Set<Group> childGroups = stem.getChildGroups(Scope.SUB);
        for (Group g : childGroups) {
            allMemberSubjects.addAll(getAllMemberSubjects(session, g));
        }

        return allMemberSubjects;
    }
    /**
     * Gives all the members of the stem : child stems, child groups and their members.
     * @param stem The key of the target stem.
     * @param session The grouper session.
     * @return All the members of the stem.
     */
    private Set<GrouperDTO> getAllMembers(final GrouperSession session, final Stem stem) {

        final Set<GrouperDTO> allMembers = new HashSet<GrouperDTO>();

        // Adds all the member groups.
        final Set<Group> childGroups = stem.getChildGroups(Scope.SUB);
        for (Group g : childGroups) {
            allMembers.addAll(getAllMembers(session, g));
            allMembers.add(fetchGrouperData(g));
        }

        // Add all The child Stems.
        final Set<Stem> childStems = stem.getChildStems(Scope.SUB);
        for (Stem s : childStems) {
            allMembers.add(fetchGrouperData(s));
        }
        return allMembers;
    }

    /**
     * Tests if a subject is member of a given group.
     * @param name The name of the group or stem.
     * @param subjectId The id of the subject.
     * @return True if the subject is a member of the group.
     * @see org.esco.grouper.services.IGrouperAPIExposer#hasMember
     * (java.lang.String, java.lang.String)
     */
    public boolean hasMember(final String name, final String subjectId) {

        final GrouperSession session = sessionUtil.createSession();
        final GroupOrStem gos = fetchGroupOrStem(session, name);
        boolean member = false;

        if (gos != null) {
            if (gos.isGroup()) {
                final Subject subject = fetchSubject(subjectId);
                member = gos.asGroup().hasImmediateMember(subject);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Group: ");
            sb.append(name);
            sb.append(" hasMember: ");
            sb.append(subjectId);
            sb.append(" = ");
            sb.append(member);
            LOGGER.debug(sb);
        }

        sessionUtil.stopSession(session);
        return member;
    }

    /**
     * Tests if a subject is a deep member of a given group or stem.
     * @param name The name of the group or stem.
     * @param subjectId The id of the subject.
     * @return True if the subject is a deep member of the group.
     * @see org.esco.grouper.services.IGrouperAPIExposer#hasMember
     * (java.lang.String, java.lang.String)
     */
    public boolean hasDeepMember(final String name, final String subjectId) {

        final GrouperSession session = sessionUtil.createSession();
        final GroupOrStem gos = fetchGroupOrStem(session, name);
        boolean member = false;
        final Subject subject = fetchSubject(subjectId);
        if (gos != null && subject != null) {
            if (gos.isGroup()) {
                // The target is a group.

                member = gos.asGroup().hasMember(subject);

                if (LOGGER.isDebugEnabled()) {
                    final StringBuffer sb = new StringBuffer("Group: ");
                    sb.append(name);
                    sb.append(" hasDeepMember: ");
                    sb.append(subjectId);
                    sb.append(" = ");
                    sb.append(member);
                    LOGGER.debug(sb);
                }


            } else {
                // The target is a folder.
                final Member subjectAsMember = fetchMember(session, subject);
                if (subjectAsMember != null) {

                    @SuppressWarnings("rawtypes")
                    final Iterator memberships = subjectAsMember.getMemberships().iterator();

                    while (memberships.hasNext() && !member) {
                        final Membership m = (Membership) memberships.next();
                        try {
                            member = m.getGroup().getName().startsWith(name);

                        } catch (GroupNotFoundException e) {
                            LOGGER.error(e, e);
                        }
                    }

                }
                if (LOGGER.isDebugEnabled()) {
                    final StringBuffer sb = new StringBuffer("Folder: ");
                    sb.append(name);
                    sb.append(" hasDeepMember: ");
                    sb.append(subjectId);
                    sb.append(" = ");
                    sb.append(member);
                    LOGGER.debug(sb);
                }

            }
        }


        sessionUtil.stopSession(session);
        return member;
    }

    /**
     * Finds the description of a specified group or stem from the data store.
     * @param key The key of the group or stem to look for.
     * @return The group if it can be found, null otherwise.
     * @see org.esco.grouper.services.IGrouperAPIExposer#findGroupOrStem(java.lang.String)
     */
    public GrouperDTO findGroupOrStem(final String key) {

        final GrouperSession session = sessionUtil.createSession();
        final GroupOrStem gos = fetchGroupOrStem(session, key);
        GrouperDTO infos = null;

        if (gos != null) {
            infos = fetchGrouperData(gos);
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("GroupInformations for ");
            sb.append(key);
            sb.append(": ");
            sb.append(infos);
            LOGGER.debug(sb);
        }

        sessionUtil.stopSession(session);
        return infos;
    }

    /**
     * Gives the root groups from a given stem.
     * @param key The name of the stem.
     * @return The list of the groups in the specified stem its child stems.
     */
    public GrouperDTO[] getAllRootGroupsFromStem(final String key) {
        final GrouperSession session = sessionUtil.createSession();
        final Stem s = fetchStem(session, key);
        Set<GrouperDTO> groups = null;

        if (s != null) {
            groups = new HashSet<GrouperDTO>();
            Stack<Stem> stems = new Stack<Stem>();
            stems.add(s);
            while (!stems.isEmpty()) {
                final Stem currentStem = stems.pop();
                @SuppressWarnings("rawtypes")
                final Set currentChildGroups = currentStem.getChildGroups();
                @SuppressWarnings("rawtypes")
                final Set currentChildStems = currentStem.getChildStems();

                for (Object o : currentChildGroups) {
                    final Group g = (Group) o;
                    if (g.toMember().getImmediateMemberships().isEmpty()) {
                        groups.add(fetchGrouperData((Group) o));
                    }
                }

                for (Object o : currentChildStems) {
                    stems.add((Stem) o);
                }
            }
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Child groups for stem ");
            sb.append(key);
            sb.append(": ");
            sb.append(groups);
            LOGGER.debug(sb);
        }

        sessionUtil.stopSession(session);

        if (groups == null) {
            return null;
        }

        return groups.toArray(new GrouperDTO[groups.size()]);
    }

    /**
     * Gives subgroups of a group or stem.
     * @param key The name of the group or stem.
     * @return The subgroups.
     */
    public GrouperDTO[] getMemberGroups(final String key) {

        List<GrouperDTO> memberGroups = null;
        final GrouperSession session = sessionUtil.createSession();
        final GroupOrStem gos = fetchGroupOrStem(session, key);

        if (gos != null) {

            memberGroups = new ArrayList<GrouperDTO>();

            // The name denotes a group.
            if (gos.isGroup()) {

                final Group group = gos.asGroup();

                // Composite groups are flattened.
                if (!group.hasComposite()) {

                    @SuppressWarnings("rawtypes")
                    final Set members = group.getImmediateMemberships();


                    for (Object o : members) {
                        try {
                            Member mb = ((Membership) o).getMember();
                            if (mb.getSubjectType().equals(SubjectTypeEnum.GROUP)) {
                                memberGroups.add(fetchGrouperData(mb.toGroup()));
                            }
                        } catch (MemberNotFoundException e) {
                            LOGGER.error(e, e);
                        } catch (GroupNotFoundException e) {
                            LOGGER.error(e, e);
                        }
                    }
                }
                // The name denotes a stem.
            } else if (gos.isStem()) {
                final Stem stem = gos.asStem();

                @SuppressWarnings("rawtypes")
                final Set childStems = stem.getChildStems();
                for (Object childStem : childStems) {
                    memberGroups.add(fetchGrouperData((Stem) childStem));
                }

                @SuppressWarnings("rawtypes")
                final Set childGroups = stem.getChildGroups();
                for (Object childGroup : childGroups) {
                    memberGroups.add(fetchGrouperData((Group) childGroup));
                }
            }
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Member groups for ");
            sb.append(key);
            sb.append(": ");
            sb.append(memberGroups);
            LOGGER.debug(sb.toString());
        }

        sessionUtil.stopSession(session);
        if (memberGroups == null) {
            return null;
        }
        return memberGroups.toArray(new GrouperDTO[memberGroups.size()]);
    }

    /**
     * Gives all the groups and subjects that are members of this group or stem.
     * @param key The key of the target group or stem.
     * @return All the members of the group or stem denoted by the key.
     * @see org.esco.grouper.services.IGrouperAPIExposer#getAllMembers(java.lang.String)
     */
    public GrouperDTO[] getAllMembers(final String key) {

        Set<GrouperDTO> allMembers = null;
        final GrouperSession session = sessionUtil.createSession();
        final GroupOrStem gos = fetchGroupOrStem(session, key);

        if (gos != null) {

            // The name denotes a group.
            if (gos.isGroup()) {

                allMembers = getAllMembers(session, gos.asGroup());

            // The name denotes a stem.
            } else if (gos.isStem()) {
                allMembers = getAllMembers(session, gos.asStem());
            }
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("All Members for ");
            sb.append(key);
            sb.append(": ");
            sb.append(allMembers);
            LOGGER.debug(sb.toString());
        }

        sessionUtil.stopSession(session);
        if (allMembers == null) {
            return null;
        }
        return allMembers.toArray(new GrouperDTO[allMembers.size()]);
    }

    /**
     * Gives all the groups that are members of this group or stem.
     * @param key The key of the target group or stem.
     * @return All the groups members of the group or stem.
     */
    public GrouperDTO[] getAllMemberGroups(final String key) {

        Set<GrouperDTO> allMemberGroups = null;
        final GrouperSession session = sessionUtil.createSession();
        final GroupOrStem gos = fetchGroupOrStem(session, key);

        if (gos != null) {

            // The name denotes a group.
            if (gos.isGroup()) {

                allMemberGroups = getAllMemberGroups(session, gos.asGroup());

            // The name denotes a stem.
            } else if (gos.isStem()) {
                allMemberGroups = getAllMemberGroups(session, gos.asStem());
            }
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("All member groups for ");
            sb.append(key);
            sb.append(": ");
            sb.append(allMemberGroups);
            LOGGER.debug(sb.toString());
        }

        sessionUtil.stopSession(session);
        if (allMemberGroups == null) {
            return null;
        }
        return allMemberGroups.toArray(new GrouperDTO[allMemberGroups.size()]);
    }

    /**
     * Gives all the subjects that are members of this group or stem.
     * @param key The key of the target group or stem.
     * @return All the subjects members of the group or stem.
     */
    public GrouperDTO[] getAllMemberSubjects(final String key) {
        Set<GrouperDTO> allMemberSubjects = null;
        final GrouperSession session = sessionUtil.createSession();
        final GroupOrStem gos = fetchGroupOrStem(session, key);

        if (gos != null) {

            // The name denotes a group.
            if (gos.isGroup()) {

                allMemberSubjects = getAllMemberSubjects(session, gos.asGroup());

            // The name denotes a stem.
            } else if (gos.isStem()) {
                allMemberSubjects = getAllMemberSubjects(session, gos.asStem());
            }
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("All member subjects for ");
            sb.append(key);
            sb.append(": ");
            sb.append(allMemberSubjects);
            LOGGER.debug(sb.toString());
        }

        sessionUtil.stopSession(session);
        if (allMemberSubjects == null) {
            return null;
        }
        return allMemberSubjects.toArray(new GrouperDTO[allMemberSubjects.size()]);
    }


    /**
     * Gives the members of type subject of the group.
     * @param key The name of the group.
     * @return The keys of the terminal elements members of the group
     * (i.e. without the subgroups, but with the subgroups members).
     */
    public GrouperDTO[] getMemberSubjects(final String key) {

        List<GrouperDTO> memberSubjects = null;
        final GrouperSession session = sessionUtil.createSession();
        final GroupOrStem gos = fetchGroupOrStem(session, key);

        if (gos != null) {

            if (gos.isGroup()) {
                final Group group = gos.asGroup();


                @SuppressWarnings("rawtypes")
                Set members = null;

                // Composite groups are flattened.
                if (group.hasComposite()) {
                    members = group.getCompositeMembers();
                } else {
                    members = group.getImmediateMembers();
                }

                memberSubjects = new ArrayList<GrouperDTO>();
                for (Object o : members) {
                    Member mb = (Member) o;
                    if (mb.getSubjectType().equals(SubjectTypeEnum.PERSON)) {
                        try {
                            memberSubjects.add(fetchGrouperData(mb.getSubject()));
                        } catch (SubjectNotFoundException e) {
                            LOGGER.error(e, e);
                        }
                    }
                }
            }
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Members subjects for ");
            sb.append(key);
            sb.append(": ");
            sb.append(memberSubjects);
            LOGGER.debug(sb.toString());
        }

        sessionUtil.stopSession(session);
        if (memberSubjects == null) {
            return null;
        }
        return memberSubjects.toArray(new GrouperDTO[memberSubjects.size()]);
    }



    /**
     * Gives the group descriptions of the groups to which a given subject belongs to.
     * @param subjectKey The key of the considered subject.
     * @return The list of the groups which the subject identified by subjectKey belongs to.
     * @see org.esco.grouper.services.IGrouperAPIExposer
     * #getMembershipsForSubject(java.lang.String)
     */
    public GrouperDTO[] getMembershipsForSubject(final String subjectKey) {

        final Subject subject = fetchSubject(subjectKey);
        if (subject == null) {
            return null;
        }

        final GrouperSession session = sessionUtil.createSession();
        final Member member = fetchMember(session, subject);
        GrouperDTO[] infos = null;
        if (member != null) {
            infos = fetchGrouperData(member.getMemberships(), null);
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Memberships for subject ");
            sb.append(subjectKey);
            sb.append(": ");
            int cpt = 0;
            if (infos != null) {
                for (GrouperDTO info : infos) {
                    if (cpt++ > 0) {
                        sb.append(", ");
                    }
                    sb.append(info);
                }
            } else {
                sb.append(infos);
            }
            LOGGER.debug(sb);
        }

        sessionUtil.stopSession(session);
        return infos;
    }

    /**
     * if the key denotes a group, gives the descriptions of the groups to which a given group belongs to.
     * The description of the parent stem is returned too.
     * If the key denotes a stem, the parent stem is returned.
     * @param  key the key of the group or stem.
     * @return The list of the groups which the group identified by key belongs + the parent stem.
     * @see org.esco.grouper.services.IGrouperAPIExposer
     * #getMembershipsForGroupOrStem(java.lang.String)
     */
    public GrouperDTO[] getMembershipsForGroupOrStem(final String key) {

        GrouperDTO[] infos = null;
        final GrouperSession session = sessionUtil.createSession();
        final GroupOrStem gos = fetchGroupOrStem(session, key);

        if (gos != null) {
            if (gos.isGroup()) {
                final Group group = gos.asGroup();
                infos = fetchGrouperData(group.toMember().getImmediateMemberships(), group.getParentStem());
            } else if (gos.isStem()) {
                final Stem stem = gos.asStem();
                if (!stem.isRootStem()) {
                    try {
                        infos = new GrouperDTO[1];
                        infos[0] = fetchGrouperData(stem.getParentStem());
                    } catch (StemNotFoundException e) {
                        LOGGER.error(e, e);
                    }
                }
            }
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Memberships for group ");
            sb.append(key);
            sb.append(": ");
            int cpt = 0;

            if (infos != null) {
                for (GrouperDTO info : infos) {
                    if (cpt++ > 0) {
                        sb.append(", ");
                    }
                    sb.append(info);

                }
            } else {
                sb.append(infos);
            }
            LOGGER.debug(sb);
        }

        sessionUtil.stopSession(session);
        return infos;
    }

    /**
     * Checks the initialization of the bean.
     * @throws Exception
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        /* Nothing to do */
    }

    /**
     * Search groups or stems whose name matches the query string according to the specified method.
     * @param query The part of the name of the group.
     * @param method The method used to perform the comparison.
     * @return The array of groups or stem descriptions whose name match query according
     * to the method of comparison.
     * @see org.esco.grouper.services.IGrouperAPIExposer
     * #searchForGroupsOrStems(java.lang.String, int)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public GrouperDTO[] searchForGroupsOrStems(final String query, final int method) {

        final GrouperSession session = sessionUtil.createSession();
        final Stem root = StemFinder.findRootStem(session);
        final QueryFilter groupFilter = new GroupNameFilter(query, root);
        final QueryFilter stemFilter = new StemNameFilter(query, root);
        List<GrouperDTO> filtred = null;

        try {
            // Retrieves the candidates.
            final GrouperQuery gQuery = GrouperQuery.createQuery(session,  groupFilter);
            final GrouperQuery sQuery = GrouperQuery.createQuery(session,  stemFilter);
            final Set candidates = gQuery.getGroups();
            candidates.addAll(sQuery.getStems());


            // Filters the candidates according to the specified method.
            filtred = new ArrayList<GrouperDTO>();
            for (Object o : candidates) {
                final Owner candidate = (Owner) o;
                switch (method) {
                case IGroupConstants.IS:
                    if (candidate.getName().equals(query)) {
                        if (candidate instanceof Group) {
                            filtred.add(fetchGrouperData((Group) candidate));
                        } else {
                            filtred.add(fetchGrouperData((Stem) candidate));
                        }
                    }
                    break;
                case IGroupConstants.ENDS_WITH:
                    if (candidate.getName().endsWith(query)) {
                        if (candidate instanceof Group) {
                            filtred.add(fetchGrouperData((Group) candidate));
                        } else {
                            filtred.add(fetchGrouperData((Stem) candidate));
                        }
                    }
                    break;
                case IGroupConstants.STARTS_WITH:
                    if (candidate.getName().startsWith(query)) {
                        if (candidate instanceof Group) {
                            filtred.add(fetchGrouperData((Group) candidate));
                        } else {
                            filtred.add(fetchGrouperData((Stem) candidate));
                        }
                    }
                    break;
                case IGroupConstants.CONTAINS:
                    if (candidate instanceof Group) {
                        filtred.add(fetchGrouperData((Group) candidate));
                    } else {
                        filtred.add(fetchGrouperData((Stem) candidate));
                    }
                    break;
                default:
                    final StringBuffer sb = new StringBuffer("Unsupported method: ");
                sb.append(method);
                sb.append(" (See availaible value in ");
                sb.append(IGroupConstants.class.getName());
                sb.append(")");
                throw new EscoGrouperException(sb.toString());
                }
            }

        } catch (QueryException e) {
            LOGGER.error(e, e);
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Search for Groups, query=");
            sb.append(query);
            sb.append(" method=");
            sb.append(method);
            sb.append(": ");
            sb.append(filtred);
            LOGGER.debug(sb.toString());
        }

        sessionUtil.stopSession(session);

        if (filtred == null) {
            return null;
        }
        return filtred.toArray(new GrouperDTO[filtred.size()]);
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

        // Translation of the method in a pattern.
        String pattern;
        switch (method) {
        case IGroupConstants.IS:
            pattern = query;
            break;
        case IGroupConstants.ENDS_WITH:
            pattern = "*" + query;
            break;
        case IGroupConstants.STARTS_WITH:
            pattern = query + "*";
            break;
        case IGroupConstants.CONTAINS:
            pattern = "*" + query + "*";
            break;
        default:
            final StringBuffer sb = new StringBuffer("Unsupported method: ");
        sb.append(method);
        sb.append(" (See availaible value in ");
        sb.append(IGroupConstants.class.getName());
        sb.append(")");
        throw new EscoGrouperException(sb.toString());
        }

        // Retrieves the Subjects.
        @SuppressWarnings("rawtypes")
        final Set candidates = SubjectFinder.findAll(pattern);

        // Retrieves the ids of the subjects.
        final List<String> ids = new ArrayList<String>();
        for (Object o : candidates) {
            ids.add(((Subject) o).getId());
        }

        if (LOGGER.isDebugEnabled()) {
            final StringBuffer sb = new StringBuffer("Search for Subjects, query=");
            sb.append(query);
            sb.append(" method=");
            sb.append(method);
            sb.append(": ");
            int cpt = 0;
            for (String id : ids) {
                if (cpt++ > 0) {
                    sb.append(", ");
                }
                sb.append(id);

            }
            LOGGER.debug(sb.toString());
        }
        return ids.toArray(new String[ids.size()]);
    }

//    public static void main(final String[] args) {
//        ThreadLocal<ApplicationContext> appCtx = new ThreadLocal<ApplicationContext>();
//        appCtx.set(new FileSystemXmlApplicationContext("classpath:applicationContext.xml"));
//        BeanFactory beanFactory = appCtx.get();
//        IGrouperAPIExposer exp = (IGrouperAPIExposer) beanFactory.getBean("GrouperServiceExposer");
//        System.out.println("--------------- hasDeepMember ---------------");
//        String subjectId = "F089032o";
//        System.out.println("1 stem y ==>" + exp.hasDeepMember("esco", subjectId));
//        System.out.println("2 direct group y ==>"
//                + exp.hasDeepMember("esco:Etablissements:LEONARD DE VINCI_0370001A:Parents", subjectId));
//        System.out.println("3 indirect group y ==>"
//                + exp.hasDeepMember("esco:Etablissements:LEONARD DE VINCI_0370001A:Tous_LEONARD DE VINCI",
//                        subjectId));
//        System.out.println("4 direct stem y ==>"
//                + exp.hasDeepMember("esco:Etablissements:LEONARD DE VINCI_0370001A", subjectId));
//        System.out.println("5 indirect stem y ==>" + exp.hasDeepMember("esco:admin:local", subjectId));
//        System.out.println("6 stem n ==>" + exp.hasDeepMember("esco:Applications", subjectId));
//        System.out.println("7 group n ==>"
//          + exp.hasDeepMember("esco:Applications:newsPortlet:LEGTA_VENDOME_BLOIS_MONTOIRE",
//                subjectId));
//        System.out.println("8 incorect group or stem n ==>" + exp.hasDeepMember("esco:Applications_", subjectId));
//        System.out.println("9 incorect subject n ==>" + exp.hasDeepMember("esco:Applications", "x"));
//
//        System.out.println("\n\n--------------- hasMember ---------------");
//        System.out.println("1 stem n ==>" + exp.hasMember("esco", subjectId));
//        System.out.println(" direct group y ==>"
//                + exp.hasMember("esco:Etablissements:LEONARD DE VINCI_0370001A:Parents",
//                        subjectId));
//        System.out.println("3 indirect group n ==>"
//                + exp.hasMember("esco:Etablissements:LEONARD DE VINCI_0370001A:Tous_LEONARD DE VINCI",
//                        subjectId));
//
//
//    }
}
