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
import java.util.regex.Pattern;

import org.esco.grouper.exceptions.UnknownTemplateElementTempateElement;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 16 janv. 2013
 */
public class EvaluableStringCondition implements Serializable {

	/** Serial ID. */
	private static final long serialVersionUID = -1630978490901548037L;
	/** */
	private EvaluableString evaluableString;
	/** */
	private Pattern condition;

	/** Constructor
	 * @param string
	 * @param condition
	 * @throws UnknownTemplateElementTempateElement */
	public EvaluableStringCondition (final String string, final Pattern condition) throws UnknownTemplateElementTempateElement {
		this.evaluableString = new EvaluableString(string);
		this.condition = condition;
	}
	/** Constructor
	 * @param string
	 * @param condition */
	public EvaluableStringCondition (final EvaluableString string, final Pattern condition) {
		this.evaluableString = string;
		this.condition = condition;
	}

	/**
	 * Getter of member string.
	 * @return <code>EvaluableString</code> the attribute string
	 */
	public EvaluableString getEvaluableString() {
		return evaluableString;
	}

	/**
	 * Getter of member condition.
	 * @return <code>Pattern</code> the attribute condition
	 */
	public Pattern getCondition() {
		return condition;
	}

	/**
	 * @return True if a condition is define.
	 */
	public boolean hasConditionDefinition() {
		return condition == null;
	}

	/**
	 * @param value
	 * @return True if the value match the pattern.
	 */
	public boolean isMatchingCondition(final String value) {
		return condition == null || this.condition.matcher(value).matches();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((condition == null) ? 0 : condition.pattern().hashCode());
		result = prime * result
				+ ((evaluableString == null) ? 0 : evaluableString.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (!(obj instanceof EvaluableStringCondition)) { return false; }
		final EvaluableStringCondition other = (EvaluableStringCondition) obj;
		if (condition == null) {
			if (other.condition != null)return false;
		} else if (!condition.pattern().equals(other.condition.pattern()))
			return false;
		if (evaluableString == null) {
			if (other.evaluableString != null) return false;
		} else if (!evaluableString.equals(other.evaluableString)) return false;
		return true;
	}

}
