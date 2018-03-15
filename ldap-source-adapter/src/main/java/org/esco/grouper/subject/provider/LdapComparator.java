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
/*
   simple subject comparator
*/

package org.esco.grouper.subject.provider;

import java.util.Comparator;

import edu.internet2.middleware.subject.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class LdapComparator implements Comparator {

   private static Log log = LogFactory.getLog(LdapComparator.class);

   public LdapComparator() {
	super();
   }

   public int compare(Object so0, Object so1) {
   
       try {

           Subject s0 = (Subject) so0;
           Subject s1 = (Subject) so1;
           String s0d = s0.getDescription();
           String s1d = s1.getDescription();

           // log.debug("comparing " + s0d + " to " + s1d);
           return s0d.compareTo(s1d);
	} catch (Exception e) {
           log.debug("exception " + e);
        }
        return (1);
   }
}
