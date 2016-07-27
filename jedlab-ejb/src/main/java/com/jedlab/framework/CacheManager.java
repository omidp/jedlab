package com.jedlab.framework;

import java.io.Serializable;

import org.jboss.seam.cache.CacheProvider;
import org.jboss.seam.security.Identity;

public class CacheManager implements Serializable
{

    public static void put(String key, Object value)
    {
        CacheProvider cacheProvider = CacheProvider.instance();
        if (cacheProvider == null)
            return;
        Identity identity = Identity.instance();
        if (identity.isLoggedIn() == false)
            return;
        String username = identity.getCredentials().getUsername();
        cacheProvider.put(username, key, value);

    }

    public static Object get(String key)
    {
        CacheProvider cacheProvider = CacheProvider.instance();
        if (cacheProvider == null)
            return null;
        Identity identity = Identity.instance();
        if (identity.isLoggedIn() == false)
            return null;
        String username = identity.getCredentials().getUsername();
        return cacheProvider.get(username, key);
    }

    public static void remove(String key)
    {
        CacheProvider cacheProvider = CacheProvider.instance();
        if (cacheProvider == null)
            return;
        Identity identity = Identity.instance();
        if (identity.isLoggedIn() == false)
            return;
        String username = identity.getCredentials().getUsername();
        cacheProvider.remove(username, key);
    }

    public static void clearCache()
    {
        CacheProvider cacheProvider = CacheProvider.instance();
        if (cacheProvider == null)
            return;
        cacheProvider.clear();
    }

}
