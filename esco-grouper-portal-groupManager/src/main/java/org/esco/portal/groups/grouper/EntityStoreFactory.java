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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.groups.IEntityStore;
import org.jasig.portal.groups.IEntityStoreFactory;

/**
 * Factory for the EntityStore.
 * @author GIP RECIA - A. Deman
 * 22 nov. 07
 *
 */
public class EntityStoreFactory implements IEntityStoreFactory {

	/** Logger. */
	private static final Log LOGGER = LogFactory.getLog(EntityStoreFactory.class);

	/**
	 * Constructor for EntityStoreFactory.
	 */
	public EntityStoreFactory() {
		/* */
	}

	/**
	 * Creates an instance.
	 * @return The instance.
	 * @see org.jasig.portal.groups.IEntityStoreFactory#newEntityStore()
	 */
	public IEntityStore newEntityStore() {
		LOGGER.info("!!!         EntityStoreFactory.newEntityStore()   !!!");
		return new EntityStore();
	}

}
