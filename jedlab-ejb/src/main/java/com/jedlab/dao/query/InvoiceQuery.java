package com.jedlab.dao.query;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Tag;

@Name("invoiceQuery")
@Scope(ScopeType.CONVERSATION)
public class InvoiceQuery extends PagingEntityQuery<Tag>
{

    private static final String EJBQL = "select i from Invoice i ";

    private static final String[] RESTRICTIONS = { "i.member.id = #{loginActionManager.currentUserId}", };


    public InvoiceQuery()
    {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setOrderColumn("id");
        setMaxResults(15);
    }


}
