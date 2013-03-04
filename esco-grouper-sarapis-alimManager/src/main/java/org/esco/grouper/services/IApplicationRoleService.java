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
