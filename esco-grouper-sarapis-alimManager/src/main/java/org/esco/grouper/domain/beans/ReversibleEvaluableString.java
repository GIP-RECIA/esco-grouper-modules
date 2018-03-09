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

import org.esco.grouper.exceptions.UnknownTemplateElementTempateElement;

/**
 * @author GIP RECIA - A. Deman
 * 12 août 08
 *
 */
public class ReversibleEvaluableString extends EvaluableString {

    /** Serial version UID.*/
    private static final long serialVersionUID = 2519079352524420095L;

    /** The String before the evaluation. */
    private String templateString;


    /**
     * Builds an instance of ReversibleEvaluableString.
     */
    protected ReversibleEvaluableString() {
        super();
    }

    /**
     * Builds an instance of ReversibleEvaluableString.
     * @param string The source string.
     * @throws UnknownTemplateElementTempateElement
     */
    public ReversibleEvaluableString(final String string)
    throws UnknownTemplateElementTempateElement {
        super(string);
        templateString = string;
    }

    /**
     * Evaluates the string by replacing the template elements by a value.
     * @param values The substitution values used to perform the evaluation.
     * @return The evaluated instance.
     */
    @Override
    public ReversibleEvaluableString evaluate(final String...values) {
        if (isEvaluated()) {
            return this;
        }
        final String newString = TemplateElement.evaluate(getTemplateMask(),
                getString(), values);
        final ReversibleEvaluableString evaluated = new ReversibleEvaluableString();
        evaluated.setString(newString);
        evaluated.setTemplateString(getTemplateString());
        return evaluated;
    }


    /**
     * Getter for templateString.
     * @return templateString.
     */
    public String getTemplateString() {
        return templateString;
    }

    /**
     * Setter for templateString.
     * @param templateString the new value for templateString.
     */
    public void setTemplateString(final String templateString) {
        this.templateString = templateString;
    }

}
