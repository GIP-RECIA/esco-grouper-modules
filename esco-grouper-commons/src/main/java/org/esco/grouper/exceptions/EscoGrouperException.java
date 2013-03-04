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
 * Base exception thrown by the web service.
 * @author GIP RECIA - A. Deman
 * 26 nov. 07
 *
 */
public class EscoGrouperException extends RuntimeException {

	/** Serial Version UID.*/
	private static final long serialVersionUID = 4894843667914823550L;

	/**
	 * Constructor for WS4GrouperException.
	 */
	public EscoGrouperException() {
		super();
	}

	/**
	 * Constructor for WS4GrouperException.
	 * @param message The message displayed by the exception.
	 */
	public EscoGrouperException(final String message) {
		super(message);
	}
	
	/**
	 * Constructor for WS4GrouperException.
	 * @param wrappedException An exception to wrap.
	 */
	public EscoGrouperException(final Exception wrappedException) {
		super(wrappedException.getCause());
	}
	
	/**
	 * Constructor for WS4GrouperException.
	 * @param message The message to use add to the source exception.
	 * @param wrappedException The Grouper exception to wrap.
	 */
	public EscoGrouperException(final String message, final Exception wrappedException) {
		super(message, wrappedException.getCause());
	}

}
