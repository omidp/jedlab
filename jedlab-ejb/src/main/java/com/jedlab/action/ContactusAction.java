package com.jedlab.action;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.international.StatusMessages;

@Name("contactusAction")
@Scope(ScopeType.EVENT)
public class ContactusAction implements Serializable
{
    
    
    private Contatcus contactus = new Contatcus();
    
    @In(create = true)
    Renderer renderer;
    
    

    public Contatcus getContactus()
    {
        return contactus;
    }

    
    public String contact()
    {
        StatusMessages.instance().addFromResourceBundle("Sent_OK");
        Events.instance().raiseAsynchronousEvent(Constants.SEND_CONTACTUS_MAIL, getContactus().getEmail(), getContactus().getTitle(), getContactus().getContent());
        return "successful";
    }
    
    @Observer(Constants.SEND_CONTACTUS_MAIL)
    public void sendContactusEmail(String email, String title, String content)
    {
        Contexts.getConversationContext().set("title", title);
        Contexts.getConversationContext().set("email", email);
        Contexts.getConversationContext().set("content", content);
        renderer.render("/mailTemplates/contactus.xhtml");
    }


    public static class Contatcus implements Serializable
    {
        private String title;
        private String content;
        private String email;

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public String getContent()
        {
            return content;
        }

        public void setContent(String content)
        {
            this.content = content;
        }

        public String getEmail()
        {
            return email;
        }

        public void setEmail(String email)
        {
            this.email = email;
        }

    }

}
