package com.jedlab.dao.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityHome;

import com.jedlab.model.Course;

@Name("courseHome")
@Scope(ScopeType.CONVERSATION)
public class CourseHome extends EntityHome<Course>
{

    
    
    
}
