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
 * Package for the beans.
 */
package org.esco.grouper.domain.beans;


import java.io.Serializable;

/**
 * Data (key and description) about a group, a subject or a stem fetched from Grouper.
 * @author GIP RECIA - A. Deman
 * 27 nov. 07
 *
 */
public class GrouperDTO implements Serializable {
    
    /** Constant for a DTO that denotes a subject. */
    private static final int SUBJECT = 0;

    /** Constant for a DTO that denotes a group. */    
    private static final int GROUP = 1;

    /** Constant for a DTO that denotes a stem. */
    private static final int STEM = 3;
    
    /** Serial version UID. */
    private static final long serialVersionUID = 9053240614777750291L;
  
    /** Type of the element underlying the DTO.*/
    private int type;
    
    /** Name of the group. */
    private String key;
    
    /** Name of the group. */
    private String name;
    
    /** Description of the group. */
    private String description;
    
    /** The parent stem in the case of a group. */
    private String parentStem;


    /** If the DTO denotes a group, this flag is true if the group is not a subgroup
     * of another group. If the DTO denotes a Stem, the flag is true if it is the root Stem.*/
    private boolean root;

    /**
     * Constructor for IGroupOrStemInformations.
     */
    public GrouperDTO() { /**/ }
    
       
    /**
     * Constructor for IGroupOrStemInformations.
     * @param type the Type of the underlying grouper element.
     * @param key The key of the group.
     * @param name The name of the group.
     * @param description The description of the group.
     */
    public GrouperDTO(final int type, final String key, final String name, final String description) {
        this.type = type;
        this.key = key;
        this.name = name;
        this.description = description;
    }
    
    /**
     * Constructor for IGroupOrStemInformations.
     * @param type the Type of the underlying grouper element.
     * @param parentStem the name of the parent stem (only for groups).
     * @param root True if the group is not contained in another group.
     * @param key The key of the group.
     * @param name The name of the group.
     * @param description The description of the group.
     */
    public GrouperDTO(final int type,
            final String parentStem,
            final boolean root,
            final String key, 
            final String name, 
            final String description) {
        this(type, key, name, description);
        this.parentStem = parentStem;
        this.root = root;
    }
    
    
    /**
     * Creates a DTO that denotes a subject.
     * @param key The key of the subject.
     * @param name The name of the subject.
     * @param description The description of the subject.
     * @return The new instance of DTO.
     */
    public static GrouperDTO createSubjectDTO(final String key, 
            final String name, 
            final String description) {
        return new GrouperDTO(SUBJECT, key, name, description);
    }
    
    
    /**
     * Creates a DTO that denotes a stem.
     * @param parentStem the name of the parent stem.
     * @param root True if the stem is the root stem.
     * @param key The key of the stem.
     * @param name The name of the stem.
     * @param description The description of the stem.
     * @return The new instance of DTO.
     */
    public static GrouperDTO createStemDTO(final String parentStem,
            final boolean root,
            final String key, 
            final String name, 
            final String description) {
        return new GrouperDTO(STEM, parentStem, root, key, name, description);
    }
    
    /**
     * Creates a DTO that denotes a group.
     * @param parentStem the name of the parent stem.
     * @param root True if the group is not contained in another group.
     * @param key The key of the group.
     * @param name The name of the group.
     * @param description The description of the group.
     * @return The new instance of DTO.
     */
    public static GrouperDTO createGroupDTO(final String parentStem,
            final boolean root,
            final String key, 
            final String name, 
            final String description) {
        return new GrouperDTO(GROUP, parentStem, root, key, name, description);
    }

    /**
     * Gives the hash value for this instance.
     * @return The hash value. 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (key == null) {
            return 0;
        } 
        return key.hashCode();
    }   
    
    /**
     * Tests if an object is equal to this instance.
     * @param o The object to test.
     * @return True if o is an instance of IGroupOrStemInformations 
     * equal to this instance.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof GrouperDTO)) {
            return false;
        }
        
        final GrouperDTO gsi = (GrouperDTO) o; 
        if (key != null) {
            return key.equals(gsi.getKey());
        }
        
        return gsi.getKey() == null;
    }
    
    /**
     * Gives the string representation of this object.
     * @return The string representation of the values of this object.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName());
        sb.append("#(");
        
        if (hasParentStem()) {
            sb.append("(");
            sb.append(parentStem);
            sb.append("), ");
            
            if (isRoot()) {
                sb.append("root, ");
            } 
        }
        
        sb.append(key);
        sb.append(", ");
        sb.append(name);
        sb.append(", ");
        sb.append(description);
        sb.append(")");
        return sb.toString();
    }
    
    /**
     * Tests if the DTO denotes a subject.
     * @return True if the type of this DTO is SUBJECT.
     */
    public boolean denotesSubject() {
        return type == SUBJECT;
    }
    
    /**
     * Tests if the DTO denotes a stem.
     * @return True if the type of this DTO is STEM.
     */
    public boolean denotesStem() {
        return type == STEM;
    }
     
    /**
     * Tests if the DTO denotes a group.
     * @return True if the type of this DTO is GROUP.
     */
    public boolean denotesGroup() {
        return type == GROUP;
    }
    
    /**
     * Tests if the DTO has a parentStem information.
     * @return True if the DTO has a parentStem information.
     */
    public boolean hasParentStem() {
        return parentStem != null;
    }
    
    /**
     * Getter for parentStem.
     * @return parentStem.
     */
    public String getParentStem() {
        return parentStem;
    }
    
    /**
     * Getter for root.
     * @return the root
     */
    public boolean isRoot() {
        return root;
    }

    /**
     * Setter for root.
     * @param root the root to set
     */
    public void setRoot(final boolean root) {
        this.root = root;
    }
    
    /**
     * Setter for parentStem.
     * @param parentStem The new value for parentStem.
     */
    public void setParentStem(final String parentStem) {
        this.parentStem = parentStem;
    }
        
    /**
     * Getter for key.
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Setter for key.
     * @param key the key to set
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * Getter for description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for description.
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Getter for name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name.
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }


    /**
     * Getter for type.
     * @return the type
     */
    public int getType() {
        return type;
    }


    /**
     * Setter for type.
     * @param type the type to set
     */
    public void setType(final int type) {
        this.type = type;
    }
}
