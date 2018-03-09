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
