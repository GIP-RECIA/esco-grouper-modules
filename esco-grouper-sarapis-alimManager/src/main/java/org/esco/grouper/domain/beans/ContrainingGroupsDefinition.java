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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.esco.grouper.exceptions.UnknownTemplateElementTempateElement;

/**
 * Class used to group and facilitate the management of evaluable evalStringConditions.
 * @author GIP RECIA - A. Deman
 * 1 août 08
 *
 */
public class ContrainingGroupsDefinition implements Serializable {



    /** Serial version UID.*/
    private static final long serialVersionUID = 949887214078985250L;

    /** The evaluable evalStringConditions. */
    private List<EvaluableStringCondition> evalStringConditions;

    /** Flag to determine if the instance is evaluated or not.
     * This flags is not updated when one EvaluableString is evaluated directly.
     * */
    private boolean evaluated;

    /**
     * Builds an instance of ContrainingGroupsDefinition.
     */
    public ContrainingGroupsDefinition() {
        this.evalStringConditions = new ArrayList<EvaluableStringCondition>();
    }

    /**
     * Builds an instance of ContrainingGroupsDefinition.
     * @param evalStringConditions Strings and conditions to use.
     * @param evaluated The evaluated flag.
     */
    private ContrainingGroupsDefinition(final List<EvaluableStringCondition> evalStringConditions, final boolean evaluated) {
        this.evalStringConditions = evalStringConditions;
        this.evaluated = evaluated;
    }

    /**
     * Evaluates the evaluable evalStringConditions contained in this instance.
     * @param values The values to substitue to the template elements.
     * @return The evaluated instance.
     */
    public ContrainingGroupsDefinition evaluate(final String...values) {
    	List<EvaluableStringCondition> newEvalStrings = new ArrayList<EvaluableStringCondition>();

        for (int i = 0; i < countEvaluableStringConditions(); i++) {
        	EvaluableStringCondition oldEsc = getEvaluableStringCondition(i);
            newEvalStrings.add(new EvaluableStringCondition(oldEsc.getEvaluableString().evaluate(values), oldEsc.getCondition()));
        }
        return new ContrainingGroupsDefinition(newEvalStrings, true);
    }

    /**
	 * Getter of member evalStringConditions.
	 * @return <code>List<EvaluableStringCondition></code> the attribute evalStringConditions
	 */
	public List<EvaluableStringCondition> getEvaluableStringConditions() {
		return evalStringConditions;
	}

	/**
     * Give the string representation of this instance.
     * @return The string that represents this instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < countEvaluableStringConditions(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(getEvaluableStringCondition(i));
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Gives the number of evaluable evalStringConditions.
     * @return The number of evaluable evalStringConditions.
     */
    public int countEvaluableStringConditions() {
        if (evalStringConditions == null) {
            return 0;
        }
        return evalStringConditions.size();
    }

    /**
     * Gives a specified evaluable string.
     * @param index The index where to get the evaluable string.
     * @return The EvaluableString instance.
     */
    public EvaluableStringCondition getEvaluableStringCondition(final int index) {
        return evalStringConditions.get(index);
    }

    /**
     * Adds an evaluable string.
     * @param string The string to add.
     * @param condition The condition to add.
     */
    public void addEvaluableStringCondition(final EvaluableString string, final Pattern condition) {
        evalStringConditions.add(new EvaluableStringCondition(string, condition));
    }

    /**
     * Adds an evaluable string.
     * @param string The string to add.
     * @param condition The condition associated to add.
     * @throws UnknownTemplateElementTempateElement If there is a template element in the string
     * which is unknown.
     */
    public void addEvaluableStringCondition(final String string, final Pattern condition) throws UnknownTemplateElementTempateElement {
        evalStringConditions.add(new EvaluableStringCondition(string, condition));
    }

    /**
     * Getter for evaluated.
     * @return evaluated.
     */
    public boolean isEvaluated() {
        return evaluated;
    }

}
