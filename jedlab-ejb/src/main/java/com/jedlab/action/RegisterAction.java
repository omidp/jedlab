package com.jedlab.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.faces.FacesManager;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.PasswordHash;

import com.jedlab.framework.CryptoUtil;
import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.framework.PageExceptionHandler;
import com.jedlab.framework.WebContext;
import com.jedlab.model.Member;

@Name("registerAction")
@Scope(ScopeType.CONVERSATION)
public class RegisterAction implements Serializable
{

    public static final int PASSWORD_ITERATION = 1000;

    @In(create = true)
    private Renderer renderer;

    @In
    EntityManager entityManager;

    @In
    Member user;

    private Member instance;

    private String confirmPasswd;

    public String getConfirmPasswd()
    {
        return confirmPasswd;
    }

    public void setConfirmPasswd(String confirmPasswd)
    {
        this.confirmPasswd = confirmPasswd;
    }

    public Member getInstance()
    {
        if (instance == null)
            instance = new Member();
        return instance;
    }

    public void setInstance(Member instance)
    {
        this.instance = instance;
    }

    public void load()
    {
        try
        {
            instance = (Member) entityManager.createQuery("select u from Member u where u.activationCode = :activationCode")
                    .setParameter("activationCode", user.getActivationCode()).setMaxResults(1).getSingleResult();
            if (getInstance().getEmail() == null || getInstance().getActivationCode() == null)
                throw new ErrorPageExceptionHandler(StatusMessage.getBundleMessage("No_User_Found", ""));
        }
        catch (NoResultException e)
        {
            throw new ErrorPageExceptionHandler(StatusMessage.getBundleMessage("No_User_Found", ""));
        }

    }

    @Transactional
    public String confirm()
    {
        EntityManager em = (EntityManager) Component.getInstance("entityManager");
        try
        {
            em.createQuery("select m from Member m where lower(m.username) = lower(:uname)").setParameter("uname", getInstance().getUsername()).setMaxResults(1)
                    .getSingleResult();
            StatusMessages.instance().addFromResourceBundle(Severity.ERROR, "Username_Exists");
            return null;
        }
        catch (NoResultException e)
        {
        }
        String tempPasswd = getInstance().getPassword();
        if (getConfirmPasswd().equals(tempPasswd) == false)
        {
            StatusMessages.instance().addFromResourceBundle(Severity.ERROR, "Password_Does_Not_Match");
            return null;
        }
        tempPasswd = CryptoUtil.decodeBase64(tempPasswd);
        String passwordKey = PasswordHash.instance().generateSaltedHash(tempPasswd, getInstance().getUsername(), "md5");
        getInstance().setPassword(passwordKey);
        getInstance().setActivationCode(null);
        getInstance().setActive(Boolean.TRUE);
        entityManager.flush();
        StatusMessages.instance().addFromResourceBundle("Register_Completed");
        Events.instance().raiseAsynchronousEvent(Constants.SEND_THANK_YOU_MAIL, getInstance());
        Identity identity = Identity.instance();
        identity.getCredentials().setUsername(getInstance().getUsername());
        identity.getCredentials().setPassword(CryptoUtil.encodeBase64(tempPasswd));
        identity.login();
        return "confirmed";
    }

    @Observer(Constants.SEND_THANK_YOU_MAIL)
    public void sendThankYouEmail(Member registeredUser)
    {
        Contexts.getConversationContext().set("user", registeredUser);
        renderer.render("/mailTemplates/thankyou.xhtml");
    }

    @Transactional
    public String register()
    {

        try
        {
            entityManager.createQuery("select m from Member m where lower(m.email) = lower(:email)").setParameter("email", user.getEmail())
                    .setMaxResults(1).getSingleResult();
            StatusMessages.instance().addFromResourceBundle(Severity.ERROR,"Email_Exists");
            return null;
        }
        catch (NoResultException e)
        {
        }
        try
        {
            user.setActive(Boolean.FALSE);
            user.setActivationCode(RandomStringUtils.randomAlphanumeric(45));
            entityManager.persist(user);
            entityManager.flush();
            if (user.getId() != null)
            {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                String viewId = Pages.getCurrentViewId();
                String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext, Pages.getCurrentViewId());
                url = Pages.instance().encodeScheme(viewId, facesContext, url);
                url = url.substring(0, url.lastIndexOf("/") + 1);
                String activationLink = url + "registerConfirmation.seam" + "?ac=" + user.getActivationCode();
                Events.instance().raiseAsynchronousEvent(Constants.SEND_MAIL_REGISTRATION, user, activationLink);
                StatusMessages.instance().addFromResourceBundle("Register_Success", user.getEmail());
            }
            return "registered";
        }
        catch (Exception e)
        {
            throw new PageExceptionHandler(Interpolator.instance().interpolate(StatusMessage.getBundleMessage("Register_Fail", ""),
                    user.getEmail()), Severity.ERROR);
        }
    }

    @Observer(Constants.SEND_MAIL_REGISTRATION)
    public void sendEmail(Member registeredUser, String activationLink)
    {
        Contexts.getConversationContext().set("user", registeredUser);
        Contexts.getConversationContext().set("activationLink", activationLink);
        renderer.render("/mailTemplates/register.xhtml");
    }

    public String recoverLink()
    {
        try
        {
            Member u = (Member) entityManager.createQuery("select u from Member u where lower(u.email) = lower(:email)")
                    .setParameter("email", getInstance().getEmail()).setMaxResults(1).getSingleResult();
            if (u.getActivationCode() == null || u.getActivationCode().isEmpty())
            {
                StatusMessages.instance().addFromResourceBundle(Severity.WARN, "User_Activated");
                return null;
            }
            user.setEmail(u.getEmail());
            user.setActivationCode(u.getActivationCode());
            //
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String viewId = Pages.getCurrentViewId();
            String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext, Pages.getCurrentViewId());
            url = Pages.instance().encodeScheme(viewId, facesContext, url);
            url = url.substring(0, url.lastIndexOf("/") + 1);
            String activationLink = url + "registerConfirmation.seam" + "?ac=" + u.getActivationCode();
            StatusMessages.instance().addFromResourceBundle("Activation_Sent");
            Events.instance().raiseAsynchronousEvent(Constants.SEND_MAIL_REGISTRATION, u, activationLink);
            return "recoveredLink";
        }
        catch (NoResultException e)
        {
            StatusMessages.instance().addFromResourceBundle(Severity.WARN, "Email_Not_Found");
        }
        return null;
    }

    @Transactional
    public String recoverPassword()
    {
        try
        {
            Member u = (Member) entityManager.createQuery("select u from Member u where lower(u.email) = lower(:email)")
                    .setParameter("email", getInstance().getEmail()).setMaxResults(1).getSingleResult();
            if (u.isActive() == false)
            {
                StatusMessages.instance().addFromResourceBundle(Severity.WARN, "User_Is_Not_Active");
                return null;
            }
            String code = RandomStringUtils.randomAlphanumeric(45);
            u.setRecoverPasswordCode(code);
            entityManager.flush();
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext, Pages.getCurrentViewId());
            String viewId = Pages.getCurrentViewId();
            url = Pages.instance().encodeScheme(viewId, facesContext, url);
            url = url.substring(0, url.lastIndexOf("/") + 1);
            String recoverLink = url + "resetPassword.seam" + "?rpc=" + code;
            Events.instance().raiseAsynchronousEvent(Constants.SEND_RESET_PASSWORD_MAIL, u.getUsernameOrEmail(), recoverLink, u.getEmail());
            StatusMessages.instance().addFromResourceBundle(Severity.INFO, "Reset_Pass_Check_Mail");
        }
        catch (NoResultException e)
        {
            StatusMessages.instance().addFromResourceBundle(Severity.WARN, "Email_Not_Found");
            return null;
        }
        return "recoveredPassword";
    }

    @Observer(Constants.SEND_RESET_PASSWORD_MAIL)
    public void sendResetPassword(String username, String recoverPassLink, String email)
    {
        Contexts.getConversationContext().set("username", username);
        Contexts.getConversationContext().set("email", email);
        Contexts.getConversationContext().set("recoverPassLink", recoverPassLink);
        renderer.render("/mailTemplates/resetpassword.xhtml");
    }
    
    public void sendToDashboard()
    {
        if(Identity.instance().isLoggedIn())
        {
            Map<String, Object> parameters = new HashMap<String, Object>();
            FacesManager.instance().redirect("/member/dashboard.xhtml", parameters, false, false);
        }
    }

}
