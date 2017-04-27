package com.jedlab.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.framework.HibernateEntityController;

import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.StringUtil;
import com.jedlab.model.Course;
import com.jedlab.model.Member;

@Name("announcementAction")
@Scope(ScopeType.CONVERSATION)
public class AnnouncementAction extends HibernateEntityController
{

    @In(create = true)
    private Renderer renderer;

    private String content;

    private String subject;

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String notifyUsers()
    {
        if (StringUtil.isEmpty(getContent()))
            return null;
        
        //
        List<Member> members = getSession().createQuery("select m from Member m where m.username is not null").list();
        //
        if (StringUtil.isNotEmpty(getSubject()))
        {
            for (Member m : members)
            {
                Events.instance().raiseAsynchronousEvent(Constants.SEND_MAIL_ANNOUNCEMENT, getSubject(), m, getContent());
            }
        }

        getStatusMessages().addFromResourceBundle("Email_Sent_Successfull");

        return "sent";
    }

    @Observer(value=Constants.SEND_MAIL_ANNOUNCEMENT)
    public void sendEmail(String subject, Member member, String content)
    {
        if (member != null)
        {
            Contexts.getConversationContext().set("subject", subject);
            Contexts.getConversationContext().set("username", member.getUsername());
            Contexts.getConversationContext().set("memberEmail", member.getEmail());
            Contexts.getConversationContext().set("content", content);
            renderer.render("/mailTemplates/announcement.xhtml");
        }
    }

}
