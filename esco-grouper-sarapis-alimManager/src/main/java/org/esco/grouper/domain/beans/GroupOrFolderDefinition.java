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
package org.esco.grouper.domain.beans;

import edu.internet2.middleware.grouper.Stem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.esco.grouper.exceptions.UnknownTemplateElementTempateElement;

/**
 * Class for the definition of groups or folder.
 * A definition can be a template that can be instanciate on
 * attributes values.
 * For instance the extension could be %ETABLISSEMENT_UAI% wich can be used then to
 * instanciate the definition for each value of the attribute denoted by the key
 * %ETABLISSEMENT_UAI%.
 * The mapping between the key and its value is done by the method instanciateTemplate(..).
 * @author GIP RECIA - A. Deman
 * 31 juil. 08
 *
 */
public class GroupOrFolderDefinition implements Serializable, Cloneable {
    /** Serial version UID.*/
    private static final long serialVersionUID = 5441857690289508441L;

    /** Flag used to determine if the definition denotes a group or a folder. */
    private boolean folder;

    /** The description of the group or folder. */
    private EvaluableString description;

    /** The extension of the group or folder. */
    private EvaluableString extension;

    /** The display extension of the group or folder.*/
    private EvaluableString displayExtension;

    /** The containing path of the folder or group. */
    private ReversibleEvaluableString containingPath;

    /** The definition of the members of the group. */
    private List<MembersDefinition> membersDefinitions;

    /** The privileges. */
    private List<PrivilegeDefinition> privileges;

    /** The path of the groups that contains this group (may be a template). */
    private ContrainingGroupsDefinition containingGroupsPaths;

    /** Flag used to determine if the group or folder is preexisting. */
    private boolean preexisting = true;

    /** Flag used to determine if the group or folder has to be created. */
    private boolean create;

    /** The hash code.*/
    private int hashcode;

    /**
     * Builds an empty instance of GroupOrFolderDefinition.
     */
    protected GroupOrFolderDefinition() {
        super();
    }

    /**
     * Builds an instance of GroupOrFolderDefinition.
     * @param folder True if the definition denotes a folder.
     * @param preexisting The flag used to determine if the folder is built or is preexisting.
     * @param create The flag used to determine if the group has to be created even
     * if there is no members.
     * @param containingPath The containing path.
     * @param extension The extension of the group or folder.
     * @param displayExtension The display extension of the group or folder.
     * @param description The description of the group or folder.
     * @throws UnknownTemplateElementTempateElement If there is a template element in a string
     * which is unknown.
     */
    public GroupOrFolderDefinition(final boolean folder,
            final boolean preexisting,
            final boolean create,
            final String containingPath,
            final String extension,
            final String displayExtension,
            final String description) throws UnknownTemplateElementTempateElement {
        this.folder = folder;
        this.preexisting = preexisting;
        this.create = create;
        this.containingPath = new ReversibleEvaluableString(containingPath);
        this.extension = new EvaluableString(extension);
        this.displayExtension = new EvaluableString(displayExtension);
        this.description = new EvaluableString(description);
    }

    /**
     * Evaluates a template by replacing the template elements by a value.
     * @param values The values to used to perform the evaluation of the template elements.
     * @return The GroupOrFolderDefinition with the correct values.
     */
    public GroupOrFolderDefinition evaluateTemplate(final String...values) {

        final EvaluableString newExtension = extension.evaluate(values);

        final EvaluableString newDisplayExtension = displayExtension.evaluate(values);

        final EvaluableString newDescription = description.evaluate(values);

        final ReversibleEvaluableString newContainingPath = containingPath.evaluate(values);

        GroupOrFolderDefinition gofd = new GroupOrFolderDefinition();
        gofd.folder = folder;
        gofd.preexisting = preexisting;
        gofd.containingPath = newContainingPath;
        gofd.extension = newExtension;
        gofd.displayExtension = newDisplayExtension;
        gofd.description = newDescription;
        gofd.membersDefinitions = membersDefinitions;
        gofd.containingGroupsPaths = containingGroupsPaths;
        gofd.privileges = privileges;
        return gofd;
    }

    /**
     * Gives the path associated to this definition.
     * If this definition is a template, then the returned path is also a template.
     * @return The path.
     */
    public String getPath() {
        if (isRoot()) {
            return extension.getString();
        }
        return containingPath + Stem.DELIM + extension;
    }

    /**
     * Computes the has code for this instance.
     * @return The hash value for this instance.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (hashcode == 0) {
            hashcode = getPath().hashCode();
        }
        return hashcode;
    }

    /**
     * Test if the instance is equal to an object.
     * @param o The object to test.
     * @return True if the object is equal to this one.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if (!(o instanceof GroupOrFolderDefinition)) {
            return false;
        }

        final GroupOrFolderDefinition gofd = (GroupOrFolderDefinition) o;
        if (!gofd.extension.equals(extension)) {
            return false;
        }
        return gofd.containingPath.equals(containingPath);
    }

    /**
     * Tests if the definition denotes a group.
     * @return True if the definition denotes a group.
     */
    public boolean isGroup() {
        return !folder;
    }

    /**
     * Tests if the definition denotes a folder.
     * @return True if the definition denotes a folder.
     */
    public boolean isFolder() {
        return folder;
    }

    /**
     * Tests if the method denotes a template definition.
     * @return True if the definition denotes a template.
     */
    public boolean isTemplate() {
        if (!extension.isEvaluated()) {
            return true;
        }
        if (!displayExtension.isEvaluated()) {
            return true;
        }
        if (!description.isEvaluated()) {
            return true;
        }
        if (!containingPath.isEvaluated()) {
            return true;
        }
        if (isRoot()) {
            return false;
        }
        if (containingGroupsPaths == null) {
            return false;
        }
        return !containingGroupsPaths.isEvaluated();
    }

    /**
     * Getter for description.
     * @return description.
     */
    public String getDescription() {
        return description.getString();
    }

    /**
     * Getter for extension.
     * @return extension.
     */
    public String getExtension() {
        return extension.getString();
    }

    /**
     * Getter for displayExtension.
     * @return displayExtension.
     */
    public String getDisplayExtension() {
        return displayExtension.getString();
    }

    /**
     * Getter for containingPath.
     * @return containingPath.
     */
    public String getContainingPath() {
        return containingPath.getString();
    }

    /**
     * Gives the containing path as a template.
     * @return containingPath as a template.
     */
    public String getContainingPathAsTemplate() {
        return containingPath.getTemplateString();
    }

    /**
     * Gives the number of groups who have privileges
     * on the group or folder denoted by this definition.
     * @return The number of groups that have privileges.
     */
    public int countPrivileges() {
        if (privileges == null) {
            return 0;
        }
        return privileges.size();
    }

    /**
     * Gives the privilege at a given position.
     * @param index The position of the privilege in the list.
     * @return the privilege at the specified position
     */
    public PrivilegeDefinition getPrivilege(final int index) {
        return privileges.get(index);
    }

    /**
     * Adds a privilege.
     * @param privilege The privilege to add.
     */
    public void addPrivilege(final PrivilegeDefinition privilege) {
        if (privileges == null) {
            privileges = new ArrayList<PrivilegeDefinition>();
        }
        privileges.add(privilege);
    }

    /**
     * Gives the number of members definitions for the group or folder definition.
     * @return The number of members definition for this group or folder
     * definition.
     */
    public int countMembersDefinitions() {
        if (membersDefinitions == null) {
            return 0;
        }
        return membersDefinitions.size();
    }

    /**
     * Gives a specified members definition.
     * @param index The position of the target members definition.
     * @return The members definition at the specified position.
     */
    public MembersDefinition getMembersDefiniton(final int index) {
        return membersDefinitions.get(index);
    }

    /**
     * Adds a definition of members.
     * @param membersDefinition The members definition to add to this group.
     */
    public void addMembersDefinition(final MembersDefinition membersDefinition) {
        if (membersDefinitions == null) {
            membersDefinitions = new ArrayList<MembersDefinition>();
        }
        membersDefinitions.add(membersDefinition);
    }

    /**
     * Gives the number of path for the group or folder definition.
     * @return The number of membership paths for this group or folder
     * definition.
     */
    public int countContainingGroupsPaths() {
        if (containingGroupsPaths == null) {
            return 0;
        }
        return containingGroupsPaths.countEvaluableStringConditions();
    }

    /**
     * Gives a specified containing group path.
     * @param index The position of the target membership path.
     * @return The membership path at the specified position.
     */
    public EvaluableStringCondition getContainingGroupPath(final int index) {
        return containingGroupsPaths.getEvaluableStringCondition(index);
    }

    /**
     * Adds a containing group path.
     * @param containingGroupPath The path of the containing group.
     * @param condition
     * @throws UnknownTemplateElementTempateElement If there is a template element in a string
     * which is unknown.
     */
    public void addContainingGroupPath(final String containingGroupPath, final Pattern condition) throws UnknownTemplateElementTempateElement {
        if (containingGroupsPaths == null) {
            containingGroupsPaths = new ContrainingGroupsDefinition();
        }
        containingGroupsPaths.addEvaluableStringCondition(containingGroupPath, condition);
    }

    /**
     * Gives the string representation of this group or folder definition.
     * @return The string that represents this definition.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        final StringBuilder sb = new StringBuilder("(");

        if (isPreexisting()) {
            sb.append("Preexisting ");
        }

        if (isGroup()) {
            sb.append("Group, ");
        } else {
            sb.append("Folder,");
        }
        if (isRoot()) {
            sb.append("<root>");
        } else {
            sb.append(containingPath);
        }
        sb.append(", ");
        sb.append(extension);
        sb.append(", ");
        sb.append(displayExtension);
        sb.append(", ");
        sb.append(description);

        if (countMembersDefinitions() > 0) {
            sb.append(", Members definitions: ");
            for (int i = 0; i < countMembersDefinitions(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(getMembersDefiniton(i));
            }

        }

        if (countContainingGroupsPaths() > 0) {
            sb.append(", Memberships ");
            sb.append(containingGroupsPaths);
        }

        if (countPrivileges() > 0) {
            sb.append(", Privileges: ");
            for (int i = 0; i < countPrivileges(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(getPrivilege(i));
            }

        }

        sb.append(")");
        return sb.toString();
    }

    /**
     * Getter for preexisting.
     * @return preexisting.
     */
    public boolean isPreexisting() {
        return preexisting;
    }

    /**
     * Tests if the current definition is a root.
     * @return True if the definition has no containing path.
     */
    public boolean isRoot() {
        return containingPath.isEmpty();
    }

    /**
     * Getter for create.
     * @return create.
     */
    public boolean isCreate() {
        return create;
    }
}
