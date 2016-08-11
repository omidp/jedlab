package com.jedlab.dao.home;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;
import org.jboss.seam.util.RandomStringUtils;

import com.jedlab.action.Constants;
import com.jedlab.framework.DateUtil;
import com.jedlab.framework.PageExceptionHandler;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.WebUtil;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;
import com.jedlab.model.VideoToken;

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
        if (isIdDefined())
        {
            this.duration = getInstance().getDurationWithformat();
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

    // ////////////////////////////video.seam

    private String token;

    /**
     * one time token for video link
     */
    @Transactional
    public void generateVideoToken()
    {
        Long courseId = Long.parseLong(WebUtil.getParameterValue("courseId"));
        Long chapterId = Long.parseLong(WebUtil.getParameterValue("chapterId"));
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        try
        {
            Chapter c = (Chapter) getEntityManager()
                    .createQuery("select c from Chapter c where c.course.id = :courseId AND c.id = (select mc.chapter.id from MemberCourse mc where mc.chapter.id = :chapterId AND mc.member.id = :memId)  ")
                    .setParameter("courseId", courseId).setParameter("chapterId", chapterId).setParameter("memId", uid).getSingleResult();
            if (c != null)
            {
                VideoToken vt = new VideoToken();                
                vt.setChapter(c);
                vt.setCourseId(chapterId);
                String t = RandomStringUtils.randomAlphanumeric(25);
                vt.setToken(t);
                vt.setMemberId(uid);
                getEntityManager().persist(vt);
                getEntityManager().flush();
                this.token = t;
            }
        }
        catch (NoResultException e)
        {
            throw new PageExceptionHandler("ooops");
        }
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

}
