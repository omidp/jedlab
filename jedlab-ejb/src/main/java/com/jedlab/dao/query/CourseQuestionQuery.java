package com.jedlab.dao.query;

import java.util.Arrays;
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
import org.jboss.seam.security.Identity;

import com.jedlab.action.Constants;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.PagingController;
import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;
import com.jedlab.model.CourseQuestion;
import com.jedlab.model.MemberQuestion;
import com.jedlab.model.Question;

@Name("courseQuestionQuery")
@Scope(ScopeType.CONVERSATION)
public class CourseQuestionQuery extends PagingEntityQuery<CourseQuestion>
{


    public static final String EJBQL = "select cq from CourseQuestion cq left join fetch cq.course c";
    
    public CourseQuestionQuery()
    {
        setEjbql(EJBQL);
        setMaxResults(9);
    }

    

}
