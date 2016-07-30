package com.jedlab.action;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.management.PasswordHash;
import org.jboss.seam.util.Base64;

import com.jedlab.JedLab;
import com.jedlab.model.Member;

@Name("changePasswordAction")
@Scope(ScopeType.EVENT)
public class ChangePasswordAction implements Serializable
{

    @In(create=true)
    JedLab jedLab;
    
    @In
    EntityManager entityManager;
    
    private String currentPassword;
    private String password;
    private String confirmPassword;

    public String getCurrentPassword()
    {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword)
    {
        this.currentPassword = currentPassword;
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

    @Transactional
    public String changePassword() throws UnsupportedEncodingException
    {
        String currentPass = new String(Base64.decode(getCurrentPassword()), "UTF-8");
        String pass = new String(Base64.decode(getPassword()), "UTF-8");
        String confirmPass = new String(Base64.decode(getConfirmPassword()), "UTF-8");
        if(pass == null || pass.equals(confirmPass) == false)
        {
            StatusMessages.instance().addFromResourceBundle(Severity.ERROR,"Confirm_Password_Not_Match");
            return null;
        }
        Member currentUser = jedLab.getCurrentUser();
        String passwordKey = PasswordHash.instance().generateSaltedHash(currentPass, currentUser.getUsername(), "md5");
        if(passwordKey.equals(currentUser.getPassword()) == false)
        {
            StatusMessages.instance().addFromResourceBundle(Severity.ERROR,"Password_Not_Match");
            return null;
        }
        String newPasswordKey = PasswordHash.instance().generateSaltedHash(pass, currentUser.getUsername(), "md5");
        currentUser.setPassword(newPasswordKey);
        entityManager.merge(currentUser);
        entityManager.flush();
        StatusMessages.instance().addFromResourceBundle(Severity.INFO,"User_Updated");
        return "changed";
    }

}
