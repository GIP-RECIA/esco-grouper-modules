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
package org.esco.grouper.parsing;

import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.esco.grouper.domain.beans.PersonType;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;


/**
 * Atribute handler for the Sarapis Group Service (SGS).
 * @author GIP RECIA - A. Deman
 * 6 août 08
 *
 */
public class SGSAttributeHandler implements Serializable {

    /** UID attribute. */
    public static final String UID_ATTR = "uid";

    /** Extension attribute. */
    public static final String EXT_ATTR = "extension";

    /** Display extension attribute. */
    public static final String DISP_EXT_ATTR = "display-extension";

    /** Description attribute. */
    public static final String DESC_ATTR = "description";

    /** Preexisting attribute. */
    public static final String PREEXIST_ATTR = "preexisting";

    /** Create attribute. */
    public static final String CREATE_ATTR = "create";

    /** Path attribute. */
    public static final String PATH_ATTR = "path";

    /** Right attribute. */
    public static final String RIGHT_ATTR = "right";

    /** Recursive attribute. */
    public static final String RECURS_ATTR = "recursive";

    /** Type attribute. */
    public static final String TYPE_ATTR = "type";

    /** Distribution by attribute. */
    public static final String DISTRIB_BY_ATTR = "distribution-by";

    /** Key attribute. */
    public static final String KEY_ATTR = "key";

    /** Default value attribute. */
    public static final String DEF_VAL_ATTR = "default-value";

    /** value attribute. */
    public static final String VALUE_ATTR = "value";

    /** Condition Attribute. */
    public static final String CONDITION_GROUP_EXT_ATTR = "condition-group-ext";

    /** Serial version UID.*/
    private static final long serialVersionUID = 8391732649319550004L;

    /** Logger.*/
    private static final Logger LOGGER = Logger.getLogger(SGSAttributeHandler.class);

    /** UID value. */
    private String uid;

    /** Extension value. */
    private String extension;

    /** Display extension value. */
    private String displayExtension;

    /** Description value. */
    private String description;

    /** Preexisting value. */
    private Boolean preexisting;

    /** Create value. */
    private Boolean create;

    /** Path value. */
    private String path;

    /** Right value. */
    private String right;

    /** Recursive value. */
    private Boolean recursive;

    /** Type value. */
    private PersonType type;

    /** Distribution by value. */
    private String distributionBy;

    /** Key for a template element. */
    private String key;

    /** Key for a defaultValue element. */
    private String defaultValue;

    /** value attribute. */
    private Boolean value;

    /** Condition value on group extension. */
    private Pattern conditionGroupExt;

    /**
     * Builds an instance of SGSAttributeHandler.
     */
    public SGSAttributeHandler() {
        LOGGER.debug("Creation of an instance of " + getClass().getSimpleName() + ".");
    }

    /**
     * handles attributes.
     * @param locator The locator.
     * @param attributs The attributs to handle.
     * @throws SAXParseException If one of the  parsed value is not valid.
     */
    public void handleAttributes(final Locator locator,
            final Attributes attributs) throws SAXParseException {

        for (int index = 0; index < attributs.getLength(); index++) {
            handleAttribute(locator,
                    attributs.getLocalName(index),
                    attributs.getValue(index));
        }
    }

    /**
     * Handles an attribute.
     * @param locator The current locator in the xml file.
     * @param attributeName The name of the attribute to handle.
     * @param attributeValue The value of the attribute to handle.
     * @throws SAXParseException If the value of the attribute has to be parse and is
     * not valid.
     */
    public void handleAttribute(final Locator locator,
            final String attributeName,
            final String attributeValue) throws SAXParseException {

        final String trimedAttrName = attributeName.trim();


        if (UID_ATTR.equals(trimedAttrName)) {
            uid = attributeValue.trim();
        } else if (EXT_ATTR.equals(trimedAttrName)) {
            extension = attributeValue.trim();
        } else if (DISP_EXT_ATTR.equals(trimedAttrName)) {
            displayExtension = attributeValue.trim();
        } else if (DESC_ATTR.equals(trimedAttrName)) {
            description = attributeValue.trim();
        } else if (VALUE_ATTR.equals(trimedAttrName)) {
           value = parseBoolean(locator, trimedAttrName, attributeValue);
        } else if (PREEXIST_ATTR.equals(trimedAttrName)) {
            preexisting = parseBoolean(locator, trimedAttrName, attributeValue);
            // Checks that the create and preexisting attribut are not true together.
            if (preexisting && create) {
                throw new SAXParseException("The attribute " + PREEXIST_ATTR + " and " + CREATE_ATTR
                        + " can't be true at the same time.", locator);
            }

        } else if (CREATE_ATTR.equals(trimedAttrName)) {
            create = parseBoolean(locator, trimedAttrName, attributeValue);
            // Checks that the create and preexisting attribut are not true together.
            if (preexisting && create) {
                throw new SAXParseException("The attribute " + PREEXIST_ATTR + " and " + CREATE_ATTR
                        + " can't be true at the same time.", locator);
            }
        } else if (PATH_ATTR.equals(trimedAttrName)) {
            path = attributeValue.trim();
        } else if (RIGHT_ATTR.equals(trimedAttrName)) {
            right = attributeValue.trim();
        } else if (RECURS_ATTR.equals(trimedAttrName)) {
            recursive = parseBoolean(locator, trimedAttrName, attributeValue);
        } else if (TYPE_ATTR.equals(trimedAttrName)) {
            type = PersonType.parseIgnoreCase(attributeValue);
            if (type == null) {
                throw new SAXParseException("Illegal value for atribute "
                        + attributeName + " legal values are : "
                        + Arrays.toString(PersonType.values())
                        + " found : " + attributeValue + ".", locator);
            }
        } else if (DISTRIB_BY_ATTR.equals(trimedAttrName)) {
            distributionBy = attributeValue.trim();
        } else if (KEY_ATTR.equals(trimedAttrName)) {
            key = attributeValue.trim();
        } else if (DEF_VAL_ATTR.equals(trimedAttrName)) {
            defaultValue = attributeValue.trim();
        } else if (CONDITION_GROUP_EXT_ATTR.equals(trimedAttrName)) {
        	if ("".equals(attributeValue.trim())) {
        		throw new SAXParseException("The attribute " + CONDITION_GROUP_EXT_ATTR + " is facultative but can't be empty.", locator);
        	}
            conditionGroupExt = Pattern.compile(attributeValue.trim());
        }
    }

    /**
     * Resets the handler.
     */
    public void reset() {
        extension = null;
        displayExtension = null;
        description = null;
        preexisting = false;
        create = false;
        path = null;
        recursive = false;
        type = null;
        right = null;
        distributionBy = null;
        conditionGroupExt = null;
    }

    /**
     * Parse a boolean attribute.
     * @param locator The locator for the parsed file..
     * @param attribute The attribute to parse.
     * @param valueStr The value to parse.
     * @return True of false.
     * @throws SAXParseException If the value is not true or false (case insensitive).
     */
    protected boolean parseBoolean(final Locator locator,
            final String attribute,
            final String valueStr) throws SAXParseException {
        final String trimedValue = valueStr.trim();
        if (trimedValue.equalsIgnoreCase("true")) {
            return true;
        } else if (trimedValue.equalsIgnoreCase("false")) {
            return false;
        }
        throw new SAXParseException("Illegal value for boolean atribute "
                + attribute + " (should be true or false): " + valueStr + ".", locator);

    }
    /**
     * Getter for extension.
     * @return extension.
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Getter for displayExtension.
     * @return displayExtension.
     */
    public String getDisplayExtension() {
        return displayExtension;
    }

    /**
     * Getter for description.
     * @return description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter for preexisting.
     * @return preexisting.
     */
    public Boolean getPreexisting() {
        return preexisting;
    }

    /**
     * Getter for path.
     * @return path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Getter for recursive.
     * @return recursive.
     */
    public Boolean getRecursive() {
        return recursive;
    }

    /**
     * Getter for type.
     * @return type.
     */
    public PersonType getType() {
        return type;
    }

    /**
     * Getter for distributionBy.
     * @return distributionBy.
     */
    public String getDistributionBy() {
        return distributionBy;
    }

    /**
     * Getter for key.
     * @return key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Getter for uid.
     * @return uid.
     */
    public String getUid() {
        return uid;
    }

    /**
     * Getter for value.
     * @return value.
     */
    public Boolean getValue() {
        return value;
    }

    /**
     * Getter for right.
     * @return right.
     */
    public String getRight() {
        return right;
    }

    /**
     * Getter for create.
     * @return create.
     */
    public Boolean getCreate() {
        return create;
    }

    /**
     * Getter for defaultValue.
     * @return defaultValue.
     */
    public String getDefaultValue() {
        return defaultValue;
    }

	/**
	 * Getter for conditionGroupExt.
	 * @return conditionGroupExt.
	 */
	public Pattern getConditionGroupExt() {
		return conditionGroupExt;
	}
}
