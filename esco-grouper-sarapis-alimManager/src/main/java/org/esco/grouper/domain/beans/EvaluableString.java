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
