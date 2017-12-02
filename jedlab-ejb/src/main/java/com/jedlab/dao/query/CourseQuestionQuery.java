package com.jedlab.dao.query;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Course;
import com.jedlab.model.CourseQuestion;

@Name("courseQuestionQuery")
@Scope(ScopeType.CONVERSATION)
public class CourseQuestionQuery extends PagingEntityQuery<CourseQuestion>
{

    public static final String EJBQL = "select cq from CourseQuestion cq left join fetch cq.course c";

    private static final String[] RESTRICTIONS = { "c.id = #{courseQuestionQuery.course.id}","cq.id = #{courseQuestionQuery.question.id}",
             };

    public CourseQuestionQuery()
    {
        setEjbql(EJBQL);
        setMaxResults(9);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }
    
    
    private Course course = new Course();

    public Course getCourse()
    {
        return course;
    }
    
    private CourseQuestion question = new CourseQuestion();

    public CourseQuestion getQuestion()
    {
        return question;
    }
    
    
    
    

}
