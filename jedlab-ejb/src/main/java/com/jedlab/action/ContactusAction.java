package com.jedlab.action;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("contactusAction")
@Scope(ScopeType.EVENT)
public class ContactusAction implements Serializable
{
    
    
    private Contatcus contactus = new Contatcus();
    
    

    public Contatcus getContactus()
    {
        return contactus;
    }

    
    public String contact()
    {
        
        return "successful";
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
