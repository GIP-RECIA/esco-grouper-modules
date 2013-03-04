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
import org.jasig.portal.groups.ComponentGroupServiceDescriptor;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IComponentGroupService;
import org.jasig.portal.groups.IComponentGroupServiceFactory;
import org.jasig.portal.groups.IIndividualGroupService;

/**
 * Factory for the Grop service ESCOReferenceIndividualGroupService.
 * @author GIP RECIA - A. Deman 
 * 20 mars 08 org.esco.groups.ESCOReferenceIndividualGroupServiceFactory
 *
 */
public class ESCOReferenceIndividualGroupServiceFactory  implements IComponentGroupServiceFactory {

    /** Logger. */
    private static final Log LOGGER = LogFactory.getLog(ESCOReferenceIndividualGroupServiceFactory.class);

    /**
     * Constructor for ESCOReferenceIndividualGroupServiceFactory.
     */
    public ESCOReferenceIndividualGroupServiceFactory() {
        super();
    }

    /**
     * Return an instance of the service implementation.
     * @param svcDescriptor
     * @return IIndividualGroupService
     * @exception GroupsException
     */
    public IComponentGroupService newGroupService(final ComponentGroupServiceDescriptor svcDescriptor) 
    throws GroupsException {
        try {
            IComponentGroupService service =  new ESCOReferenceIndividualGroupService(svcDescriptor);
            ESCOGrouperGroupServiceFinder.instance().intialize((IIndividualGroupService) service);
            return service;
        } catch (GroupsException ge) {
            LOGGER.error(ge.getMessage(), ge);
            throw new GroupsException(ge);
        }

    }

    /**
     * Creates an instance of service.
     * @return The group service.
     * @throws GroupsException
     * @see org.jasig.portal.groups.IComponentGroupServiceFactory#newGroupService()
     */
    public IComponentGroupService newGroupService() throws GroupsException {
        return newGroupService(new ComponentGroupServiceDescriptor());
    }
}
