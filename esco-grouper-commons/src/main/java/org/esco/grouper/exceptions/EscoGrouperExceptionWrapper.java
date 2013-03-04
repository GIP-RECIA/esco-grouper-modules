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
package org.esco.grouper.exceptions;

/**
 * Exception used by the web sevice used to wrap a Grouper exception.
 * @author GIP RECIA - A. Deman
 * 26 nov. 07
 */
public class EscoGrouperExceptionWrapper extends EscoGrouperException {
	
	/**Serial Version UID.*/
	private static final long serialVersionUID = -7288015012953680653L;

	/**
	 * Constructor for WS4GrouperException.
	 * @param wrappedException The Grouper exception to wrap.
	 */
	public EscoGrouperExceptionWrapper(final Exception wrappedException) {
		super(wrappedException);
	}

	/**
	 * Constructor for WS4GrouperException.
	 * @param message The message to use add to the source exception.
	 * @param wrappedException The Grouper exception to wrap.
	 */
	public EscoGrouperExceptionWrapper(final String message, final Exception wrappedException) {
		super(message, wrappedException);
	}
}
