package com.jedlab.framework;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.navigation.Pages;
import org.jboss.seam.web.Parameters;

import com.jedlab.framework.exceptions.ServiceException;

/**
 * @author Omid Pourhadi
 *
 */
public class WebUtil
{

    public static String getParameterValue(String paramName)
    {
        Parameters parametersInstance = Parameters.instance();
        if (parametersInstance == null)
            throw new ServiceException(0);
        String paramVal = (String) parametersInstance.convertMultiValueRequestParameter(parametersInstance.getRequestParameters(),
                paramName, String.class);
        return paramVal;
    }

    public static Map<String, Object> getPageParameters()
    {
        return Pages.instance().getStringValuesFromPageContext(FacesContext.getCurrentInstance());
    }

    public static String getClientUserAgent(HttpServletRequest request)
    {
        if (request == null)
            return "Unknown Device";
        if (request.getHeader("User-Agent") != null)
            return request.getHeader("User-Agent");
        return "Unknown Device";
    }

    public static String getClientIP(HttpServletRequest request)
    {
        // apache ProxyPass requests
        if (request == null)
            return "127.0.0.1";
        if ("127.0.0.1".equals(request.getRemoteAddr()) == false)
        {
            return request.getRemoteAddr();
        }
        if (request.getHeader("x-forwarded-for") != null)
            return request.getHeader("x-forwarded-for");
        // nginx ProxyPass requests
        if (request.getHeader("X-Real-IP") != null)
            return request.getHeader("X-Real-IP");
        // direct requests
        return request.getRemoteAddr();
    }

}
