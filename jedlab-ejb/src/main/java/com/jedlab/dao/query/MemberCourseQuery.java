package com.jedlab.dao.query;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

import com.jedlab.action.Constants;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.PagingController;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;
import com.jedlab.model.MemberCourse;

@Name("memberCourseQuery")
@Scope(ScopeType.CONVERSATION)
public class MemberCourseQuery extends PagingController<Course>
{

    private List<Course> resultList;

    private Long resultCount;

    public MemberCourseQuery()
    {
        setMaxResults(5);
    }

    @Override
    public List<Course> getResultList()
    {
        if (resultList != null)
            return truncResultList(resultList);
        Criteria criteria = createCriteria();
        if (getFirstResult() != null)
            criteria.setFirstResult(getFirstResult());
        if (getMaxResults() != null)
            criteria.setMaxResults(getMaxResults() + 1);
        criteria.addOrder(Order.desc("c.createdDate"));
        resultList = criteria.list();
        addChpterCount(resultList);
        return truncResultList(resultList);
    }

    private Criteria createCriteria()
    {
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        Criteria criteria = getSession().createCriteria(Course.class, "c");
//        criteria.createCriteria("c.chapters", "chap", Criteria.LEFT_JOIN);
        //
        DetachedCriteria dc = DetachedCriteria.forClass(MemberCourse.class, "memc");
        dc.setProjection(Projections.distinct(Projections.property("course.id")));
        dc.add(Restrictions.eq("memc.member.id", uid));
        //
        criteria.add(Subqueries.propertyIn("c.id", dc));
//        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria;
    }
    
    private void addChpterCount(List<Course> courseList)
    {
        if(CollectionUtil.isNotEmpty(courseList))
        {
            Criteria criteria = getSession().createCriteria(Chapter.class, "chapter");        
            criteria.setProjection(Projections.projectionList().add(Projections.count("course"))
                    .add(Projections.groupProperty("course")));
            criteria.add(Restrictions.in("course", courseList));
            List<Object[]> obj = criteria.list();
            for (Object[] items : obj)
            {
                Long chapterCount = Long.parseLong(String.valueOf(items[0]));
                Course course = (Course) items[1];
                for (Course c : courseList)
                {
                    if(course.getId().longValue() == c.getId().longValue())
                    {
                        c.setChapterCount(chapterCount);
                    }
                }
            }
        }
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
    
    ////////////////
    
}
