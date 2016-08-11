package com.jedlab.framework.jsf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.faces.application.FacesMessage;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.international.StatusMessage.Severity;

/**
 * @author Omid Pourhadi
 *         <p>
 *         <b>Note : </b> must use in actions only when redirect takes place
 *         unless it has leak memory consumption if no redirect takes place
 *         because of session
 *         </p>
 */
@Name("flashScope")
@Scope(ScopeType.CONVERSATION)
public class FlashScope implements Serializable
{

    private static final String SESSION_FLASH_VAR = "flashSession";

    private static final String MSG_FLASH_VAR = "flashMessage";

    private Map<String, Object> flashes;

    public void addMessage(String message)
    {
        put(MSG_FLASH_VAR + "_" + getFlashes().size(), message);
    }

    public void addMessage(FacesMessage message)
    {
        put(MSG_FLASH_VAR + "_" + getFlashes().size(), message);
    }

    public void addMessage(Severity severity, String msg)
    {
        // Severity error = severity.ERROR;
        FacesMessage fm = new FacesMessage(javax.faces.application.FacesMessage.SEVERITY_INFO, msg, msg);
        switch (severity)
        {
        case ERROR:
            fm = new FacesMessage(javax.faces.application.FacesMessage.SEVERITY_ERROR, msg, msg);
            break;
        case WARN:
            fm = new FacesMessage(javax.faces.application.FacesMessage.SEVERITY_WARN, msg, msg);
            break;
        default:
            break;
        }
        put(MSG_FLASH_VAR + "_" + getFlashes().size(), fm);
    }

    public void removeMessage()
    {
        remove(MSG_FLASH_VAR + "_" + getFlashes().size());
    }

    public void add(String key, Object value)
    {
        put(key, value);
    }

    private void remove(String key)
    {
        getFlashes().remove(key);
        Contexts.getSessionContext().remove(SESSION_FLASH_VAR);
    }

    private void put(String key, Object value)
    {
        getFlashes().put(key, value);
        Contexts.getSessionContext().set(SESSION_FLASH_VAR, getFlashes());
    }

    protected Map<String, Object> get()
    {
        Map<String, Object> flashObjects = (Map<String, Object>) Contexts.getSessionContext().get(SESSION_FLASH_VAR);
        if (flashObjects == null)
            return new HashMap<String, Object>();
        return flashObjects;
    }

    protected void clear()
    {
        Contexts.getSessionContext().remove(SESSION_FLASH_VAR);
        getFlashes().clear();
    }

    private Map<String, Object> getFlashes()
    {
        if (flashes == null)
            flashes = new ConcurrentHashMap<String, Object>();
        return flashes;
    }

    public static FlashScope instance()
    {
        if (!Contexts.isSessionContextActive())
        {
            throw new IllegalStateException("No active session context");
        }

        FlashScope instance = (FlashScope) Component.getInstance(FlashScope.class, ScopeType.CONVERSATION);

        if (instance == null)
        {
            throw new IllegalStateException("No FlashScope could be created");
        }

        return instance;
    }

}
