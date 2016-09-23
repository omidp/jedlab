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

@Name("subscription")
@Scope(ScopeType.CONVERSATION)
public class SubscriptionAction extends HibernateEntityController
{

    @In(create = true)
    private Renderer renderer;

    /**
     * comma separated email
     */
    private String emails;
    /**
     * comma separated coursename
     */
    private String courseNames;


    

    public String getEmails()
    {
        return emails;
    }

    public void setEmails(String emails)
    {
        this.emails = emails;
    }

    public String getCourseNames()
    {
        return courseNames;
    }

    public void setCourseNames(String courseNames)
    {
        this.courseNames = courseNames;
    }

    public String notifyUsers()
    {
        if (StringUtil.isEmpty(getCourseNames()) || StringUtil.isEmpty(getEmails()))
            return null;
        List<String> courses = Arrays.asList(getCourseNames().trim().split(","));
        List<Course> courseList = getSession().createQuery("select c from Course c where c.name IN :names")
                .setParameterList("names", courses).list();
        //
        List<String> emails = Arrays.asList(getEmails().trim().split(","));
//        List<Member> members = getSession().createQuery("select m from Member m where m.username IN :names")
//                .setParameterList("names", unames).list();
        //
        if (CollectionUtil.isNotEmpty(courseList))
        {
            for (String email : emails)
            {
                Events.instance().raiseAsynchronousEvent("com.jedlab.action.subscription.sendMail", courseList, email);
            }
        }
        
        return "sent";
    }

    @Observer("com.jedlab.action.subscription.sendMail")
    public void sendEmail(List<Course> courseList, String memberEmail)
    {
        if(StringUtil.isNotEmpty(memberEmail))
        {
            Contexts.getConversationContext().set("courses", courseList);
            Contexts.getConversationContext().set("memberEmail", memberEmail);
            renderer.render("/mailTemplates/subscription.xhtml");
        }
    }

}
