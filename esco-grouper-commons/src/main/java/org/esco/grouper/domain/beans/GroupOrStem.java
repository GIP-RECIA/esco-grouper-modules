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
