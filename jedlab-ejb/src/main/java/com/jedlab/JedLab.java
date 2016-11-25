package com.jedlab;

import javax.servlet.http.HttpSession;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.framework.HibernateEntityController;
import org.jboss.seam.navigation.Pages;

import com.jedlab.action.Constants;
import com.jedlab.framework.CacheManager;
import com.jedlab.framework.WebContext;
import com.jedlab.model.Course.Language;
import com.jedlab.model.Course.Level;
import com.jedlab.model.Student.Gender;
import com.jedlab.model.Student.Privacy;
import com.jedlab.model.Member;

@Name("jedLab")
@Scope(ScopeType.CONVERSATION)
public class JedLab extends HibernateEntityController
{

    public Member getCurrentUser()
    {
        Object u = CacheManager.get(Constants.CURRENT_USER);
        if (u != null)
            return (Member) u;
        Member currentUser = (Member) getSession().get(Member.class,
                Long.parseLong(String.valueOf(getSessionContext().get(Constants.CURRENT_USER_ID))));
        CacheManager.put(Constants.CURRENT_USER, currentUser);
        return currentUser;
    }

    @Factory("courseLevels")
    public Level[] courseLevels()
    {
        return Level.values();
    }

    @Factory("courseLangs")
    public Language[] courseLangs()
    {
        return Language.values();
    }
    
    @Factory("genders")
    public Gender[] genders()
    {
        return Gender.values();
    }
    
    @Factory("privacies")
    public Privacy[] privacies()
    {
        return Privacy.values();
    }

    public String getPageDescription()
    {
        String desc = Pages.instance().getDescription(Pages.getCurrentViewId());
        if(desc == null)
            return null;
        return interpolate(desc);
    }
    
    public String getSessionId()
    {
        HttpSession sess = (HttpSession) Component.getInstance("httpSession");
        if(sess == null)
            return "";
        return sess.getId();
    }
    
    public static JedLab instance()
    {
        if (!Contexts.isConversationContextActive())
        {
            throw new IllegalStateException("No active conversation context");
        }

        JedLab instance = (JedLab) Component.getInstance(JedLab.class, ScopeType.CONVERSATION);

        if (instance == null)
        {
            throw new IllegalStateException("No JedLab could be created");
        }

        return instance;
    }

}
