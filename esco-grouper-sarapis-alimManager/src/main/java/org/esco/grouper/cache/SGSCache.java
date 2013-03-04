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

package org.esco.grouper.cache;



import java.util.Arrays;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.esco.grouper.domain.beans.GroupOrFolderDefinition;
import org.esco.grouper.domain.beans.PersonType;


/**
 * Cache manager for the Sarapis Group Service.
 * This cache is a high level cache as Grouper manage its own caches.
 * @author GIP RECIA - A. Deman
 * 30 July 08
 *
 */
public class SGSCache {



    /** Cache name for the memberships. */
    private static final String MEMBERSHIPS_CACHE_NAME =  SGSCache.class + ".memberships";

    /** Cache name for the memberships for groups defined as template. */
    private static final String MEMBERSHIPS_FOR_TEMPLATES_CACHE_NAME =  SGSCache.class + ".memberships-for-templates";

    /** Cache name for the template which are created even if they are empty. */
    private static final String EMPTY_TEMPLATES_CACHE_NAME =  SGSCache.class + ".empty-templates";

    /** Cache name for the groups or folders memberships. */
    private static final String GROUPS_MEMBERSHIPS_CACHE_NAME =  SGSCache.class + ".gof-memberhips";

    /** Cache name for the groups or folders privileges. */
    private static final String GROUPS_PRIVILEGES_CACHE_NAME =  SGSCache.class + ".gof-privileges";

    /** Singleton. */
    private static final SGSCache INSTANCE = new SGSCache();


    /** Cache for the memberships. */
    private Cache membershipsCache;

    /** Cache for the memberships for template groups. */
    private Cache membershipsTemplatesCache;

    /** Cache for the empty templates. */
    private Cache emptyTemplatesCache;

    /** Cache for the groups memeberships. */
    private Cache groupsMembershipsCache;

    /** Cache for the groups privileges. */
    private Cache groupsPrivilegesCache;


    /**
     * Builds an instance of SGSCache.
     */
    protected SGSCache() {
        final CacheManager cacheManager = CacheManager.getInstance();

        if (!cacheManager.cacheExists(MEMBERSHIPS_CACHE_NAME)) {
            cacheManager.addCache(MEMBERSHIPS_CACHE_NAME);
        }

        if (!cacheManager.cacheExists(MEMBERSHIPS_FOR_TEMPLATES_CACHE_NAME)) {
            cacheManager.addCache(MEMBERSHIPS_FOR_TEMPLATES_CACHE_NAME);
        }

        if (!cacheManager.cacheExists(EMPTY_TEMPLATES_CACHE_NAME)) {
            cacheManager.addCache(EMPTY_TEMPLATES_CACHE_NAME);
        }
        if (!cacheManager.cacheExists(GROUPS_MEMBERSHIPS_CACHE_NAME)) {
            cacheManager.addCache(GROUPS_MEMBERSHIPS_CACHE_NAME);
        }
        if (!cacheManager.cacheExists(GROUPS_PRIVILEGES_CACHE_NAME)) {
            cacheManager.addCache(GROUPS_PRIVILEGES_CACHE_NAME);
        }

        membershipsCache = cacheManager.getCache(MEMBERSHIPS_CACHE_NAME);
        membershipsTemplatesCache = cacheManager.getCache(MEMBERSHIPS_FOR_TEMPLATES_CACHE_NAME);
        emptyTemplatesCache = cacheManager.getCache(EMPTY_TEMPLATES_CACHE_NAME);
        groupsMembershipsCache = cacheManager.getCache(GROUPS_MEMBERSHIPS_CACHE_NAME);
        groupsPrivilegesCache = cacheManager.getCache(GROUPS_PRIVILEGES_CACHE_NAME);
    }

    /**
     * Gives the singleton instance.
     * @return The available instance.
     */
    public static SGSCache instance() {
        return INSTANCE;
    }

    /**
     * Checks if the memberships for a group is in cache.
     * @param groupName The name of the group/
     * @return True if groups name is in the groups memberships cache.
     */
    public boolean hasInGroupsMembershipsCache(final String groupName) {
        return groupsMembershipsCache.get(groupName) != null;
    }


    /**
     * Adds a group in the groups membership cache.
     * @param groupName The name of the group to cache.
     */
    public void cacheInGroupsMembershipsCache(final String groupName) {
        groupsMembershipsCache.put(new Element(groupName, ""));
    }

    /**
     * Checks if the memberships for a group is in cache.
     * @param groupName The name of the group/
     * @return True if groups name is in the groups memberships cache.
     */
    public boolean hasInGroupsPrivielgesCache(final String groupName) {
        return groupsPrivilegesCache.get(groupName) != null;
    }


    /**
     * Adds a group in the groups membership cache.
     * @param groupName The name of the group to cache.
     */
    public void cacheInGroupsPrivilegesCache(final String groupName) {
        groupsPrivilegesCache.put(new Element(groupName, ""));
    }



    /**
     * Caches the memberships for a given type of member and a set of attributes.
     * @param definitions The groups definitions.
     * @param type The type of members.
     * @param attributes The list of attributes.
     */
    public void cacheMemberships(final Set<GroupOrFolderDefinition> definitions,
            final PersonType type,
            final String...attributes) {
        final String key = type + Arrays.toString(attributes);
        membershipsCache.put(new Element(key, definitions));
    }



    /**
     * Caches the memberships of template groups for a given type of member and a set of attributes.
     * @param definitions The groups definitions.
     * @param type The type of members.
     * @param attributes The list of attributes.
     */
    public void cacheMemebrshipsForTemplates(final Set<GroupOrFolderDefinition> definitions,
            final PersonType type,
            final String...attributes) {
        final String key = type + Arrays.toString(attributes);
        membershipsTemplatesCache.put(new Element(key, definitions));
    }

    /**
     * Tries to retrieve the memberships from the cache.
     * @param type The type of the member.
     * @param attributes The attributes of the member.
     * @return The memberships if found, null otherwise
     */
    @SuppressWarnings("unchecked")
    public Set<GroupOrFolderDefinition> getMemberships(final PersonType type,
            final String...attributes) {
        final String key = type + Arrays.toString(attributes);
        final Element elt = membershipsCache.get(key);
        if (elt != null) {

            return (Set<GroupOrFolderDefinition>) elt.getObjectValue();
        }
        return null;
    }

    /**
     * Tries to retrieve the memberships for template groups from the cache.
     * @param type The type of the member.
     * @param attributes The attributes of the member.
     * @return The memberships if found, null otherwise
     */
    @SuppressWarnings("unchecked")
    public Set<GroupOrFolderDefinition> getMembershipsForTemplates(final PersonType type,
            final String...attributes) {
        final String key = type + Arrays.toString(attributes);
        final Element elt = membershipsTemplatesCache.get(key);
        if (elt != null) {

            return (Set<GroupOrFolderDefinition>) elt.getObjectValue();
        }
        return null;
    }

    /**
     * Tests if an empty template is cached.
     * @param definition The definition that corresponds to the empty template.
     * @return True if the definition is cached.
     */
    public boolean emptyTemplateIsCached(final GroupOrFolderDefinition definition) {
        return emptyTemplatesCache.get(definition.getPath()) != null;
    }

    /**
     * Caches the definition for an empty template.
     * @param definition The definition associated to the template.
     */
    public void cacheEmptyTemplate(final GroupOrFolderDefinition definition) {
        emptyTemplatesCache.put(new Element(definition.getPath(), true));
    }
}
