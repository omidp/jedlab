package com.jedlab.dao.home;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import com.jedlab.framework.DateUtil;
import com.jedlab.framework.StringUtil;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;

@Name("chapterHome")
@Scope(ScopeType.CONVERSATION)
public class ChapterHome extends EntityHome<Chapter>
{

    @Logger
    Log logger;
    
    private String duration;

    private Course course;

    public void setChapterId(Long id)
    {
        setId(id);
    }

    public Long getChapterId()
    {
        return (Long) getId();
    }

    public Course getCourse()
    {
        if (course == null)
            course = new Course();
        return course;
    }

    public String getDuration()
    {
        return duration;
    }

    public void setDuration(String duration)
    {
        this.duration = duration;
    }

    @Override
    protected Chapter createInstance()
    {
        Chapter c = new Chapter();
        return c;
    }

    public void load()
    {
        course = getEntityManager().find(Course.class, getCourse().getId());
        if(isIdDefined())
        {
          this.duration =  getInstance().getDurationWithformat();
        }
    }

    private void wire()
    {
        if (getCourse().getId() != null)
        {
            getInstance().setCourse(getCourse());
        }
        if (StringUtil.isNotEmpty(getDuration()))
        {
            try
            {
                getInstance().setDuration(DateUtil.getDuration(getDuration()));
            }
            catch (ParseException e)
            {
                logger.info("invalid parse hour and minutes {}", e);
            }
        }
    }
    

    public boolean isWired()
    {
        return true;
    }

    public Chapter getDefinedInstance()
    {
        return isIdDefined() ? getInstance() : null;
    }

    @Override
    public String persist()
    {
        wire();
        return super.persist();
    }
    
    @Override
    public String update()
    {
        wire();
        return super.update();
    }
    

}
