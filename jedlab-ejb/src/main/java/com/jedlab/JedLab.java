package com.jedlab;

import javax.servlet.http.HttpSession;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.HibernateEntityController;
import org.jboss.seam.navigation.Pages;

import com.jedlab.action.Constants;
import com.jedlab.framework.CacheManager;
import com.jedlab.model.Course.Language;
import com.jedlab.model.Course.Level;
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

}
