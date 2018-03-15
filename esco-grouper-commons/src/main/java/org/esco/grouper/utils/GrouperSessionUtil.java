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

import java.text.DateFormat;

import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.exception.SessionException;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import edu.internet2.middleware.subject.SubjectNotUniqueException;
import org.apache.log4j.Logger;
import org.esco.grouper.exceptions.EscoGrouperException;
/**
 * Util class used to handle the grouper sessions.
 * @author GIP RECIA - A. Deman
 * 28 juil. 08
 *
 */
public class GrouperSessionUtil{

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(GrouperSessionUtil.class);

    private static final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG,
                           DateFormat.LONG);

    /** Subject id used to open the sessions. */
    private String subjectId;

    /**
     * Builds an instance of GrouperSessionUtil.
     */
    public GrouperSessionUtil() {
        super();
    }

    /**
     * Builds an instance of GrouperSessionUtil.
     * @param subjectId The subject id used to open sessions.
     */
    public GrouperSessionUtil(final String subjectId) {
        this.subjectId = subjectId;
    }

    /**
     * Creates a Grouper session instance.
     * @return The session object.
     */
    public GrouperSession createSession() {

        try {
            final GrouperSession session = GrouperSession.startBySubjectIdAndSource(subjectId, null);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Starting a new session: " + session.getSessionId());
            }
            return session;

        } catch (SubjectNotFoundException e) {
            LOGGER.error(e, e);
            throw new EscoGrouperException(e);
        } catch (SubjectNotUniqueException e) {
            LOGGER.error(e, e);
            throw new EscoGrouperException(e);
        } catch (SessionException e) {
            LOGGER.error(e, e);
            throw new EscoGrouperException(e);
        }
    }

    /**
     * Closes a grouper session.
     * @param session The session to close.
     */
    public void stopSession(final GrouperSession session) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Stopping the session : " + session.getSessionId() + ", that were started at :" + df.format(session.getStartTime()));
            }
            session.stop();
        } catch (SessionException e) {
            LOGGER.error(e, e);
        }
    }

    /**
     * Getter for subjectId.
     * @return subjectId.
     */
    public String getSubjectId() {
        return subjectId;
    }

    /**
     * Setter for subjectId.
     * @param subjectId the new value for subjectId.
     */
    public void setSubjectId(final String subjectId) {
        this.subjectId = subjectId;
    }
}
