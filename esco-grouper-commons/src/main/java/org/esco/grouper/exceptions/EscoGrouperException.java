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
