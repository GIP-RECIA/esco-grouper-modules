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
import org.jasig.portal.groups.IEntitySearcher;
import org.jasig.portal.groups.IEntitySearcherFactory;

/**
 * The Factory for EntitySearchers.
 * @author GIP RECIA - A. Deman
 * 22 nov. 07
 */
public class EntitySearcherFactory implements IEntitySearcherFactory {

	/** The logger to use.*/
	private static final Log LOGGER = LogFactory.getLog(EntitySearcherFactory.class);
	
	/**
	 * Constructor for EntitySearcherFactory.
	 */
	public EntitySearcherFactory() {
		/* */
	}
	
	/**
	 * Creates an instance of EntitySearcher.
	 * @return The instance.
	 * @see org.jasig.portal.groups.IEntitySearcherFactory#newEntitySearcher()
	 */
	
	public IEntitySearcher newEntitySearcher() {
		LOGGER.info("!!!         EntitySearcherFactory.newEntitySearcher()   !!!");
		return new EntitySearcher();
	}

}
