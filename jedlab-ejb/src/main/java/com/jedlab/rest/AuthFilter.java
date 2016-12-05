package com.jedlab.rest;

import java.io.IOException;

import javax.security.auth.login.LoginException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.NotLoggedInException;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.web.AbstractFilter;

public class AuthFilter extends AbstractFilter
{

    public static final String TOKEN = "token";
    public static final String USER_NAME = "username";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, final FilterChain chain) throws IOException, ServletException
    {
        if (!(request instanceof HttpServletRequest))
        {
            throw new ServletException("This filter can only process HttpServletRequest requests");
        }

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Force session creation
        httpRequest.getSession();

        new ContextualHttpServletRequest(httpRequest) {
            @Override
            public void process() throws ServletException, IOException, LoginException
            {
                processAuth(httpRequest, httpResponse, chain);
            }

        }.run();

    }

    private void processAuth(HttpServletRequest request, HttpServletResponse response, FilterChain fc) throws ServletException, IOException
    {
        Identity identity = Identity.instance();

        if (identity == null)
        {
            throw new ServletException("Identity not found - please ensure that the Identity component is created on startup.");
        }

        Credentials credentials = identity.getCredentials();

        boolean requireAuth = false;
        String token = request.getHeader(TOKEN);
        String username = request.getHeader(USER_NAME);
        if (token != null && username != null)
        {
            // Only reauthenticate if username doesn't match Identity.username
            // and user isn't authenticated
            if (!username.equals(credentials.getUsername()) || !identity.isLoggedIn())
            {
                try
                {
                    credentials.setPassword(token);
                    authenticate(request, username);
                }
                catch (Exception ex)
                {
                    requireAuth = true;
                }
            }
            if (!identity.isLoggedIn() && !credentials.isSet())
            {
                requireAuth = true;
            }

            try
            {
                if (!requireAuth)
                {
                    fc.doFilter(request, response);
                    return;
                }
            }
            catch (NotLoggedInException ex)
            {
                requireAuth = true;
            }
        }
        else
        {
            requireAuth = true;
        }
        if ((requireAuth && !identity.isLoggedIn()))
        {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not authorized");
        }
    }

    private void authenticate(HttpServletRequest request, final String username) throws ServletException, IOException, LoginException
    {
        Identity identity = Identity.instance();
        identity.getCredentials().setUsername(username);
        identity.authenticate();
    }

}
