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

import org.jasig.portal.EntityTypes;
import org.jasig.portal.groups.EntityImpl;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.groups.IEntityStore;

/**
 * Factory for the EntityStore.
 * @author GIP RECIA - A. Deman
 * 22 nov. 07
 *
 */
public class EntityStore implements IEntityStore {
	
	/**
	 * Constructor for EntityStore.
	 */
	public EntityStore() {
		/* */
	}

	/**
	 * @param key The key of the instance to create.
	 * @see org.jasig.portal.groups.IEntityStore#newInstance(java.lang.String)
	 */
	public IEntity newInstance(final String key) throws GroupsException {
		return new EntityImpl(key, null);
	}
	
	/**
	 * Creates an instance.
	 * @param key The key to use for the instance.
	 * @param type The underlying type of the instance.
	 * @return The instance.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityStore#newInstance(java.lang.String, java.lang.Class)
	 */
	public IEntity newInstance(final String key,
			@SuppressWarnings("unchecked") final Class type) throws GroupsException {
		
		if (EntityTypes.getEntityTypeID(type) == null ) {
			throw new GroupsException("Invalid group type: " + type); 
		}
		return new EntityImpl(key, type);
	}

}
