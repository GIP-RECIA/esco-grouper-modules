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
package org.esco.grouper.subject.provider;

import java.util.Properties;

import edu.internet2.middleware.subject.SubjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Search {
    private static Log log = LogFactory.getLog(Search.class);
    protected Properties params = new Properties();
    protected String searchType = null;

    public Search() {
    }

    public void setSearchType(String searchType1) {
        this.searchType = searchType1;
    }

    public String getSearchType() {
        return this.searchType;
    }

    public void addParam(String name, String value) {
        this.params.setProperty(name, value);
    }

    protected String getParam(String name) {
        return this.params.getProperty(name);
    }

    protected Properties getParams() {
        return this.params;
    }

    public String toString() {
        return "Search [params=" + SubjectUtils.propertiesToString(this.params) + ", searchType=" + this.searchType + "]";
    }
}