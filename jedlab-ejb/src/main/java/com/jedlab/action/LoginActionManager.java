package com.jedlab.action;

import java.util.Calendar;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.framework.EntityController;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import com.jedlab.framework.CacheManager;
import com.jedlab.framework.CookieUtil;
import com.jedlab.framework.DateUtil;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TxManager;
import com.jedlab.framework.WebContext;
import com.jedlab.model.LoginActivity;
import com.jedlab.model.Member;

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
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        String currentUserName = (String) getSessionContext().get(Constants.CURRENT_USER_NAME);
        revokeOtherTokens(currentUserName);
        // CacheManager.removeAllSeamkaRegion();
    }
    
    @Observer(value = Identity.EVENT_LOGIN_SUCCESSFUL)
    @Transactional
    public void afterLogin()
    {
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        Credentials credentials = Identity.instance().getCredentials();
        String uname = credentials.getUsername();       
        Member m = (Member) getEntityManager()
                .createQuery("select m from Member m where lower(m.username) = lower(:uname) or lower(m.email) = lower(:email)")
                .setParameter("uname", credentials.getUsername()).setParameter("email", credentials.getUsername()).setMaxResults(1)
                .getSingleResult();
        if (Member.INSTRUCTOR_DISC.equals(m.getDiscriminator()))
        {
            Identity.instance().addRole(Constants.ROLE_INSTRUCTOR);
        }
        if (Member.STUDENT_DISC.equals(m.getDiscriminator()))
        {
            Identity.instance().addRole(Constants.ROLE_STUDENT);
        }
        LoginActivity la = addActivity(m.getUsername());
        Contexts.getSessionContext().set(Constants.CURRENT_USER_ID, m.getId());
        Contexts.getSessionContext().set(Constants.CURRENT_USER_NAME, m.getUsername());
        Contexts.getSessionContext().set(LoginActivity.TOKEN, la.getToken());
        CacheManager.put(Constants.CURRENT_USER, m);
        //
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        Cookie cookie = CookieUtil.findCookieByName(req, "captchaRequired");
        if (cookie != null)
            CookieUtil.removeCookie(response, cookie);
    }

    @Transactional
    public void revokeOtherTokens(String userName)
    {
        TxManager.beginTransaction();
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

    public Long getCurrentUserId()
    {
        return (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
    }

}
