package com.jedlab.dao.query;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;

import com.jedlab.JedLab;
import com.jedlab.action.Constants;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.PagingController;
import com.jedlab.framework.StringUtil;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;
import com.jedlab.model.MemberCourse;
import com.jedlab.model.MemberQuestion;
import com.jedlab.model.Question;

@Name("courseQuery")
@Scope(ScopeType.CONVERSATION)
public class CourseQuery extends PagingController<Course>
{

    private List<Course> resultList;

    private Long resultCount;

    Course course = new Course();

    public CourseQuery()
    {
        setMaxResults(9);
        addToExpressions("#{courseQuery.course.name}");
        addToExpressions("#{courseQuery.course.id}");
        addToExpressions("#{courseQuery.course.level}");
    }

    @Override
    public List<Course> getResultList()
    {
        if (isAnyParameterDirty())
        {
            refresh();
        }
        if (resultList != null)
            return truncResultList(resultList);
        Criteria criteria = getSession().createCriteria(Course.class, "c");
        applyFilter(criteria);
        addToValueParams("#{courseQuery.course.name}");
        addToValueParams("#{courseQuery.course.id}");
        addToValueParams("#{courseQuery.course.level}");
        if (getFirstResult() != null)
            criteria.setFirstResult(getFirstResult());
        if (getMaxResults() != null)
            criteria.setMaxResults(getMaxResults() + 1);
        criteria.addOrder(Order.desc("sticky")).addOrder(Order.desc("createdDate"));
        resultList = criteria.list();
        addChpterCount(resultList);
        addRegisteredUserCount(resultList);
        return truncResultList(resultList);
    }

    private void addRegisteredUserCount(List<Course> courseList)
    {
        if(CollectionUtil.isNotEmpty(courseList))
        {
            Criteria criteria = getSession().createCriteria(MemberCourse.class, "mc");        
            criteria.setProjection(Projections.projectionList().add(Projections.countDistinct("member"))
                    .add(Projections.groupProperty("course")));
//            criteria.add(Restrictions.in("course", courseList));
            List<Object[]> obj = criteria.list();
            for (Object[] items : obj)
            {
                Long userCount = Long.parseLong(String.valueOf(items[0]));
                Course q = (Course) items[1];
                for (Course item : courseList)
                {
                    if(q.getId().longValue() == item.getId().longValue())
                    {
                        item.setRegisteredUserCount(userCount == null ? new Long(0) : userCount.longValue());
                    }
                }
            }
        }
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

    private void applyFilter(Criteria criteria)
    {
        if(Identity.instance().hasRole(Constants.ROLE_STUDENT))
            criteria.add(Restrictions.eq("active", true));
        if(Identity.instance().hasRole(Constants.ROLE_INSTRUCTOR))
        {
            criteria.add(Restrictions.eq("instructor.id", JedLab.instance().getCurrentUserId()));
        }
        if(StringUtil.isNotEmpty(getCourse().getName()))
            criteria.add(Restrictions.ilike("name", getCourse().getName(), MatchMode.ANYWHERE));
        if(getCourse().getLevel() != null)
            criteria.add(Restrictions.eq("level", getCourse().getLevel()));
    }

    private void refresh()
    {
        resultCount = null;
        resultList = null;
    }

    @Override
    public Long getResultCount()
    {
        if (isAnyParameterDirty())
        {
            refresh();
        }
        if (resultCount != null)
            return resultCount;
        Criteria criteria = getSession().createCriteria(Course.class, "c");
        applyFilter(criteria);
        criteria.setProjection(Projections.rowCount());
        resultCount = (Long) criteria.uniqueResult();
        return resultCount;
    }

    @Override
    public boolean isNextExists()
    {
        return resultList != null && getMaxResults() != null && resultList.size() > getMaxResults();
    }

    public Course getCourse()
    {
        return course;
    }

}
