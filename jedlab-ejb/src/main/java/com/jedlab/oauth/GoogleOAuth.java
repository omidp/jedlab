package com.jedlab.oauth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.core.Expressions;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

import com.github.scribejava.core.oauth.OAuth20Service;
import com.jedlab.framework.CookieUtil;

public class GoogleOAuth extends HttpServlet
{

    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        new ContextualHttpServletRequest(request) {

            @Override
            public void process() throws Exception
            {
                Cookie c = CookieUtil.findCookieByName(request, "captchaRequired");
                if (c != null)
                    CookieUtil.removeCookie(response, c);
                OAuth20Service service = (OAuth20Service) Expressions.instance().createValueExpression("#{googleOAuth}").getValue();
                // pass access_type=offline to get refresh token
                // https://developers.google.com/identity/protocols/OAuth2WebServer#preparing-to-start-the-oauth-20-flow
                final Map<String, String> additionalParams = new HashMap<>();
                additionalParams.put("access_type", "offline");
                // force to reget refresh token (if usera are asked not the
                // first time)
                additionalParams.put("prompt", "consent");
                response.sendRedirect(service.getAuthorizationUrl(additionalParams));
            }
        }.run();
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }

}
