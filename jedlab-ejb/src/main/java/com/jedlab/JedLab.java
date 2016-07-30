package com.jedlab;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.HibernateEntityController;

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

}
