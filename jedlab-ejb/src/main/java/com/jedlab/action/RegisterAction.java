package com.jedlab.action;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.PasswordHash;

import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.framework.PageExceptionHandler;
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
                throw new ErrorPageExceptionHandler(StatusMessage.getBundleMessage("No_User_Found",""));
        }
        catch (NoResultException e)
        {
            throw new ErrorPageExceptionHandler(StatusMessage.getBundleMessage("No_User_Found",""));
        }

    }

    @Transactional
    public String confirm()
    {
        String tempPasswd = getInstance().getPassword();
        if (getConfirmPasswd().equals(tempPasswd) == false)
        {
            StatusMessages.instance().addFromResourceBundle("Password_Does_Not_Match");
            return null;
        }
        String passwordKey = PasswordHash.instance().generateSaltedHash(tempPasswd, getInstance().getUsername(), "md5");
        getInstance().setPassword(passwordKey);
        getInstance().setActivationCode(null);
        getInstance().setActive(Boolean.TRUE);
        entityManager.flush();
        // renderer.render("/mailTemplates/thankyou.xhtml");
        Identity identity = Identity.instance();
        identity.getCredentials().setUsername(getInstance().getUsername());
        identity.getCredentials().setPassword(tempPasswd);
        identity.login();
        return "confirmed";
    }

    @Transactional
    public String register()
    {

        try
        {
            user.setActive(Boolean.FALSE);
            user.setActivationCode(RandomStringUtils.randomAlphanumeric(30));
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
                Events.instance().raiseAsynchronousEvent("sendMail", user, activationLink);
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
    
    @Observer("sendMail")
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
            Member u = (Member) entityManager.createQuery("select u from User u where u.email = :email")
                    .setParameter("email", getInstance().getEmail()).setMaxResults(1).getSingleResult();
            if (u.getActivationCode() == null || u.getActivationCode().isEmpty())
                throw new PageExceptionHandler("user already activated");
            user.setEmail(u.getEmail());
            user.setActivationCode(u.getActivationCode());
            // renderer.render("/mailTemplates/register.xhtml");
            return "recoveredLink";
        }
        catch (NoResultException e)
        {
            throw new PageExceptionHandler("user does not exists");
        }
    }

    @Transactional
    public String recoverPassword()
    {
        try
        {
            Member u = (Member) entityManager.createQuery("select u from User u where u.email = :email")
                    .setParameter("email", getInstance().getEmail()).setMaxResults(1).getSingleResult();
            if (u.isActive() == false)
                throw new PageExceptionHandler("user is no activated");
            u.setRecoverPasswordCode(RandomStringUtils.randomAlphanumeric(25));
            entityManager.flush();
            // renderer.render("/mailTemplates/resetPassword.xhtml");

        }
        catch (NoResultException e)
        {
        }
        return "recoveredPassword";
    }

}
