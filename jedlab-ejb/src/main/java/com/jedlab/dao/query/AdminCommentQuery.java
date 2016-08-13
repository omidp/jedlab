package com.jedlab.dao.query;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.security.Restrict;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.framework.WebUtil;
import com.jedlab.model.Chapter;
import com.jedlab.model.Comment;
import com.jedlab.model.Course;
import com.jedlab.model.Member;

@Name("adminCommentQuery")
@Scope(ScopeType.CONVERSATION)
@Restrict(value="#{s:hasRole('Admin')}")
public class AdminCommentQuery extends PagingEntityQuery<Comment>
{

    private static final String EJBQL = "select c from Comment c LEFT OUTER JOIN c.course course LEFT OUTER JOIN c.member m";

    private static final String[] RESTRICTIONS = { "c.course.id = #{adminCommentQuery.course.id}", "c.member.username = #{adminCommentQuery.member.username}" };

    Course course = new Course();

    Member member = new Member();

    public AdminCommentQuery()
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

    public Member getMember()
    {
        return member;
    }
    
    @Transactional
    public void delete()
    {
        String commentId = WebUtil.getParameterValue("commentId");
        Comment comment = getEntityManager().find(Comment.class, Long.parseLong(commentId));
        getEntityManager().remove(comment);
        getEntityManager().flush();
    }

}
