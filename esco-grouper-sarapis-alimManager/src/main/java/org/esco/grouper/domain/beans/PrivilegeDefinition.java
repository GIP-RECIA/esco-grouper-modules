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

import java.io.Serializable;

import org.esco.grouper.exceptions.EscoGrouperException;


/**
 * Definition of a privilege.
 * This definition associate a right (Read, write or All) and a path to a group.
 * This path can be an evaluable string (i.e. with template elements like esco:admin:local%ETAB%:%CLASSE%).
 * @author GIP RECIA - A. Deman
 * 1 août 08
 *
 */
public class PrivilegeDefinition implements Serializable {


    /** Serial default UID.*/
    private static final long serialVersionUID = 5449177734260605352L;

    /** The type of right. */
    public static enum Right {
        /** Admin right.*/
        ADMIN,

        /** Update privilege. */
        UPDATE,

        /** Creation of folder.*/
        FOLDER_CREATION,

        /** Creation of Group. */
        GROUP_CREATION,

        /** View privilege.*/
        VIEW,

        /** Read privilege.*/
        READ;

        /**
         * Parses a string to a Right instance.
         * @param value The value to parse.
         * @return The Right that is equal to the value if it exists,
         * null otherwise.
         */
        public static Right parseIgnoreCase(final String value) {
            try {
                return valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new EscoGrouperException("Invalid Type of privilege: " + value
                        + ". Legal values are: "
                        + Right.ADMIN + ", "
                        + Right.UPDATE + ", "
                        + Right.VIEW + ", "
                        + Right.READ + ", "
                        + Right.FOLDER_CREATION + ", "
                        + Right.GROUP_CREATION + ".", e);
            }
        }

    }

    /** The type of members of the group. */
    private Right privilege;

    /** The path that has the privilege. */
    private EvaluableString path;

    /**
     * Builds an instance of PrivilegeDefinition.
     * @param privilege The privilege.
     * @param path The path that has the privilege.
     */
    public PrivilegeDefinition(final String privilege, final EvaluableString path) {
        this.privilege = Right.parseIgnoreCase(privilege);
        this.path = path;
    }

    /**
     * Gives the string representation of this members definition.
     * @return The String that represents this members definition.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("Privilege def. [");
        if (privilege != null) {
            sb.append(privilege);
        }
        sb.append(" - ");
        if (path != null) {
            sb.append(path);
        }
        sb.append("]");
        return sb.toString();
    }


    /**
     * Computes the hashcode for this defitinion.
     * @return The hashcode for this defintion.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int mult = 31;
        int hashcode = 0;
        if (privilege != null) {
            hashcode = privilege.hashCode();
        }
        if (path != null) {
            hashcode = hashcode + mult * path.hashCode();
        }
        return hashcode;
    }

    /**
     * Tests if this instance is equal to another object.
     * @param obj The object to test.
     * @return True if the tested object is equal to this instance.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PrivilegeDefinition)) {
            return false;
        }
        final PrivilegeDefinition other = (PrivilegeDefinition) obj;
        if (privilege == null) {
            if (other.privilege != null) {
                return false;
            }
        } else if (!privilege.equals(other.privilege)) {
            return false;
        }
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        }
        return path.equals(other.path);
    }

    /**
     * Getter for privilege.
     * @return privilege.
     */
    public Right getPrivilege() {
        return privilege;
    }

    /**
     * Getter for path.
     * @return path.
     */
    public EvaluableString getPath() {
        return path;
    }


}
