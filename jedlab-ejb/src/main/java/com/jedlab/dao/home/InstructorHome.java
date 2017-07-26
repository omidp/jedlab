package com.jedlab.dao.home;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.persistence.EntityManager;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.IOUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.HibernateEntityController;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;

import com.jedlab.action.Constants;
import com.jedlab.framework.CacheManager;
import com.jedlab.framework.CryptoUtil;
import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TxManager;
import com.jedlab.model.Instructor;
import com.jedlab.model.Member;

@Name("instructorHome")
@Scope(ScopeType.CONVERSATION)
public class InstructorHome extends HibernateEntityController
{

    @Logger
    Log logger;
    
    @In
    EntityManager entityManager;

    private Member user;

    private Instructor instructor;

    private byte[] uploadImage;

    private Integer fileSize;
    
    private String encodedUsername;
    
    

    public String getEncodedUsername()
    {
        return encodedUsername;
    }

    public void setEncodedUsername(String encodedUsername)
    {
        this.encodedUsername = encodedUsername;
    }

    public void load()
    {
        instructor = (Instructor) getSession().get(Instructor.class,
                Long.parseLong(String.valueOf(getSessionContext().get(Constants.CURRENT_USER_ID))));
        user = instructor;
        if(instructor == null)
            throw new ErrorPageExceptionHandler("instructor can not be found");
        if(StringUtil.isNotEmpty(getEncodedUsername()))
        {
            String uname = CryptoUtil.decodeBase64(getEncodedUsername());
            if(instructor.getUsername().equals(uname))
            {
                TxManager.beginTransaction();
                TxManager.joinTransaction(entityManager);
                entityManager.createQuery("update Instructor i set i.approved = true where i.id = :id").setParameter("id", instructor.getId()).executeUpdate();
            }
        }
    }

    public byte[] getUploadImage()
    {
        return uploadImage;
    }

    public void setUploadImage(byte[] uploadImage)
    {
        this.uploadImage = uploadImage;
    }

    

    public Instructor getInstructor()
    {
        return instructor;
    }



    byte[] userThumbnailImage;

    public byte[] getUserThumbnailImage()
    {
        if (userThumbnailImage != null)
            return userThumbnailImage;
        byte[] image = getInstructor().getImage();
        if (image == null)
            return null;
        ByteArrayInputStream bis = new ByteArrayInputStream(image);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            Thumbnails.of(bis).size(446, 446).toOutputStream(bos);
            userThumbnailImage = bos.toByteArray();
        }
        catch (IOException e)
        {
        }
        finally
        {
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(bos);
        }
        logger.info("{0} image has been thumbnail", user.getUsername());
        return userThumbnailImage;
    }

    public Integer getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Integer fileSize)
    {
        this.fileSize = fileSize;
    }

   

    public Member getUser()
    {
        if (user == null)
            throw new UnsupportedOperationException("unable to find user");
        return user;
    }

    public String update()
    {
        // 107KB
        if (getFileSize() != null && getFileSize() > 107371)
        {
            getStatusMessages().addFromResourceBundle(Severity.ERROR, "File_Size_Exceed");
            return null;
        }
        else
        {
            if(getUploadImage() != null && getFileSize() != null)
                instructor.setImage(getUploadImage());
        }
        getSession().flush();
        getSession().clear();
        CacheManager.remove(Constants.CURRENT_USER);
        StatusMessages.instance().addFromResourceBundle(Severity.INFO, "User_Updated");
        return "updated";
    }

    

   

}
