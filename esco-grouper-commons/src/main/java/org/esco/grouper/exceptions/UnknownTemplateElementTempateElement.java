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

import org.esco.grouper.domain.beans.TemplateElement;

/**
 *
 * @author GIP RECIA - A. Deman
 * 7 août 08
 *
 */
public class UnknownTemplateElementTempateElement extends Exception {

    /** Serial version UID.*/
    private static final long serialVersionUID = 8273546847763385330L;

    /**
     * Builds an instance of UnknownTemplateElementTempateElement.
     * @param invalidTemplateKey The template key that is the cause of the error.
     */
    public UnknownTemplateElementTempateElement(final String invalidTemplateKey) {
        super("Unknown Template Element in the string: " + invalidTemplateKey
                + " - Known template elements are: "
                + TemplateElement.getAvailableTemplateElements() + ".");
    }

}
