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
package org.esco.grouper.domain.beans;

import java.io.Serializable;


/**
 * Result of a grouper operation.
 * @author GIP RECIA - A. Deman
 * 27 may 08
 *
 */
public class GrouperOperationResultDTO implements Serializable {

    /** Result of a valid grouper operation. */
    public static final GrouperOperationResultDTO RESULT_OK = new GrouperOperationResultDTO();

    /** Serial version UID.*/
    private static final long serialVersionUID = 3643694431079789466L;

    /** Flag of error.*/
    private boolean error;

    /** Exception if there is an error. */
    private Exception exception;

    /**
     * Constructor for GrouperOperationResultDTO.
     */
    public GrouperOperationResultDTO() {
        super();
    }

    /**
     * Constructor for GrouperOperationResultDTO.
     * @param exception The exception thrown during the Grouper operation.
     */
    public GrouperOperationResultDTO(final Exception exception) {
        this.error = true;
        this.exception = exception;
    }

    /**
     * Getter for error.
     * @return the error
     */
    public boolean isError() {
        return error;
    }

    /**
     * Setter for error.
     * @param error the error to set
     */
    public void setError(final boolean error) {
        this.error = error;
    }

    /**
     * Getter for exception.
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Setter for exception.
     * @param exception the exception to set
     */
    public void setException(final Exception exception) {
        this.exception = exception;
    }

    /**
     * Gives the string representation of the grouper result.
     * @return The string representation of the grouper operation.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (!isError()) {
            return getClass().getSimpleName() + "#{No Error}";
        }
        return getClass().getSimpleName() + "#{Error:" + exception.getMessage() +  "}";
    }
}
