package com.jedlab.action;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.framework.EntityController;
import org.jboss.seam.security.Identity;

import com.jedlab.framework.DateUtil;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TxManager;
import com.jedlab.framework.WebContext;
import com.jedlab.model.LoginActivity;

@Name("loginActionManager")
@Scope(ScopeType.EVENT)
public class LoginActionManager extends EntityController
{

    public LoginActivity addActivity(String username)
    {
        String token = RandomStringUtils.randomAlphanumeric(15);
        Date ttl = DateUtil.addDate(new Date(), Calendar.MINUTE, LoginActivity.TTL);
        LoginActivity la = new LoginActivity(username, new Date(), WebContext.instance().getClientIP(), ttl, WebContext.instance()
                .getClientUserAgent(), new Date(), token);
        getEntityManager().persist(la);
        getEntityManager().flush();
        return la;
    }

    @Observer(value = "org.jboss.seam.security.loggedOut")
    @Transactional
    public void afterLogout()
    {
        if (TxManager.isNoTransaction())
            TxManager.joinTransaction(getEntityManager());
        String currentUserName = (String) getSessionContext().get(Constants.CURRENT_USER_NAME);
        revokeOtherTokens(currentUserName);
        // CacheManager.removeAllSeamkaRegion();
    }

    @Transactional
    public void revokeOtherTokens(String userName)
    {
        if (TxManager.isNoTransaction())
            TxManager.joinTransaction(getEntityManager());
        Date lastUsed = DateUtil.addDate(new Date(), Calendar.MINUTE, -LoginActivity.TTL);
        getEntityManager().createNamedQuery(LoginActivity.UPDATE_LOGOUT_ALL_SAME_LOGEDIN_USERS).setParameter(1, userName)
                .setParameter(2, lastUsed).executeUpdate();
        getEntityManager().flush();
    }

    public LoginActivity findLastActiveLogin(String username)
    {
        try
        {
            LoginActivity la = (LoginActivity) getEntityManager().createNamedQuery(LoginActivity.FIND_LOGIN_ACTIVITY_BY_USER_NAME)
                    .setParameter(1, username).setMaxResults(1).getSingleResult();
            return la;
        }
        catch (NoResultException e)
        {
        }
        return null;
    }

    @Transactional
    public void checkToken()
    {
        getLog().info("checkToken");
        String token = (String) Contexts.getSessionContext().get(LoginActivity.TOKEN);
        if (StringUtil.isNotEmpty(token))
        {
            try
            {

                LoginActivity la = (LoginActivity) getEntityManager()
                        .createQuery("select la from LoginActivity la where la.token = :token").setParameter("token", token)
                        .getSingleResult();
                if (isRequestFromSameOrigin(la) == false)
                {
                    Identity.instance().logout();
                    getSessionContext().remove(LoginActivity.TOKEN);
                }
                else
                {
                    TxManager.beginTransaction();
                    TxManager.joinTransaction(getEntityManager());
                    updateLastUsedToken(token);
                    // TxManager.commitTransaction();
                }
            }
            catch (NoResultException e)
            {
                Identity.instance().logout();
            }

        }
        else
        {
            Identity.instance().logout();
        }
    }

    private void updateLastUsedToken(String token)
    {
        Query query = getEntityManager().createNamedQuery(LoginActivity.UPDATE_LAST_USED);
        query.setParameter(1, new Date()).setParameter(2, token);
        query.executeUpdate();
        getEntityManager().flush();
    }

    private boolean isRequestFromSameOrigin(LoginActivity la)
    {
        return la.getIpAddress().equals(WebContext.instance().getClientIP())
                && la.getDevice().equals(WebContext.instance().getClientUserAgent());
    }
    
    
    public String getCurrentToken()
    {
        return (String) Contexts.getSessionContext().get(LoginActivity.TOKEN);
    }
    
    public String getCurrentUsername()
    {
        return (String) Contexts.getSessionContext().get(Constants.CURRENT_USER_NAME);
    }

}
