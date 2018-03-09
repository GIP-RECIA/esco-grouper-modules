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
