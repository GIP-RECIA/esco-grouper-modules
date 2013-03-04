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
 * Groups
 */
package org.esco.portal.groups.grouper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.groups.ComponentGroupServiceDescriptor;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IEntityGroupStore;
import org.jasig.portal.groups.IEntityGroupStoreFactory;

/**
 * Factory for the EntityGroupStore.
 * @author GIP RECIA - A. Deman 
 * 22 nov. 07
 *
 */
public class EntityGroupStoreFactory implements IEntityGroupStoreFactory {

	/** Logger. */
	private static final Log LOGGER = LogFactory.getLog(EntityGroupStoreFactory.class);

	/**
	 * Constructor for EntityGroupStoreFactory.
	 */
	public EntityGroupStoreFactory() {
		LOGGER.debug("Creation of an instance of EntityGroupStoreFactory.");
	}
	
	/**
	 * Creates an instance of EntityGroupStore.
	 * @return The instance.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroupStoreFactory
	 * #newGroupStore()
	 */
	
	public IEntityGroupStore newGroupStore() throws GroupsException {
		return newGroupStore(null);
	}

	/**
	 * Construction with parameters.
	 * @param svcDescriptor The parameters.
	 * @return The instance.
	 * @throws GroupsException
	 * @see org.jasig.portal.groups.IEntityGroupStoreFactory
	 * #newGroupStore(org.jasig.portal.groups.ComponentGroupServiceDescriptor)
	 */
	public IEntityGroupStore newGroupStore(final ComponentGroupServiceDescriptor svcDescriptor)
			throws GroupsException {
		return new EntityGroupStore();
	}
}
