package com.jedlab.action;

import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.captcha.Captcha;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.PasswordHash;

import com.jedlab.framework.CacheManager;
import com.jedlab.framework.CookieUtil;
import com.jedlab.framework.CryptoUtil;
import com.jedlab.model.LoginActivity;
import com.jedlab.model.Member;

@Stateless
@Name("authenticator")
public class AuthenticatorBean implements Authenticator
{
    @Logger
    private Log log;

    @In
    Identity identity;

    @In
    Credentials credentials;

    @In
    EntityManager entityManager;

    @In(create = true)
    LoginActionManager loginActionManager;

    public boolean isCaptchaRequired()
    {
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return CookieUtil.findCookieByName(req, "captchaRequired") != null;
    }

    public boolean authenticate()
    {
        log.info("authenticating {0}", credentials.getUsername());
        try
        {
            if (isCaptchaRequired())
            {
                Captcha captcha = Captcha.instance();
                if (captcha.validateResponse(captcha.getResponse()) == false)
                {
                    StatusMessages.instance().addFromResourceBundle(Severity.ERROR, "Captcha_Incorrect");
                    return false;
                }
            }
            Member m = (Member) entityManager.createQuery("select m from Member m where lower(m.username) = lower(:uname) or lower(m.email) = lower(:email)")
                    .setParameter("uname", credentials.getUsername()).setParameter("email", credentials.getUsername()).setMaxResults(1)
                    .getSingleResult();
            if (m.isActive() == false)
            {
                StatusMessages.instance().addFromResourceBundle(Severity.ERROR, "Deactive_User");
                return false;
            }
            if ("admin".equals(credentials.getUsername()))
            {
                identity.addRole(Constants.ROLE_ADMIN);
            }
            String pass = CryptoUtil.decodeBase64(credentials.getPassword());
            String passwordKey = PasswordHash.instance().generateSaltedHash(pass, m.getUsername(), "md5");
            if (passwordKey.equals(m.getPassword()))
            {
                LoginActivity la = loginActionManager.addActivity(m.getUsername());
                Contexts.getSessionContext().set(Constants.CURRENT_USER_ID, m.getId());
                Contexts.getSessionContext().set(Constants.CURRENT_USER_NAME, m.getUsername());
                Contexts.getSessionContext().set(LoginActivity.TOKEN, la.getToken());
                CacheManager.put(Constants.CURRENT_USER, m);
                //
                HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                Cookie cookie = CookieUtil.findCookieByName(req, "captchaRequired");
                if(cookie != null)
                    CookieUtil.removeCookie(response, cookie);
                //
                return true;
            }
            // check token
            // comes from filter
            // LoginActivity la =
            // loginActionManager.findLastActiveLogin(credentials.getUsername());
            // if(la != null)
            // {
            // if(credentials.getPassword().equals(la.getToken()))
            // {
            // Contexts.getSessionContext().set(Constants.CURRENT_USER_ID,
            // m.getId());
            // Contexts.getSessionContext().set(Constants.CURRENT_USER_NAME,
            // m.getUsername());
            // return true;
            // }
            // }
        }
        catch (NoResultException e)
        {
        }
        addCaptchaCookie();
        return false;
    }

    private void addCaptchaCookie()
    {
        Cookie cookie = new Cookie("captchaRequired", "true");
        cookie.setMaxAge(60 * 60 * 10); // 10 hours
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        CookieUtil.addCookie(cookie, response);
    }

}
