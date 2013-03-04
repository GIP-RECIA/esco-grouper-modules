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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

import org.esco.grouper.domain.beans.PersonType;

/**
 * Reference implementation of anIEntityDescription.
 * This class can be overridden in order to facilitate the manipulation of the
 * attributes values.
 * @author GIP RECIA - A. Deman
 * 9 December 2009
 *
 */
public class EntityDescriptionImpl implements IEntityDescription {


	/** Serial version UID.*/
	private static final long serialVersionUID = 844453196203572444L;

	/** Values of the attributes with the order used to give them to the web service. */
	private List<String> values = new ArrayList<String>();

	/** Order of the grouped attribute values, if used. */
	private int groupedAttributeValuesPosition = -1;

	/** Grouped attribute values. */
	private String[] groupedAttributeValues;

    /** The type of the person. */
    private PersonType type;

    /** The Id of the person. */
    private String id;

    /** The position with the max value. */
    private int maxPostionValue = -1;

    /**
     * Builds an instance of EntityDescriptionImpl.
     * @param id The id of the person.
     */
    public EntityDescriptionImpl(final String id) {
    	this.id = id;
    }

	/**
	 * {@inheritDoc}
	 * @see org.esco.grouper.services.IEntityDescription#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 * @see org.esco.grouper.services.IEntityDescription#getType()
	 */
	public PersonType getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 * @see org.esco.grouper.services.IEntityDescription#hasIdInformation()
	 */
	public boolean hasIdInformation() {
		return !"".equals(id) && id != null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.esco.grouper.services.IEntityDescription#hasTypeInformation()
	 */
	public boolean hasTypeInformation() {
		return type != null;
	}

	/**
	 * {@inheritDoc}
	 * @throws IllegalStateException if the value for the position position is already set,
	 * for another attribute value or for the groupedAttributeValues.
	 * setAttributeValue(int, java.lang.String)
	 */
	public void setAttributeValue(final int position, final String value) {

		// Checks that the position is not already used for the groupedAttributeValues.
		if (position <= maxPostionValue) {
			if (groupedAttributeValuesPosition == position) {
				throw new IllegalStateException("The position " + position
						+ " is already used by the property groupedAttributeValuesPosition.");
			}
		}

		// Handles the new position.
		handleNewPosition(position);

		if (position < values.size()) {
			values.set(position, value);
		} else {
			values.add(value);
		}

	}


	/**
	 * {@inheritDoc}
	 * @see org.esco.grouper.services.IEntityDescription#getAttributeValue(int)
	 */
	public String getAttributeValue(final int position) {
		if (position > maxPostionValue) {
			throw new NoSuchElementException("Request element at position: "
					+ position + " - Max position: " + maxPostionValue);
		}

		if (groupedAttributeValuesPosition == position) {
			return groupedAttributeValues.toString();
		}

		return values.get(position);

	}

	/**
	 * Handles a new position.
	 * @param position The new position to handle.
	 */
	private void handleNewPosition(final int position) {
		if (position > maxPostionValue) {
			if (maxPostionValue > 0) {

				// The test i <= position is for the case of a groupedAttributeValuesPosition
				// In the last position.
				for (int i = maxPostionValue + 1; i <= position; i++) {
					if (i < values.size()) {
						values.set(i, "");
					} else {
						values.add("");
					}
				}
			}
			maxPostionValue = position;
		}
	}

	/**
	 * {@inheritDoc}
	 * @throws IllegalStateException If the property groupedAttributeValues is already set
	 * or if the position is already used.
	 */
	public void setGroupedAttributeValues(final int position, final Collection<String> groupedAttributeValues) {

		setGroupedAttributeValues(position,
				groupedAttributeValues.toArray(new String[groupedAttributeValues.size()]));
	}

	/**
	 * {@inheritDoc}
	 * @see org.esco.grouper.services.IEntityDescription#
	 * setGroupedAttributeValues(int, java.lang.String[])
	 */
	public void setGroupedAttributeValues(final int position, final String[] groupedAttributeValues) {

		// Checks that the position is not already used for a single attribute value.
		if (position <= maxPostionValue && position < values.size()) {
			if (!"".equals(values.get(position))) {
				throw new IllegalStateException("The position " + position
						+ " is already used for an attribute value.");
			}
		}

		handleNewPosition(position);
		groupedAttributeValuesPosition = position;
		this.groupedAttributeValues = groupedAttributeValues;
	}

	/**
	 * {@inheritDoc}
	 * @see org.esco.grouper.services.IEntityDescription#getGroupedAttributeValues()
	 */
	public String[] getGroupedAttributeValues() {
		return groupedAttributeValues;

	}

	/**
	 * {@inheritDoc}
	 * @see org.esco.grouper.services.IEntityDescription#hasGroupedAttributeValues()
	 */
	public boolean hasGroupedAttributeValues() {
		if (groupedAttributeValuesPosition < 0) {
			return false;
		}
		if (groupedAttributeValues == null) {
			return false;
		}
		return groupedAttributeValues.length > 0;
	}


	/**
	 * {@inheritDoc}
	 * @see org.esco.grouper.services.IEntityDescription#setId(java.lang.String)
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 * @see org.esco.grouper.services.IEntityDescription#
	 * setType(org.esco.grouper.domain.beans.PersonType)
	 */
	public void setType(final PersonType type) {
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 * @see org.esco.grouper.services.IEntityDescription#getValuesArrays()
	 */
	public String[][] getValuesArrays() {
		String[][] valuesList;
		if (hasGroupedAttributeValues()) {
			valuesList = new String[groupedAttributeValues.length][];
			int index = 0;
			for (String attrValue : groupedAttributeValues) {
				final String[]  attValArr = values.toArray(new String[maxPostionValue]);
				attValArr[groupedAttributeValuesPosition] = attrValue;
				valuesList[index++] = attValArr;
			}
		} else {
			valuesList = new String[1][];
			valuesList[0] =  values.toArray(new String[values.size()]);
		}


		return valuesList;
	}


	/**
	 * {@inheritDoc}
	 * @see org.esco.grouper.services.IEntityDescription#hasAttributeValue(int)
	 */
	public boolean hasAttributeValue(final int position) {

		if (position > maxPostionValue) {
			return false;
		}
		if (groupedAttributeValuesPosition == position ) {
			return true;
		}
		return !"".equals(values.get(position));
	}


}
