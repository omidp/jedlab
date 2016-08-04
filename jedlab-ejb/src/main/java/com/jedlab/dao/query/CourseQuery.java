package com.jedlab.dao.query;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Course;

@Name("courseQuery")
@Scope(ScopeType.CONVERSATION)
public class CourseQuery extends PagingEntityQuery<Course>
{

    private static final String EJBQL = "select c from Course c LEFT OUTER JOIN c.chapters chapters";
    
    private static final String[] RESTRICTIONS = { "lower(c.name) like lower(concat('%',concat(#{courseQuery.course.name},'%')))",
        "c.id = #{courseQuery.course.id}"
        };
    
    Course course = new Course();

    public CourseQuery()
    {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
//        setOrderColumn("memoType");
        setMaxResults(15);
    }

    public Course getCourse()
    {
        return course;
    }
    
    
    
    
    
    
    
}
