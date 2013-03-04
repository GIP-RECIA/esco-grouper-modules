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

/**
 * Constants for the grouper group store implementation.
 * @author GIP RECIA - A. Deman
 * 1 avr. 08
 *
 */
public abstract class ESCOConstants {
    

    /** Separator for the groups/stems to load. */
    public static final String SEP = ";";
    
	/** Initial size for the sets. */
	public static final int INITIAL_CAPACITY = 100;

	/** Default cache duration for the grouper requests in milliseconds. */
	public static final long DEFAULT_CACHE_DURATION = 30000;
	
	/** Thousand. */
	public static final long THOUSAND = 1000;
	
	/**
	 * Constructor for ESCOConstants.
	 */
	private ESCOConstants() {
	    /* */
	}
}
