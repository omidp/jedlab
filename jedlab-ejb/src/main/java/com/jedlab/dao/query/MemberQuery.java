package com.jedlab.dao.query;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Chapter;
import com.jedlab.model.Comment;
import com.jedlab.model.Course;
import com.jedlab.model.Member;

@Name("memberQuery")
@Scope(ScopeType.CONVERSATION)
public class MemberQuery extends PagingEntityQuery<Member>
{

    private static final String EJBQL = "select m from Member m ";

    private static final String[] RESTRICTIONS = {};

    Member member = new Member();

    public MemberQuery()
    {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setOrderColumn("id");
        setMaxResults(15);
    }

    public Member getMember()
    {
        return member;
    }

}
