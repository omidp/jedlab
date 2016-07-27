package com.jedlab.framework;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [DOT] com
 */
public final class CookieUtil
{

    public static void addCookie(Cookie cookie, HttpServletResponse response)
    {
        response.addCookie(cookie);
    }

    public static Cookie findCookieByName(HttpServletRequest request, String key)
    {
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
        {
            for (int i = 0; i < cookies.length; i++)
            {
                Cookie cookie = cookies[i];
                if (cookie.getName().equals(key))
                {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static void removeCookie(ServletResponse response, Cookie cookie)
    {
        if (cookie != null && response != null)
        {
            cookie.setValue(null);
            cookie.setMaxAge(0);
            ((HttpServletResponse) response).addCookie(cookie);
        }
    }

    public static String getCookieValue(String key, HttpServletRequest request)
    {
        Cookie cookie = (Cookie) findCookieByName(request, key);
        if (cookie == null)
            return null;
        return cookie.getValue();
    }
    
    
    public static String getCookieName(String uri)
    {
        int pos = uri.lastIndexOf(".");
        if (pos != -1)
        {
            uri = uri.substring(0, pos);
        }
        // cookie name cannot contains semicolon
        if (uri.indexOf(":") > 0)
        {
            // jsfId
            uri = uri.substring(uri.lastIndexOf(":") + 1);
        }
        // better regular expression can do this
        return uri.replaceAll(":", "_").replaceAll("/", "_").replaceAll("\\.", "") + "_sortState";
    }

    

}
