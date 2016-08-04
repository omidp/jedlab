package com.jedlab.dao.query;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

import com.jedlab.action.Constants;
import com.jedlab.framework.PagingController;
import com.jedlab.model.MemberCourse;

@Name("memberCourseQuery")
@Scope(ScopeType.CONVERSATION)
public class MemberCourseQuery extends PagingController<MemberCourse>
{

    private List<MemberCourse> resultList;

    private Long resultCount;

    public MemberCourseQuery()
    {
        setMaxResults(15);
    }

    @Override
    public List<MemberCourse> getResultList()
    {
        if (resultList != null)
            return resultList;
        Criteria criteria = createCriteria();
        if (getFirstResult() != null)
            criteria.setFirstResult(getFirstResult());
        if (getMaxResults() != null)
            criteria.setMaxResults(getMaxResults() + 1);
        resultList = criteria.list();
        return truncResultList(resultList);
    }

    private Criteria createCriteria()
    {
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        Criteria criteria = getSession().createCriteria(MemberCourse.class, "mc");
        criteria.createCriteria("mc.course", "c", Criteria.LEFT_JOIN);
        criteria.createCriteria("c.chapters", "chap", Criteria.LEFT_JOIN);
        criteria.add(Restrictions.eq("mc.member.id", uid));
        //
        DetachedCriteria dc = DetachedCriteria.forClass(MemberCourse.class, "memc");
        dc.setProjection(Projections.distinct(Projections.property("course")));
        dc.add(Restrictions.eq("memc.member.id", uid));
        //
        criteria.add(Subqueries.propertyIn("mc.course.id", dc));
        return criteria;
    }

    @Override
    public Long getResultCount()
    {
        if (resultCount != null)
            return resultCount;
        Criteria criteria = createCriteria();
        criteria.setProjection(Projections.rowCount());
        resultCount = (Long) criteria.uniqueResult();
        return resultCount;
    }

    @Override
    public boolean isNextExists()
    {
        return resultList != null && getMaxResults() != null && resultList.size() > getMaxResults();
    }

}
