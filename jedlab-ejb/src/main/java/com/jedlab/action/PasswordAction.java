package com.jedlab.action;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.security.management.PasswordHash;

import com.jedlab.framework.CryptoUtil;
import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.model.Member;

@Name("passwordAction")
@Scope(ScopeType.CONVERSATION)
public class PasswordAction implements Serializable
{

    @In
    EntityManager entityManager;

    Member user;

    private String password;

    private String confirmPassword;

    private String recoverPasswordCode;

    public String getRecoverPasswordCode()
    {
        return recoverPasswordCode;
    }

    public void setRecoverPasswordCode(String recoverPasswordCode)
    {
        this.recoverPasswordCode = recoverPasswordCode;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getConfirmPassword()
    {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword)
    {
        this.confirmPassword = confirmPassword;
    }

    public void load()
    {
        try
        {
            user = (Member) entityManager.createQuery("select u from Member u where u.recoverPasswordCode = :rpc")
                    .setParameter("rpc", getRecoverPasswordCode()).setMaxResults(1).getSingleResult();
            if (user.isActive() == false)
                throw new ErrorPageExceptionHandler("user is not activated");
        }
        catch (NoResultException e)
        {
            throw new ErrorPageExceptionHandler("invalid code");
        }
    }

    public String reset()
    {
        if (getPassword().equals(getConfirmPassword()) == false)
        {
            StatusMessages.instance().addFromResourceBundle(Severity.ERROR,"Password_Does_Not_Match");
            return null;
        }
        String passwd = CryptoUtil.decodeBase64(getPassword());
        String passwordKey = PasswordHash.instance().generateSaltedHash(passwd, user.getUsername(), "md5");
        entityManager.createQuery("update Member u set u.password = :passwd, u.recoverPasswordCode = null where u.id = :userId").setParameter("passwd", passwordKey)
                .setParameter("userId", user.getId()).executeUpdate();
        entityManager.flush();
        entityManager.refresh(user);
        StatusMessages.instance().addFromResourceBundle("Password_Changed");
        return "reset";
    }

}
