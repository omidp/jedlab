package com.jedlab.dao.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.HibernateEntityController;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;

import com.jedlab.action.Constants;
import com.jedlab.framework.CacheManager;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.WebUtil;
import com.jedlab.model.Member;

@Name("userHome")
@Scope(ScopeType.CONVERSATION)
public class UserHome extends HibernateEntityController
{

    @Logger
    Log logger;
    

    private Member user;


    public void load()
    {
        user = (Member) getSession().get(Member.class, Long.parseLong(String.valueOf(getSessionContext().get(Constants.CURRENT_USER_ID))));
    }


    public Member getUser()
    {
        if (user == null)
            throw new UnsupportedOperationException("unable to find user");
        return user;
    }

    public String update()
    {
        getSession().flush();
        getSession().clear();
        CacheManager.remove(Constants.CURRENT_USER);
        StatusMessages.instance().addFromResourceBundle(Severity.INFO, "User_Updated");
        return "updated";
    }
    
    public Long getCurrentUserId()
    {
        return (Long) getSessionContext().get(Constants.CURRENT_USER_ID);
    }
    
    @Transactional
    public void activateToggle()
    {
        String idParam = WebUtil.getParameterValue("memId");
        if(StringUtil.isNotEmpty(idParam))
        {
            Member mem = (Member) getSession().get(Member.class, Long.parseLong(idParam));
            if(mem.isActive())
                mem.setActive(false);
            else
                mem.setActive(true);
        }
        getSession().flush();
        getSession().clear();
    }

}
