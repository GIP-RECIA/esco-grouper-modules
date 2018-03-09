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
package org.esco.grouper.parsing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Util class used to load properties.
 * @author GIP RECIA - A. Deman
 * 29 avr. 08
 *
 */
public class PropertyParser implements Serializable {

    /**
     * Conparator for the number of references in a property value.
     * (e.g. file=${root}/{sub.dir}/aFilename  is after zfile=${root}/anotherFileName)
     */
    private static class ReferencesCountComparator implements Comparator<String> {

        /** The properties instance used to sor the keys. */
        private Properties properties;

        /**
         * Builds an instance of ReferencesCountComparator.
         * @param properties The properties instance to use for sorting the keys.
         */
        public ReferencesCountComparator(final Properties properties) {
            this.properties = properties;
        }

        /**
         * Gives the order for two keys.
         * @param s1 The first key.
         * @param s2 The second keys.
         * @return < 0 if there is less references in the value corresponding to the key1, 0 if the number of
         * references is equal, > 0 if the number of references in the value 2 is greater.
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(final String s1, final String s2) {
            final String value1 = properties.getProperty(s1);
            final String value2 = properties.getProperty(s2);
            int count1 = 0;
            int count2 = 0;

            for (int i = 0; i < value1.length(); i++) {
                if (value1.charAt(i) == '$') {
                    count1++;
                }
            }

            for (int i = 0; i < value2.length(); i++) {
                if (value2.charAt(i) == '$') {
                    count2++;
                }
            }
            return count1 - count2;
        }
    }


    /** Serial version UID. */
    private static final long serialVersionUID = 7763505305410596203L;

    /** Separator for the attributes. */
    private static final String ATTRIBUTES_SEP = " ";

    /** Separator in the property keys. */
    private static final String PROP_KEY_SEP = ".";

    /** Singleton. */
    private static final PropertyParser INSTANCE = new PropertyParser();

    /**
     * Conparator for the number of references in a property value.
     * (e.g. file=${root}/{sub.dir}/aFilename  is after zfile=${root}/anotherFileName)
     */


    /**
     * Constructor for PropertyParser.
     */
    protected PropertyParser() {
        /* Use the singleton. */
    }

    /**
     * Gives the singleton.
     * @return The singleton.
     */
    public static PropertyParser instance() {
        return INSTANCE;
    }

    /**
     * Resoves the references in the properties instance.
     * For instance, the properties :<br/>
     * dir=/a/directory<br/>
     * file=${dir}/filename<br/>
     * Becomes : <br/>
     * dir=/a/directory<br/>
     * file=/a/directory/filename<br/>
     * @param properties The porperties instance.
     * @return The properties with references evaluated.
     */
    public Properties resolveReferences(final Properties properties) {

        final List<String> sortedKeys = new ArrayList<String>(properties.keySet().size());
        for (Object keyObj1 : properties.keySet()) {
            sortedKeys.add((String) keyObj1);
        }
        Collections.sort(sortedKeys, new ReferencesCountComparator(properties));

        for (String key1  : sortedKeys) {
            final String regexRefKey1 = ".*\\$\\{\\s*" + key1 + "\\s*\\}.*";
            final String replaceRegexKey1 = "\\$\\{\\s*" + key1 + "\\s*\\}";
            for (String key2 : sortedKeys) {
                if (!key1.equals(key2)) {
                    final String value2 = properties.getProperty(key2);
                    if (value2.matches(regexRefKey1)) {

                        final String value1 = properties.getProperty(key1);
                        final String newValue2 = value2.replaceAll(replaceRegexKey1, value1);
                        properties.setProperty(key2, newValue2);
                    }
                }
            }
        }
        return properties;

    }

    /**
     * Retrieves the string value from a properties INSTANCE for a given key.
     * @param logger The logger to use.
     * @param propertiesSourceName The name of the source used to load the properties.
     * @param properties The properties instance.
     * @param key The considered key.
     * @return The String value if available in the properties, null otherwise.
     */
    public String parseStringFromProperty(final Logger logger,
            final String propertiesSourceName,
            final Properties properties,
            final String key) {
        final String strValue = properties.getProperty(key);
        if (strValue == null) {
            logger.error("Unable to find a value for " + key + " in file: " + propertiesSourceName);
            return null;
        }
        return strValue.trim();
    }

    /**
     * Retrieves the string value from a properties instance for a given key.
     * @param properties The properties instance.
     * @param key The considered key.
     * @param defaultValue The default value.
     * @return The String value if available in the properties, the default value otherwise.
     */
    public String parseStringFromPropertySafe(final Properties properties,
            final String key, final String defaultValue) {
        final String strValue = properties.getProperty(key);
        if (strValue == null) {
            return defaultValue;
        }
        return strValue.trim();
    }

    /**
     * Retrieves the string array value from a properties INSTANCE for a given key.
     * @param logger The logger to use.
     * @param propertiesSourceName The name of the source used to load the properties.
     * @param properties The properties INSTANCE.
     * @param key The considered key.
     * @return The String array value if available in the properties, null otherwise.
     */
    public String[] parseStringArrayFromProperty(final Logger logger,
            final String propertiesSourceName,
            final Properties properties,
            final String key) {
        final String strValue = properties.getProperty(key);
        if (strValue == null) {
            logger.error("Unable to find a value for " + key + " in file: " + propertiesSourceName);
            return null;
        }
        final String[] arrayValue = strValue.split(ATTRIBUTES_SEP);
        for (int i = 0; i < arrayValue.length; i++) {
            arrayValue[i] = arrayValue[i].trim();
        }
        return arrayValue;
    }

    /**
     * Retrieves the integer value from a properties INSTANCE for a given key.
     * @param logger The logger to use.
     * @param propertiesSourceName The name of the source used to load the properties.
     * @param properties The properties INSTANCE.
     * @param key The considered key.
     * @return The Integer value if available in the properties, null otherwise.
     */
    public Integer parseIntegerFromProperty(final Logger logger,
            final String propertiesSourceName,
            final Properties properties,
            final String key) {
        final String strValue = properties.getProperty(key);
        if (strValue == null) {
            logger.error("Unable to find a value for " + key + " in file: " + propertiesSourceName);
            return null;
        }
        try {
            final int intValue = Integer.parseInt(strValue.trim());
            return intValue;
        } catch (NumberFormatException e) {
            logger.error("Invalid value for " + key + ": " + strValue + " in file: " + propertiesSourceName);
            logger.error(e, e);
            return null;
        }
    }

    /**
     * Retrieves the prefix of a key.
     * @param key The key. For instance if the key is esco-dynamicGroups.defaultSourceId
     * then esco-dynamicGroups will be returned.
     * @return The prefix of the key.
     */
    public String retrievePrefix(final String key) {
        if (key.contains(PROP_KEY_SEP)) {
            return key.substring(0, key.indexOf(PROP_KEY_SEP)).trim();
        }
        return key.trim();
    }


}
