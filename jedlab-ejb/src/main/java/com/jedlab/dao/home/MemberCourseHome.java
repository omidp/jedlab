package com.jedlab.dao.home;

import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.HibernateEntityController;
import org.jboss.seam.persistence.PersistenceContexts;

import com.jedlab.action.Constants;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.WebUtil;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;
import com.jedlab.model.Member;
import com.jedlab.model.MemberCourse;

@Name("memberCourseHome")
@Scope(ScopeType.CONVERSATION)
public class MemberCourseHome extends HibernateEntityController
{

    @RequestParameter
    private Long courseId;

    private Boolean memberRegisteredInCourse;

    public Boolean getMemberRegisteredInCourse()
    {
        if (memberRegisteredInCourse != null)
            return memberRegisteredInCourse;
        if (courseId == null)
            courseId = Long.parseLong(WebUtil.getParameterValue("courseId"));
        Long uid = (Long) getSessionContext().get(Constants.CURRENT_USER_ID);
        if(uid == null)
            return false;
        Criteria criteria = getSession().createCriteria(MemberCourse.class, "mc");
        criteria.add(Restrictions.eq("mc.member.id", uid));
        criteria.add(Restrictions.eq("mc.course.id", courseId));
        criteria.setMaxResults(1);
        MemberCourse mc = (MemberCourse) criteria.uniqueResult();
        if (mc != null)
            memberRegisteredInCourse = true;
        else
            memberRegisteredInCourse = false;
        return memberRegisteredInCourse;
    }

    public String register()
    {
        PersistenceContexts.instance().changeFlushMode(FlushModeType.MANUAL);
        Long uid = (Long) getSessionContext().get(Constants.CURRENT_USER_ID);
        Criteria criteria = getSession().createCriteria(Course.class, "c");
        criteria.createCriteria("c.chapters", Criteria.LEFT_JOIN);
        criteria.add(Restrictions.idEq(courseId));
        Course c = (Course) criteria.uniqueResult();
        Set<Chapter> chapters = c.getChapters();
        if (CollectionUtil.isNotEmpty(chapters))
        {
            for (Chapter chapter : chapters)
            {
                MemberCourse mc = new MemberCourse();
                mc.setChapter(chapter);
                mc.setCourse(c);
                //
                Member m = new Member();
                m.setId(uid);
                mc.setMember(m);
                getSession().save(mc);
            }
        }
        getSession().flush();
        return "registered";
    }

}
