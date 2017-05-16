package com.jedlab.action;

import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Events;
import org.jboss.seam.framework.EntityController;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.PasswordHash;

import com.jedlab.framework.CookieUtil;
import com.jedlab.framework.CryptoUtil;
import com.jedlab.model.Instructor;
import com.jedlab.validators.InstructorValidator;

@Name("instructorRegisterAction")
@Scope(ScopeType.CONVERSATION)
public class InstructorRegisterAction extends EntityController
{

    Instructor instance = new Instructor();

    private byte[] uploadImage;

    private Integer fileSize;

    public Integer getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Integer fileSize)
    {
        this.fileSize = fileSize;
    }

    public byte[] getUploadImage()
    {
        return uploadImage;
    }

    public void setUploadImage(byte[] uploadImage)
    {
        this.uploadImage = uploadImage;
    }

    public Instructor getInstance()
    {
        return instance;
    }

    @Transactional
    public String register()
    {
        if (getFileSize() != null)
            if (getFileSize() > 0 && getFileSize() < 107371)
                getInstance().setImage(getUploadImage());
        //validation check bu database contraints
        getInstance().setActive(Boolean.TRUE);
        getInstance().setActivationCode(null);
        String passwordKey = PasswordHash.instance().generateSaltedHash(getInstance().getPassword(), getInstance().getUsername(), "md5");
        getInstance().setPassword(passwordKey);
        getEntityManager().persist(getInstance());
        StatusMessages.instance().addFromResourceBundle("Register_Completed");
        Events.instance().raiseAsynchronousEvent(Constants.SEND_THANK_YOU_MAIL, getInstance());
        HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        Cookie c = CookieUtil.findCookieByName(req, "captchaRequired");
        if(c != null)
            CookieUtil.removeCookie(res, c);
        Identity ident = Identity.instance();
        ident.addRole(Constants.ROLE_INSTRUCTOR);
        ident.getCredentials().setUsername(getInstance().getUsername());
        ident.getCredentials().setPassword(CryptoUtil.encodeBase64(getInstance().getPassword()));
        ident.login();
        return "persisted";
    }

}