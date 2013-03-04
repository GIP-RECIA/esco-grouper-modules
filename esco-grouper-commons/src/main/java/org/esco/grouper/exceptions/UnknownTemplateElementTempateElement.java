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
