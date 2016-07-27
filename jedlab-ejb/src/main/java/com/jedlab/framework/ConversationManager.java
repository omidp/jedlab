package com.jedlab.framework;

import java.io.Serializable;
import java.util.logging.Logger;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;

/**
 * @author Omid Pourhadi
 * <b>NOTE: be careful with s:link and do not used this component in create method unless it is tree table and not reference from page.xml</b>
 * <b>mostly used in load method where you can not begin conversation from page.xml or need a new conversation </b>
 */
@Name("conversationManager")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class ConversationManager implements Serializable
{

    
    private static final Logger logger = Logger.getLogger(ConversationManager.class.getName());
    
    public void startNewConversation()
    {
        if (Conversation.instance().isLongRunning())
        {
            Conversation.instance().end(true);
            Conversation.instance().leave();
        }
        logger.info("beginning a new conversation");
        Conversation.instance().begin(true, false);
    }

    public void leaveConversation()
    {
        if (Conversation.instance().isLongRunning())
        {
            Conversation.instance().leave();
            logger.info("leaving conversation");
        }
    }

    public static ConversationManager instance()
    {
        if (!Contexts.isEventContextActive())
        {
            throw new IllegalStateException("No active conversation context");
        }

        ConversationManager instance = (ConversationManager) Component.getInstance(ConversationManager.class, ScopeType.CONVERSATION);

        if (instance == null)
        {
            throw new IllegalStateException("No ConversationManager could be created");
        }

        return instance;
    }

}
