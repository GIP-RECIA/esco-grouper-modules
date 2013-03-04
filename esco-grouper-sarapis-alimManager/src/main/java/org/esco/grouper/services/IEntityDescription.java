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

package org.esco.grouper.services;

import java.io.Serializable;
import java.util.Collection;

import org.esco.grouper.domain.beans.PersonType;

/**
 * Interface for the description of entities that can be members
 * of a group.
 * @author GIP RECIA - A. Deman
 * 9 December 2009
 *
 */
public interface IEntityDescription extends Serializable {


    /**
     * Gives the type of the person.
     * @return The type of the person.
     */
    PersonType getType();

    /**
     * Tests if there is a type information.
     * @return True if there is a type information.
     */
    boolean hasTypeInformation();

    /**
     * Sets the person type.
     * @param type The new person type.
     */
    void setType(final PersonType type);

    /**
     * Gives the id of the person.
     * @return The id of the person.
     */
    String getId();

    /**
     * Sets the id.
     * @param id The new id.
     */
    void setId(final String id);

    /**
     * Tests if there is an id information.
     * @return True if there is an id information.
     */
    boolean hasIdInformation();

    /**
     * Sets a value for the attribute to a given position.
     * @param position The position of the attribute value.
     * @param value The value to set.
     */
    void setAttributeValue(final int position, final String value);

    /**
     * Sets a value for the attribute to a given position.
     * @param position The position of the attribute value.
     * @return The value associated to the attribute at the position position.
     */
    String getAttributeValue(final int position);

    /**
     * The grouped attributes can be used when only one attribute has different values. The aim is to reduce
     * the requests.
     * For instance if an entity has those attributes values: <br/>
     * 1 => v1<br/>
     * 2 => v2.1, v2.2<br/>
     * 3 => None
     * 4 => v4<br/>
     *
     * Using setGroupedAttributeValues(2, &lt;v2.1, v2.2&gt; while produce the result:
     * [v1, v2.1, "", v4] and [v1, v2.2, "", v4]<br/>
     * @param position The position of the grouped attribute values.
     * @param groupedAttributeValues The distinct values for the attribute at the position index.
     */
    void setGroupedAttributeValues(final int position, final Collection<String> groupedAttributeValues);

    /**
     * The grouped attributes can be used when only one attribute has different values. The aim is to reduce
     * the requests.
     * For instance if an entity has those attributes values: <br/>
     * 1 => v1<br/>
     * 2 => v2.1, v2.2<br/>
     * 3 => None
     * 4 => v4<br/>
     *
     * Using setGroupedAttributeValues(2, &lt;v2.1, v2.2&gt; while produce the result:
     * [v1, v2.1, "", v4] and [v1, v2.2, "", v4]<br/>
     * @param position The position of the grouped attribute values.
     * @param groupedAttributeValues The distinct values for the attribute at the position index.
     */
    void setGroupedAttributeValues(final int position, final String[] groupedAttributeValues);


    /**
     * Gives the grouped attribute values.
     * @return The values for the attribute.
     */
    String[] getGroupedAttributeValues();

    /**
     * Tests if a grouped attribute values is used.
     * @return True if a grouped attribute values is used.
     */
    boolean hasGroupedAttributeValues();

    /**
     * Test is an attribute is set for a given position.
     * @param position The position to test.
     * @return True if the attribute is set for the given position.
     */
    boolean hasAttributeValue(final int position);

    /**
     * Gives the values.
     * @return The values.
     */
    String[][]  getValuesArrays();

}
