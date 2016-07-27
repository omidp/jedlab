package com.jedlab.filters;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.theme.ThemeSelector;
import org.jboss.seam.web.AbstractFilter;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.jedlab.framework.CookieUtil;

/**
 * @author Omid Pourhadi
 *
 *         omidpourhadi [AT] gmail [DOT] com
 */
@Scope(APPLICATION)
@Name("com.jedlab.framework.themelocaleFilter")
@Install(precedence = Install.FRAMEWORK, value = true)
@BypassInterceptors
@Filter(within = { "org.jboss.seam.web.multipartFilter", "org.jboss.seam.web.authenticationFilter", "org.jboss.seam.web.loggingFilter",
        "org.jboss.seam.web.exceptionFilter", "org.jboss.seam.web.identityFilter" })
public class ThemeLocaleFilter extends AbstractFilter
{

    private static final String GEO_FILE_NAME = "GeoLite2-Country.mmdb";

    private static final String THEME_COOKIE_NAME = "org.jboss.seam.core.Theme";

    private static final String LOCALE_COOKIE_NAME = "org.jboss.seam.core.Locale";
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {

        if (request instanceof HttpServletRequest)
        {
            final HttpServletRequest req = (HttpServletRequest) request;
            final Cookie themeCookie = CookieUtil.findCookieByName(req, THEME_COOKIE_NAME);
            final Cookie localeCookie = CookieUtil.findCookieByName(req, LOCALE_COOKIE_NAME);
            if (themeCookie == null && localeCookie == null)
            {
                new ContextualHttpServletRequest(req) {

                    @Override
                    public void process() throws Exception
                    {
                        doWork(req);
                    }
                }.run();
            }

        }

        chain.doFilter(request, response);

    }

    private void doWork(HttpServletRequest req) throws IOException
    {
        ThemeSelector themeSelector = ThemeSelector.instance();
        LocaleSelector localeSelector = LocaleSelector.instance();
        InputStream stream = null;
        try
        {
            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(GEO_FILE_NAME);
            if (stream == null)
            {
                stream = ThemeLocaleFilter.class.getClassLoader().getResourceAsStream(GEO_FILE_NAME);
            }
            if (stream == null)
            {
                stream = ThemeLocaleFilter.class.getResourceAsStream(GEO_FILE_NAME);
            }
            //
            DatabaseReader reader = new DatabaseReader.Builder(stream).build();

            InetAddress ipAddress = InetAddress.getByName(req.getRemoteAddr());

            if ("127.0.0.1".equals(req.getRemoteAddr()) == false)
            {
                try
                {
                    CountryResponse countryResponse = reader.country(ipAddress);
                    String country = countryResponse.getCountry().getName();
                    if ("Iran".equals(country))
                    {
                        themeSelector.setTheme("persianTheme");
                        localeSelector.setLanguage("fa");
                        localeSelector.setLocale(new Locale("fa", "IR"));
                    }
                    else
                    {
                        themeSelector.setTheme("defaultTheme");
                        localeSelector.setLanguage("en");
                        localeSelector.setLocale(new Locale("en", "US"));
                    }
                }
                catch (GeoIp2Exception e)
                {
                    // DO NOTHING
                }
            }

        }
        finally
        {
            IOUtils.closeQuietly(stream);
        }        
    }

}
