package com.jedlab.framework.jsf;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessages;

/**
 * @author Omid Pourhadi
 *
 */
public class FlashPhaseListener implements PhaseListener
{

    public FlashPhaseListener()
    {
    }

    @Override
    public void afterPhase(PhaseEvent event)
    {
        FlashScope.instance().clear();
    }

    @Override
    public void beforePhase(PhaseEvent event)
    {
        Map<String, Object> map = FlashScope.instance().get();
        for (Map.Entry<String, Object> item : map.entrySet())
        {
            Object msg = item.getValue();
            if (msg != null)
            {
                if (msg instanceof String)
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(String.valueOf(msg)));
                else if (msg instanceof FacesMessage)
                    FacesContext.getCurrentInstance().addMessage(null, (FacesMessage) msg);
                else if (msg instanceof StatusMessage)
                {
                    StatusMessage sm = (StatusMessage) msg;
//                    FacesContext.getCurrentInstance().addMessage(null, FacesMessages.createFacesMessage(sm.getSeverity(), null, null));
                }
                else
                {
                    //Outjection
                    Contexts.getConversationContext().set(item.getKey(), item.getValue());
                }
            }
        }

    }

    @Override
    public PhaseId getPhaseId()
    {
        return PhaseId.RENDER_RESPONSE;
    }

}
