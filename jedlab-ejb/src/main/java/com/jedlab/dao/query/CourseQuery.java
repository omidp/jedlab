package com.jedlab.dao.query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Course;

@Name("courseQuery")
@Scope(ScopeType.CONVERSATION)
public class CourseQuery extends PagingEntityQuery<Course>
{

    
    
    
}
