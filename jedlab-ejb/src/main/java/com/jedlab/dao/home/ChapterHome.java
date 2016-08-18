package com.jedlab.dao.home;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.codec.binary.Base64;
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
import com.jedlab.model.Member;
import com.jedlab.model.MemberCourse;
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
            try
            {
                Long cntSeq = (Long) getEntityManager().createQuery("select count(c) from Chapter c where c.course.id = :courseId")
                        .setParameter("courseId", getCourse().getId()).getSingleResult();
                getInstance().setSequence((int) (cntSeq * 10));
            }
            catch (NoResultException e)
            {
                getInstance().setSequence(1);
            }
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
        //
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
    @Transactional
    public String persist()
    {
        wire();
        String persist = super.persist();
        List<Member> members = getEntityManager().createQuery("select mc.member from MemberCourse mc where mc.course.id = :courseId")
                .setParameter("courseId", getCourse().getId()).getResultList();
        for (Member mem : members)
        {
            MemberCourse mc = new MemberCourse();
            mc.setCourse(getCourse());
            mc.setChapter(getInstance());
            mc.setMember(mem);
            getEntityManager().persist(mc);
            getEntityManager().flush();
        }
        return persist;
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
     * 
     * @throws UnsupportedEncodingException
     */
    @Transactional
    public void generateVideoToken() throws UnsupportedEncodingException
    {
        Long courseId = Long.parseLong(WebUtil.getParameterValue("courseId"));
        Long chapterId = Long.parseLong(WebUtil.getParameterValue("chapterId"));
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        try
        {
            Chapter c = (Chapter) getEntityManager()
                    .createQuery(
                            "select c from Chapter c where c.course.id = :courseId AND c.id = (select mc.chapter.id from MemberCourse mc where mc.chapter.id = :chapterId AND mc.member.id = :memId)  ")
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
                //
                getEntityManager()
                        .createQuery(
                                "update MemberCourse mc set mc.viewed = true where mc.course.id = :courseId AND mc.member.id = :memId AND mc.chapter.id = :chapterId")
                        .setParameter("courseId", courseId).setParameter("memId", uid).setParameter("chapterId", chapterId).executeUpdate();
                //
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

}
