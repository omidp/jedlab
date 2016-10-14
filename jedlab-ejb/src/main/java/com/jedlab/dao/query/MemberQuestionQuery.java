package com.jedlab.dao.query;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.action.Constants;
import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.MemberQuestion;

@Name("memberQuestionQuery")
@Scope(ScopeType.CONVERSATION)
public class MemberQuestionQuery extends PagingEntityQuery<MemberQuestion>
{

    private static final String EJBQL = "select mq from MemberQuestion mq LEFT OUTER JOIN mq.question q";

    private static final String[] RESTRICTIONS = { "mq.member.id = #{memberQuestionQuery.currentUserId}" };

    public MemberQuestionQuery()
    {
        setMaxResults(9);
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }

    public Long getCurrentUserId()
    {
        return (Long) getSessionContext().get(Constants.CURRENT_USER_ID);
    }

}
