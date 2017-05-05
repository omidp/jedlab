package com.jedlab.framework;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesManager;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;

@Name("webContext")
@Scope(SESSION)
@BypassInterceptors
@Startup
public class WebContext implements Serializable
{

    
    public String getContextPath()
    {
        HttpServletRequest request = (HttpServletRequest) Component.getInstance("httpRequest");
        if (request == null)
            return null;
        return request.getContextPath();
    }

    public void checkPermission(String target, String action, String msg)
    {
        if (!Identity.instance().hasPermission(target, action))
        {
            throw new AuthorizationException(msg);
        }
    }

    public String uploadFolder()
    {
        return FacesContext.getCurrentInstance().getExternalContext().getInitParameter("uploadFolder");
    }

    public String getClientIP()
    {
        // apache ProxyPass requests
        HttpServletRequest request = (HttpServletRequest) Component.getInstance("httpRequest");
        if (request == null)
            return "127.0.0.1";
        return WebUtil.getClientIP(request);
    }

    public String getClientUserAgent()
    {
        HttpServletRequest request = (HttpServletRequest) Component.getInstance("httpRequest");
        if (request == null)
            return "Unknown Device";
        return WebUtil.getClientUserAgent(request);
    }
    
    public String getCurrentView()
    {
        return Pages.getCurrentViewId();
    }

    public boolean isPostback()
    {
        return getFacesContext().getRenderKit().getResponseStateManager().isPostback(getFacesContext());
    }

    private FacesContext getFacesContext()
    {
        return FacesContext.getCurrentInstance();
    }
    
    public void redirectIt()
    {
        redirectIt(false, true);
    }

    public void redirectIt(boolean includePageParameters)
    {
        redirectIt(false, includePageParameters);
    }

    public void redirectIt(boolean includeConversationId, boolean includePageParameters)
    {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("pageNumber", 0);
        FacesManager.instance().redirect(Pages.getCurrentViewId(), parameters, includeConversationId, includePageParameters);
    }
    
    public void redirectIt(boolean includeConversationId, boolean includePageParameters, Map<String, Object> parameters)
    {
        if(parameters == null)
            parameters = new HashMap<>();
        FacesManager.instance().redirect(Pages.getCurrentViewId(), parameters, includeConversationId, includePageParameters);
    }
    
    public static WebContext instance()
    {
        if (!Contexts.isSessionContextActive())
        {
            throw new IllegalStateException("No active session context");
        }

        WebContext instance = (WebContext) Component.getInstance(WebContext.class, ScopeType.SESSION);

        if (instance == null)
        {
            throw new IllegalStateException("No WebContext could be created");
        }

        return instance;
    }
    
}
