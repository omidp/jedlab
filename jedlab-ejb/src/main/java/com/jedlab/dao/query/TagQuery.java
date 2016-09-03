package com.jedlab.dao.query;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Tag;

@Name("tagQuery")
@Scope(ScopeType.CONVERSATION)
public class TagQuery extends PagingEntityQuery<Tag>
{

    private static final String EJBQL = "select t from Tag t ";

    private static final String[] RESTRICTIONS = { "lower(t.name) like lower(concat('%',concat(#{tagQuery.tag.name},'%')))", };

    Tag tag = new Tag();

    public TagQuery()
    {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setOrderColumn("id");
        setMaxResults(15);
    }

    public Tag getTag()
    {
        return tag;
    }

}
