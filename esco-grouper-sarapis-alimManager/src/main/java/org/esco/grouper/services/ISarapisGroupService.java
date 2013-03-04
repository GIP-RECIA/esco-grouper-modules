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


import org.esco.grouper.domain.beans.GrouperOperationResultDTO;

/**
 * Interface for the use of groups in an establishment.
 * @author GIP RECIA - A. Deman
 * 3 June 08
 *
 */
public interface ISarapisGroupService {




    /**
     * Adds a person to groups.
     * @param personDescription The description of the person.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     */
    GrouperOperationResultDTO addToGroups(final IEntityDescription personDescription);

    /**
     * Updates the memberships of a person.
     * @param personDescription The description of the person.
     * @return The result object which contains the informations about how the operation
     * has been performed.
     */
    GrouperOperationResultDTO updateMemberships(final IEntityDescription personDescription);

    /**
     * Removes a person from all the groups, including the groups which are not managed by this service.
     * @param userId The id of the person to remove from the groups.
     * @return The result of the Grouper operation.
     */
    GrouperOperationResultDTO removeFromAllGroups(final String userId);

    /**
     * Removes a person from the groups managed by this service.
     * @param userId The id of the person to remove from the groups.
     * @return The result of the Grouper operation.
     */
    GrouperOperationResultDTO removeFromManagedGroups(final String userId);

}
