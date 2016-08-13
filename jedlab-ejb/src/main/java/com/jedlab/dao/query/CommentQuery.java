package com.jedlab.dao.query;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Chapter;
import com.jedlab.model.Comment;
import com.jedlab.model.Course;

@Name("commentQuery")
@Scope(ScopeType.CONVERSATION)
public class CommentQuery extends PagingEntityQuery<Comment>
{

    private static final String EJBQL = "select c from Comment c LEFT OUTER JOIN c.course course LEFT OUTER JOIN c.member m where c.reply is null";

    private static final String[] RESTRICTIONS = { 
            "c.course.id = #{commentQuery.course.id}"};
    
    Course course = new Course();

    public CommentQuery()
    {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        // setOrderColumn("memoType");
        setMaxResults(15);
    }

    public Course getCourse()
    {
        return course;
    }
    
    

}
