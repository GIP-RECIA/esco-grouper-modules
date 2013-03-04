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

import java.io.Serializable;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.groups.IIndividualGroupService;

/**
 * Used to retrieve the individual group service associated to the grouper groups.
 * @author GIP RECIA - A. Deman
 * 17 mars 08
 *
 */
public class ESCOGrouperGroupServiceFinder implements Serializable {

	/** Serial version UID.*/
	private static final long serialVersionUID = 4504008861245970330L;

	/** Logger. */
	//private static final Log LOGGER = LogFactory.getLog(ESCOReferenceEntityGroupStoreFactory.class);
	private static final Log LOGGER = LogFactory.getLog(ESCOGrouperGroupServiceFinder.class);

	/** Singleton INSTANCE. */
	private static final ESCOGrouperGroupServiceFinder INSTANCE = new ESCOGrouperGroupServiceFinder();

	/** The service. */
	private IIndividualGroupService grouperService;

	/**
	 * Constructor for ESCOGrouperGroupServiceFinder.
	 */
	protected ESCOGrouperGroupServiceFinder() {
		/* protected*/
	}

	/**
	 * Initializes the grouper group service finder.
	 * @param service The name of the grouper service.
	 */
	public void intialize(final IIndividualGroupService service) {
		this.grouperService = service;
		if (LOGGER.isDebugEnabled()) {
			final StringBuffer sb = new StringBuffer("Initialization of the ");
			sb.append(getClass().getSimpleName());
			sb.append(" with service name : ");
			sb.append(service.getServiceName());
			LOGGER.info(sb);
		}
	}

	/**
	 * Gives the singleton instance.
	 * @return The singleton instance.
	 */
	public static ESCOGrouperGroupServiceFinder instance() {
		return INSTANCE;
	}

	/**
	 * Getter for grouperService.
	 * @return the grouperService
	 */
	public IIndividualGroupService getGrouperService() {
		return grouperService;
	}

}
