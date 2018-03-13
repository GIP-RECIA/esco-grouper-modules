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
/**
 *
 */
package org.esco.grouper.services;

import org.esco.grouper.domain.beans.GrouperDTO;
import org.esco.grouper.domain.beans.GrouperOperationResultDTO;

/**
 * Service for the application roles management via Grouper.
 * @author GIP RECIA - A. Deman
 * 3 juin 08
 *
 */
public interface IApplicationRoleService {

    /**
     * Creates a global role for a given application (e.g. superadmin for moodle)
     * @param applicationName The name of the target application.
     * @param globalRoleName The name of the global role.
     * @return The object that denotes the result of the grouper operation.
     */
    GrouperOperationResultDTO createGlobalRole(final String applicationName,
            final String globalRoleName);

     /**
      * creates a local role for a given association. This role is called local because
      * it is associated to an establishment.
      * @param applicationName The name of the application.
      * @param establishmentUAI The UAI of the establishment.
      * @param establishmentName The name of the establishment.
      * @param localRoleName The name of the role.
      * @return The result object of the operation.
      */
    GrouperOperationResultDTO createLocalRole(final String applicationName,
            final String establishmentUAI,
            final String establishmentName,
            final String localRoleName);

    /**
     * Creates a local role for an application, an establishment and a category.
     * @param applicationName The name of the application.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param category The category e.g. a class in the establishment.
     * @param localRoleName The name of the role.
     * @return The result object of the Grouper operation.
     */
    GrouperOperationResultDTO createLocalRole(final String applicationName,
            final String establishmentUAI,
            final String establishmentName,
            final String category,
            final String localRoleName);

    /**
     * Gives the members of the group associated to a global role for an application.
     * @param applicationName The name Of the application.
     * @param globalRoleName The name of the role.
     * @return The informations about the subjects members of the role.
     */
    GrouperDTO[] getMembersForGobalRole(final String applicationName,
            final String globalRoleName);

    /**
     * Gives global roles associated to an user for an application.
     * @param applicationName The name Of the application.
     * @param globalRoleName The name of the role.
     * @param userId The id of the user.
     * @return The informations about the roles to wihch the user is member of.
     */
    GrouperDTO[] getGobalRolesForUser(final String applicationName,
            final String globalRoleName,
            final String userId);

    /**
     * Tests if an user is member of a global role for an application.
     * @param applicationName The name of the application.
     * @param gloablRoleName The name of the role.
     * @param userId The id of the user.
     * @return True if the user is member of the role, false otherwise.
     */
    Boolean isMemeberOfGlobalRole(final String applicationName,
            final String gloablRoleName,
            final String userId);


    /**
     * Gives the members of the group associated to a local role for an application.
     * @param applicationName The name Of the application.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param localRoleName The name of the role.
     * @return The informations about the subjects members of the role.
     */
    GrouperDTO[] getMembersForLocalRole(final String applicationName,
            final String establishmentUAI,
            final String establishmentName,
            final String localRoleName);

    /**
     * Gives local roles associated to an user for an application.
     * @param applicationName The name Of the application.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName the name of the establishment.
     * @param localRoleName The name of the role.
     * @param userId The id of the user.
     * @return The informations about the local roles to which the user is member of.
     */
    GrouperDTO[] getLocalRolesForUser(final String applicationName,
            final String establishmentUAI,
            final String establishmentName,
            final String localRoleName,
            final String userId);

    /**
     * Tests if an user is member of a local role for an application.
     * @param applicationName The name of the application.
     * @param establishmentUAI The UAI of the establishment.
     * @param establishmentName The name of the establishment.
     * @param localRoleName The name of the role.
     * @param userId The id of the user.
     * @return True if the user is member of the local role.
     */
    Boolean isMemeberOfLocalRole(final String applicationName,
            final String establishmentUAI,
            final String establishmentName,
            final String localRoleName,
            final String userId);



}
