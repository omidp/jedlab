package com.jedlab.podcast;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Member;
import com.jedlab.model.Podcast;

@Name("podcastQuery")
@Scope(ScopeType.CONVERSATION)
public class PodcastQuery extends PagingEntityQuery<Podcast>
{

    private static final String EJBQL = "select p from Podcast p JOIN FETCH p.member m ";

    private static final String[] RESTRICTIONS = {  };

    Member member = new Member();

    public PodcastQuery()
    {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        // filter on model
        setOrderColumn("p.createdDate");
        setOrderDirection("desc");
        setMaxResults(25);
    }

    public Member getMember()
    {
        return member;
    }

}
