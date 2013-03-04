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

package org.esco.grouper.domain.beans;

import java.io.Serializable;

import org.esco.grouper.exceptions.UnknownTemplateElementTempateElement;

/**
 * String tha may contain (or not) a template element.
 * @author GIP RECIA - A. Deman
 * 1 août 08
 *
 */
public class EvaluableString implements Serializable {

    /** Serial version UID.*/
    private static final long serialVersionUID = -3881318995251360541L;

    /** The evaluable string. */
    private String string;

    /** The template mask. */
    private int templateMask;

    /**
     * Builds an instance of EvaluableString.
     */
    protected EvaluableString() {
        super();
    }

    /**
     * Builds an instance of EvaluableString.
     * @param string The orioginal string.
     * @throws UnknownTemplateElementTempateElement If there is a template element in the string
     * which is unknown.
     */
    public EvaluableString(final String string) throws UnknownTemplateElementTempateElement {
        if (string == null) {
            this.string = "";
        } else {
            this.string = string;
        }
        this.templateMask = TemplateElement.computeTemplateMask(this.string);
        if (!TemplateElement.isValid(string)) {
            throw new UnknownTemplateElementTempateElement(string);
        }
    }

    /**
     * Evaluates the string by replacing the template elements by a value.
     * @param values The substitution values used to perform the evaluation.
     * @return The evaluated instance.
     * which is unknown.
     */
    public EvaluableString evaluate(final String...values) {

        if (isEvaluated()) {
            return this;
        }

        final String newString = TemplateElement.evaluate(templateMask,
                string, values);
        final EvaluableString evaluated = new EvaluableString();
        evaluated.string = newString;
        return evaluated;
    }

    /**
     * Gives the string without checking the evaluation.
     * @return The string.
     */
    public String getString() {
        return string;
    }

    /**
     * Gives the string representation of the instance.
     * @return The string that represents this instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return string;
    }

    /**
     * Tests if an object is equal to this instance.
     * @param obj The object to test.
     * @return True if the object is equal to this instance.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof EvaluableString)) {
            return false;
        }

        return string.equals(((EvaluableString) obj).string);
    }

    /**
     * Gives the hash value of this instance.
     * @return The hash value.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return string.hashCode();
    }

    /**
     * Tests if the evaluable string is already evaluated.
     * @return True if the instance is already evaluated.
     */
    public boolean isEvaluated() {
        return templateMask == 0;
    }

    /**
     * Tests if the evaluable string is empty.
     * @return True if the evaluable string is empty.
     */
    public boolean isEmpty() {
        return "".equals(string);
    }

    /**
     * Getter for templateMask.
     * @return templateMask.
     */
    protected int getTemplateMask() {
        return templateMask;
    }

    /**
     * Setter for string.
     * @param string the new value for string.
     */
    protected void setString(final String string) {
        this.string = string;
    }



}
