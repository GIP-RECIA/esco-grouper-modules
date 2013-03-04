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

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.Stem;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.esco.grouper.exceptions.EscoGrouperException;



/**
 * used to manage in the same way a group or a stem.
 * @author GIP RECIA - A. Deman
 * 15 mai 08
 *
 */
public class GroupOrStem implements Serializable {

    /** Serial version UID.*/
    private static final long serialVersionUID = -8707511102005096577L;

    /** Logger.*/
    private static final Logger LOGGER = Logger.getLogger(GroupOrStem.class);

    /** The group. */
    private Group group;

    /** The stem. */
    private Stem stem;

    /**
     * Constructor for GroupOrStem.
     * @param stem The stem.
     */
    public GroupOrStem(final Stem stem) {
        this.stem = stem;
    }

    /**
     * Constructor for GroupOrStem.
     * @param group The group.
     */
    public GroupOrStem(final Group group) {
        this.group = group;
    }

    /**
     * Gives the String representation of the instance.
     * @return The String representation of the instance.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String image = getClass().getSimpleName() + "#{";
        if (isGroup()) {
            image += "group, " + asGroup();

        } else if (isStem()) {
            image += "stem, " + asStem();
        }
        image += "}";
        return image;

    }

    /**
     * Tests if the instance denotes a group.
     * @return True if the instance denotes a group.
     */
    public boolean isGroup() {
        return group != null;
    }

    /**
     * Tests if the instance denotes a stem.
     * @return True if the instance denotes a stem.
     */
    public boolean isStem() {
        return stem != null;
    }

    /**
     * Gives the group underlying by this instance.
     * @return The group.
     */
    public Group asGroup() {
        if (isStem()) {
            final String msg = "ERROR : trying to access to a folder as a group.";
            LOGGER.error(msg);
            throw new EscoGrouperException(msg);
        }
        return group;
    }

    /**
     * Gives the stem underlying by this instance.
     * @return The stem.
     */
    public Stem asStem() {
        if (isGroup()) {
            final String msg = "Error: Trying to access to a group as a folder.";
            LOGGER.error(msg);
            throw new EscoGrouperException(msg);
        }
        return stem;
    }
}
