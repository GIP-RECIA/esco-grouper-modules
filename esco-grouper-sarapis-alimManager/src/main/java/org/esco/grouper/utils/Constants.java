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

    /**
     * Builds an instance of Constants.
     */
    protected Constants() {
        super();
    }

}
