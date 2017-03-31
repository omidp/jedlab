package com.jedlab.dao.home;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.IOUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.HibernateEntityController;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;

import com.jedlab.JedLab;
import com.jedlab.action.Constants;
import com.jedlab.framework.CacheManager;
import com.jedlab.framework.ReflectionUtil;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.WebUtil;
import com.jedlab.model.Instructor;
import com.jedlab.model.Member;
import com.jedlab.model.Student;

@Name("instructorHome")
@Scope(ScopeType.CONVERSATION)
public class InstructorHome extends HibernateEntityController
{

    @Logger
    Log logger;

    private Member user;

    private Instructor instructor;

    private byte[] uploadImage;

    private Integer fileSize;

    public void load()
    {
        instructor = (Instructor) getSession().get(Instructor.class,
                Long.parseLong(String.valueOf(getSessionContext().get(Constants.CURRENT_USER_ID))));
        user = instructor;
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
