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
import org.esco.grouper.services.GrouperWSClient;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.groups.IEntitySearcher;
import org.jasig.portal.security.IPerson;

/**
 * Searcher for entities in the grouper backend.
 * @author GIP RECIA - A. Deman
 * 22 nov. 07
 *
 */
public class EntitySearcher implements IEntitySearcher {

	/** Logger. */
	private static final Log LOGGER = LogFactory.getLog(EntitySearcher.class);

	/** Web service used to access Grouper informations. */
	private static final GrouperWSClient GROUPER_WS = GrouperWSClient.instance();
	/**
	 * Package protected constructor.
	 * Constructor for EntityGroupStore.
	 */
	EntitySearcher() { /* Package protected.*/ }

	/**
	 * Search subjects whose name matches the query string according to the specified method.
	 * @param query The part of the name of the subject.
	 * @param method The method used to perform the comparison.
	 * @param type The type of groups.
	 * @return The array of identifiers whose name match query according
	 * to the method of comparison.
	 * @see org.jasig.portal.groups.IEntitySearcher#searchForEntities(java.lang.String, int, java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public EntityIdentifier[] searchForEntities(final String query, final int method, final Class type) {


		String[] keys = GROUPER_WS.searchForSubjects(query, method);
		EntityIdentifier[] ids = new EntityIdentifier[keys.length];
		int index = 0;
		for (String key : keys) {
			ids[index++] = new EntityIdentifier(key, IPerson.class);
		}


		// Debug.
		if (LOGGER.isDebugEnabled()) {
			final StringBuffer sb = new StringBuffer("searchForEntities query: ");
			sb.append(query);
			sb.append(" method: ");
			sb.append(method);
			sb.append("\nResult:\n");
			for (String key : keys) {
				sb.append("\t");
				sb.append(key);
			}

			LOGGER.debug(sb.toString());
		}
		return ids;
	}

}
