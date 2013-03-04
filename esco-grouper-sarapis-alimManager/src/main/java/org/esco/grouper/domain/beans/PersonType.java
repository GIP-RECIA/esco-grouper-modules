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

package org.esco.grouper.domain.beans;

import java.io.Serializable;

import org.esco.grouper.exceptions.EscoGrouperException;

/**
 * The type of a person.
 * @author GIP RECIA - A. Deman
 * 4 déc. 08
 * @modifier GIP RECIA J. Gribonvald
 * 14 fev. 2012
 */
public enum PersonType implements Serializable {
    /** All types.*/
    ALL,

    /** The members are the teachers.*/
    TEACHER,

    /** The members are the students.*/
    STUDENT,

    /** The members are the administrative employee.*/
    ADMINISTRATIVE,

    /** The members are the TOS employee.*/
    COLLECTIVITE,

    /** The members are the parents. */
    PARENT,

    /** Le membre est issue d'un service académique. */
    ACADEMIC,

    /** Le membre est un tuteur de stage. */
    TUTEURSTAGE,

    /** Le membre est un responsable d'entreprise. */
    CHIEFENTREPRISE,

    /** Le membre est un personnel exterieur. */
    EXTERNAL,

    /** Custom type for a specific local use. */
    LOCAL1,

    /** Custom type for a specific local use. */
    LOCAL2,

    /** Custom type for a specific local use. */
    LOCAL3,

    /** Custom type for a specific local use. */
    LOCAL4,

    /** Custom type for a specific local use. */
    LOCAL5;


    /**
     * Parse a string to a PersonType Instance.
     * @param value The value to parse.
     * @return The PersonType that is equal to the value if it exists,
     * null otherwise.
     */
    public static PersonType parseIgnoreCase(final String value) {
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EscoGrouperException("Invalid Type of member: " + value
                    + ". Legal values are: "
                    + PersonType.STUDENT + ", "
                    + PersonType.TEACHER + ", "
                    + PersonType.PARENT + ", "
                    + PersonType.ADMINISTRATIVE + ", "
                    + PersonType.COLLECTIVITE + ", "
                    + PersonType.ACADEMIC + ", "
                    + PersonType.TUTEURSTAGE + ", "
                    + PersonType.CHIEFENTREPRISE + ", "
                    + PersonType.EXTERNAL + ", "
                    + PersonType.LOCAL1 + ", "
                    + PersonType.LOCAL2 + ", "
                    + PersonType.LOCAL3 + ", "
                    + PersonType.LOCAL4 + ", "
                    + PersonType.LOCAL5 + ", "
                    + PersonType.ALL + ".", e);
        }
    }
}