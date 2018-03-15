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

import java.util.Set;
import java.util.Map;

import edu.internet2.middleware.subject.provider.SubjectImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Ldap Subject implementation.
 */
public class LdapSubject extends SubjectImpl {
	

	private static Log log = LogFactory.getLog(LdapSubject.class);
	
	/*
	 * Constructor called by SourceManager.
	 */
	protected LdapSubject(String id, String name, String description,
			String type, String sourceId) {
                super(id, name, description, type, sourceId);
		log.debug("LdapSubject Name = "  + name);
	}
	protected LdapSubject(String id, String name, String description,
			String type, String sourceId, Map<String, Set<String>> attributes) {
                super(id, name, description, type, sourceId, attributes);
        }

  /** if we have try to get all the attributes */
  private boolean attributesGotten = false;

  /** 
   * setter for gotten
   */
  public void setAttributesGotten(boolean v) {
      attributesGotten = v;
  }

  /**
   * Try to get more attributes (might be different search)
   */
  private void getAllAttributes() {
    if (!this.attributesGotten) {
      try {
        ((LdapSourceAdapter)this.getSource()).getAllAttributes(this);
      } finally {
        this.attributesGotten = true;
      }
    }
  }

  /** Some attrs might require get all attrs */

  /** name and description will be defined, but empty */
  @Override
  public String getName() {
    if (super.getName().length()>0) return super.getName();
    this.getAllAttributes();
    return super.getName();
  }
  @Override
  public String getDescription() {
    if (super.getDescription().length()>0) return super.getDescription();
    log.debug("getting all (description), gotten = " + attributesGotten);
    this.getAllAttributes();
    log.debug("got all (description), gotten=" + attributesGotten + ", desc=" + super.getDescription());
    return super.getDescription();
  }

  /** other attrs will be null */

  @Override
  public Map<String, Set<String>> getAttributes() {
    this.getAllAttributes();
    return super.getAttributes();
  }

  @Override
  public String getAttributeValue(String name1) {
    this.getAllAttributes();
    return super.getAttributeValue(name1);
  }
  @Override
  public String getAttributeValueOrCommaSeparated(String attributeName) {
    this.getAllAttributes();
    return super.getAttributeValueOrCommaSeparated(attributeName);
  }
  @Override
  public Set<String> getAttributeValues(String name1) {
    this.getAllAttributes();
    return super.getAttributeValues(name1);
  }
  @Override
  public String getAttributeValueSingleValued(String attributeName) {
    this.getAllAttributes();
    return super.getAttributeValueSingleValued(attributeName);
  }

}
