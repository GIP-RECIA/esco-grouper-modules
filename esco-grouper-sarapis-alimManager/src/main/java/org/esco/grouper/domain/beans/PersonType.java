/**
 * Copyright © 2008 GIP-RECIA (https://www.recia.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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