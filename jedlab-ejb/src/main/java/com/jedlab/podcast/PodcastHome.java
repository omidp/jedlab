package com.jedlab.podcast;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;

import com.jedlab.JedLab;
import com.jedlab.framework.TxManager;
import com.jedlab.framework.ValidationUtil;
import com.jedlab.model.Member;
import com.jedlab.model.Podcast;

@Name("podcastHome")
@Scope(ScopeType.CONVERSATION)
public class PodcastHome extends EntityHome<Podcast>
{

    @In
    Session hibernateSession;
    
    private Set<ConstraintViolation<Object>> invalidValues= new HashSet<ConstraintViolation<Object>>();


    private byte[] uploadImage;

    private Integer fileSize;

    private String uuid;

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public byte[] getUploadImage()
    {
        return uploadImage;
    }

    public void setUploadImage(byte[] uploadImage)
    {
        this.uploadImage = uploadImage;
    }

    public Integer getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Integer fileSize)
    {
        this.fileSize = fileSize;
    }


    public Long getPodcastId()
    {
        return (Long) getId();
    }

    public void setPodcastId(Long podcastId)
    {
        setId(podcastId);
    }


    @Transactional
    public void updateViewCount()
    {
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        getEntityManager().createQuery("update Story s set s.viewCount = (s.viewCount+1)  where s.uuid = :uuid").setParameter("uuid", getUuid()).executeUpdate();        
    }
    
    public void load()
    {
        getInstance();
    }

    private void wire()
    {
        invalidValues.clear();
        Member currentUser = JedLab.instance().getCurrentUser();
        getInstance().setMember(currentUser);
        if (getFileSize() != null && getFileSize() > 107371)
        {
            invalidValues.add(ValidationUtil.addInvalidValue("image", Podcast.class,
                    StatusMessage.getBundleMessage("File_Size_Exceed",
                            "")));
        }
        else
        {
            if(getUploadImage() != null && getFileSize() != null)
                getInstance().setImage(getUploadImage());
        }
    }

    @Override
    public String persist()
    {
        wire();
        if (invalidValues.size() > 0)
        {
            getStatusMessages().add(invalidValues);
            return null;
        }
        return super.persist();
    }

    @Override
    public String update()
    {
        wire();
        if (invalidValues.size() > 0)
        {
            getStatusMessages().add(invalidValues);
            return null;
        }
        return super.update();
    }


}
