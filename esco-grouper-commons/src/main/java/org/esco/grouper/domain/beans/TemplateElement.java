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

import java.util.ArrayList;
import java.util.List;


/**
 * Element of a template definition.
 * @author GIP RECIA - A. Deman
 * 31 juil. 08
 */
public class TemplateElement {
    
    /** Separator for the template elements. */
    private static final char SEPARATOR = '%';
    
    /** Counter used to buikd the masks. */
    private static int maskCount;
    
    /** The available elements (ordered for the evaluation process).*/
    private static  List<TemplateElement> availableTemplateElements = new ArrayList<TemplateElement>();
    
    /** Key for the template. */
    private String key;
    
    /** The default value for the template element. */
    private String defaultValue;
    
    /** Mask associated to this template element. */
    private int mask = nextMask();
 
    /**
     * Builds an instance of TemplateElement.
     * @param key The key of the template.
          */
    protected TemplateElement(final String key) {
        this.key = key;
        if (this.key.charAt(0) != SEPARATOR) {
            this.key = SEPARATOR + this.key;
        }
        if (this.key.charAt(key.length() - 1) != SEPARATOR) {
            this.key += SEPARATOR;
        }
    }
    
    /**
     * Builds an instance of TemplateElement.
     * @param key The key of the template.
     * @param defaultValue The default value to use.
     */
    protected TemplateElement(final String key, final String defaultValue) {
        this(key);
        this.defaultValue = defaultValue;
    }
    
    /**
     * Gives the hash value for this template element.
     * @return The hash value.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return key.hashCode();
    }
    
    /**
     * Tests if this instance is equal to this instance.
     * @param o The object to test.
     * @return True if the object is equal to this instance.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        
        if (this == o) {
            return true;
        }
        
        if (o == null) {
            return false;
        }
        
        if (!(o instanceof TemplateElement)) {
            return false;
        }
        
        return key.equals(((TemplateElement) o).key);
    }
    
    /**
     * Gives the next available mask.
     * @return The next available mask.
     */
    private static synchronized int nextMask() {
        return (int) Math.pow(2, maskCount++);
    }
    
    /**
     * Gives the string representation of this template definition element.
     * @return The string that represents this template element.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return key;
    }
    
    /**
     * Replaces the template element in a string.
     * @param src The string that may contain the template element to repace.
     * @param replacement The replacement value.
     * @return The new string. 
     */
    public String replace(final String src, final String replacement) {
        return src.replaceAll(key, replacement);
    }
    
    /**
     * Tests if a mask is flaged for this definition.
     * @param testedMask The mask to test.
     * @return True if the mask is flagged for this definition.
     */
    public boolean hasFlag(final int testedMask) {
        return (mask & testedMask) == mask;
    }
    
    /**
     * Marks a mask for this element if a string contains this template element.
     * @param src The string to check.
     * @param currentMask The mask to use.
     * @return the same mask if the string does not contain the template element,
     * the mask with the flag associated to this element otherwise.
     */
    protected int markIfNecessary(final String src, final int currentMask) {
        if (!isContainedBy(src)) {
            return currentMask;
        }
        return currentMask | mask;
    }
    
    /**
     * Adds a template element, so this elements can be used in the static methods.
     * <b>Note :</b> The order of the registration is important as it has consequences
     * on the evaluation process.
     * @param templateElement The template element to register.
     */
    public static void registerTemplateElement(final TemplateElement templateElement) {
        if (!availableTemplateElements.contains(templateElement)) {
            availableTemplateElements.add(templateElement);
        }
    }
    
    /**
     * Adds a template element, so this elements can be used in the static methods.
     * <b>Note :</b> The order of the registration is important as it has consequences
     * on the evaluation process.
     * @param key The key of the template element to register.
     */
    public static void registerTemplateElement(final String key) {
        registerTemplateElement(new TemplateElement(key));
    }
    
    /**
     * Adds a template element, so this elements can be used in the static methods.
     * <b>Note :</b> The order of the registration is important as it has consequences
     * on the evaluation process.
     * @param key The key of the template element to register.
     * @param defaultValue The default value to use for the template element to register.
     */
    public static void registerTemplateElement(final String key, final String defaultValue) {
        registerTemplateElement(new TemplateElement(key, defaultValue));
    }
    
    /**
     * Removes all the registered template elements.
     */
    public static void removeAllRegisteredTemplateElements() {
        availableTemplateElements.clear();
    }
    
    /**
     * Givs a template element.
     * @param key The key of the template element to retrieve.
     * @return The template element if found, null otherwise.
     */
    public static TemplateElement getAvailableTemplateelementByKey(final String key) {
        final String trimedKey = key.trim();
        for (TemplateElement templateElt : availableTemplateElements) {
            if (templateElt.key.equals(trimedKey)) {
                return templateElt;
            }
        }
        return null;
    }
    
    /**
     * Computes the mask for all the template elements.
     * @param src The string that may contain the template elements.
     * @return The mask corresponding to all the template elements 
     * contained in the string.
     */
    public static int computeTemplateMask(final String src) {
        int currentMask = 0;
        for (TemplateElement templateElt : availableTemplateElements) {
            currentMask = templateElt.markIfNecessary(src, currentMask);
        }
        return currentMask;
    }
    
    /**
     * Resolves the template element in a source string.
     * @param testedMask The mask associated to the source string. 
     * @param src The source string that may contains the key of this template element.
     * @param value The value to use in replacement of the key. If the value is empty and the template has a default
     * value, then the default value is used.
     * @return The String with the  keys replaces by values.
     */
    public String evaluate(final int testedMask, final String src, final String value) {
        String checkedValue = value;
        if ("".equals(value) && hasDefaultValue()) {
            checkedValue = defaultValue;
        }
        
        if (hasFlag(testedMask)) {
            return replace(src, checkedValue);
        }
        return src;
    }
    
    /**
     * Evaluates a source string for alla the available Template elements.
     * @param testedMask The mask associated to the source string.
     * @param src The source string.
     * @param values The values used to evaluates the template elements.
     * in the same order than the registered template elements.
     * @return The evaluated string.
     */
    public static String evaluate(final int testedMask, final String src,
            final String...values) {
        String evaluated = src;
        for (int i = 0; i < Math.min(availableTemplateElements.size(), values.length); i++) {
            evaluated = availableTemplateElements.get(i).evaluate(testedMask, evaluated, values[i]);
        }
        return evaluated;
    }
    
    /**
     * Tests if a string contains this definition.
     * @param testedString The string to test.
     * @return True if the String contains the definition.
     */
    public boolean isContainedBy(final String testedString) {
        if (testedString.indexOf(SEPARATOR) < 0) {
            return false;
        }
        return testedString.contains(key);
    }
    
    /**
     * Tests if a string is a template element.
     * @param str The string to test.
     * @return True if the string is equal to the key of one of
     * the registered template elements.
     */
    public static boolean isTemplateElement(final String str) {
        if (str == null || "".equals(str)) {
            return false;
        }
        final String trimedStr = str.trim();
        for (TemplateElement templateElt : availableTemplateElements) {
            if (templateElt.key.equals(trimedStr)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks the validity of a String that may contain a template element.
     * @param string The tested string.
     * @return True if the tested string contains a template element.
     */
    public static boolean isValid(final String string) {
        int index1;
        for (index1 = 0; index1 < string.length(); index1++) {
            if (string.charAt(index1) == SEPARATOR) {
                boolean found = false;
                int index2;
                for (index2 = index1 + 1; index2 < string.length() && !found; index2++) {
                    if (string.charAt(index2) == SEPARATOR) {
                        found = true;
                        String subStr = string.substring(index1, index2 + 1);
                        boolean valid = false;
                        for (int k = 0; k < availableTemplateElements.size() && !valid; k++) {
                            valid = availableTemplateElements.get(k).isContainedBy(subStr);
                            
                        }
                        if (!valid) {
                            return false;
                        } 
                    }
                }
                if (!found) {
                    return false;
                }
                    index1 = index2 - 1;
            }
        }
        return true;
    }

    /**
     * Getter for availableTemplateElements.
     * @return availableTemplateElements.
     */
    public static List<TemplateElement> getAvailableTemplateElements() {
        return availableTemplateElements;
    }
    
    /**
     * Gives the number of available template elements.
     * @return The number of available template elements.
     */
    public static int countAvailableTemplateElements() {
        return availableTemplateElements.size();
    }
    
    /**
     * Gives a specified template element.
     * @param index The index of the template element.
     * @return The template element.
     */
    public static TemplateElement getAvailableTemplateElement(final int index) {
        return availableTemplateElements.get(index);
    }

    /**
     * Tests if the template element has a default value.
     * @return True if the template element has a default value.
     */
    public boolean hasDefaultValue() {
        return defaultValue != null;
    }

    /**
     * Setter for defaultValue.
     * @param defaultValue the new value for defaultValue.
     */
    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
