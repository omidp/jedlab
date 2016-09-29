package com.jedlab.dao.home;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;
import org.jboss.seam.util.RandomStringUtils;

import com.jedlab.action.Constants;
import com.jedlab.framework.DateUtil;
import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.framework.PageExceptionHandler;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TxManager;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;
import com.jedlab.model.Member;
import com.jedlab.model.MemberCourse;
import com.jedlab.model.VideoToken;

@Name("chapterHome")
@Scope(ScopeType.CONVERSATION)
public class ChapterHome extends EntityHome<Chapter>
{

    private String duration;

    private Course course;

    @In
    Session hibernateSession;

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
        if (getCourse().getId() != null)
        {
            Criteria criteria = hibernateSession.createCriteria(Course.class, "c");
            criteria.createCriteria("c.chapters", "chap", Criteria.LEFT_JOIN);
            criteria.add(Restrictions.idEq(getCourse().getId()));
            // course = getEntityManager().find(Course.class,
            // getCourse().getId());
            course = (Course) criteria.uniqueResult();
        }
        if (isIdDefined() && course == null)
        {
            try
            {
                course = (Course) getEntityManager().createQuery("select chap.course from Chapter chap where chap.id = :chapId")
                        .setParameter("chapId", getChapterId()).setMaxResults(1).getSingleResult();
            }
            catch (NoResultException e)
            {
            }
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
                e.printStackTrace();
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
        Session sess = (Session) Component.getInstance("hibernateSession");
        List<BigInteger> memberIds = sess
                .createSQLQuery("select mc.member_id from member_course mc where mc.course_id = :courseId group by member_id")
                .setParameter("courseId", getCourse().getId()).list();
        for (BigInteger memId : memberIds)
        {
            MemberCourse mc = new MemberCourse();
            mc.setCourse(getCourse());
            mc.setChapter(getInstance());
            //
            Member mem = new Member();
            mem.setId(memId.longValue());
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

    public String getToken()
    {
        if (token == null)
        {
            TxManager.beginTransaction();
            TxManager.joinTransaction(getEntityManager());
            Long courseId = getCourse().getId();
            Long chapterId = getChapterId();
            Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
            if(uid == null)
                throw new ErrorPageExceptionHandler("user not found");
            try
            {
                Chapter c = (Chapter) getEntityManager()
                        .createQuery(
                                "select c from Chapter c where c.course.id = :courseId AND c.id IN (select mc.chapter.id from MemberCourse mc where mc.chapter.id = :chapterId AND mc.member.id = :memId)  ")
                        .setParameter("courseId", courseId).setParameter("chapterId", chapterId).setParameter("memId", uid)
                        .getSingleResult();
                if (c != null)
                {
                    VideoToken vt = new VideoToken();
                    vt.setChapter(c);
                    vt.setCourseId(courseId);
                    String t = RandomStringUtils.randomAlphanumeric(25);
                    vt.setToken(t);
                    vt.setMemberId(uid);
                    getEntityManager().persist(vt);
                    //
                    getEntityManager()
                            .createQuery(
                                    "update MemberCourse mc set mc.viewed = true where mc.course.id = :courseId AND mc.member.id = :memId AND mc.chapter.id = :chapterId")
                            .setParameter("courseId", courseId).setParameter("memId", uid).setParameter("chapterId", chapterId)
                            .executeUpdate();
                    //
                    getEntityManager().flush();
                    token = t;

                }
            }
            catch (NoResultException e)
            {
                throw new ErrorPageExceptionHandler("course not found");
            }
        }
        return token;
    }

}
