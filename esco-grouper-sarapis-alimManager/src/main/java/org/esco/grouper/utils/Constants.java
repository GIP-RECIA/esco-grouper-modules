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
package org.esco.grouper.utils;

import edu.internet2.middleware.grouper.privs.Privilege;


/**
 * Constants.
 * @author GIP RECIA - A. Deman
 * 28 juil. 08
 *
 */
public abstract class Constants {

    /** Separator for the name and UAI in the group names .*/
    public static final String NAME_UAI_SEP = "_";

    /** The admin right for a group. */
    public static final Privilege ADMIN_PRIV = Privilege.getInstance("admin");

    /** The view right for a group. */
    public static final Privilege VIEW_PRIV = Privilege.getInstance("view");

    /** The read right for a group. */
    public static final Privilege READ_PRIV = Privilege.getInstance("read");

    /** The update right for a group. */
    public static final Privilege UPDATE_PRIV = Privilege.getInstance("update");

    /** The stem privilege for the stems. */
    public static final Privilege STEM_PRIV = Privilege.getInstance("stem");

    /** The create privilege for the stems. */
    public static final Privilege CREATE_PRIV = Privilege.getInstance("create");

    /** The stem attribute privilege for the stem. */
    public static final Privilege STEM_ATTR_READ_PRIV = Privilege.getInstance("stemAttrRead");

    /** The stem admin privilege for the stem. */
    public static final Privilege STEM_ADMIN_PRIV = Privilege.getInstance("stemAdmin");

    /* The stem update privilege for the stem. */
    public static final Privilege STEM_ATTR_UPDATE_PRIV = Privilege.getInstance("stemAttrUpdate");


    /**
     * Builds an instance of Constants.
     */
    protected Constants() {
        super();
    }

}
