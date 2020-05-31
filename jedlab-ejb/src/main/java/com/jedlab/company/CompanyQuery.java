package com.jedlab.company;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.CompanyEntity;
import com.jedlab.model.Member;
import com.jedlab.model.Podcast;

@Name("companyQuery")
@Scope(ScopeType.CONVERSATION)
public class CompanyQuery extends PagingEntityQuery<CompanyEntity>
{

    private static final String EJBQL = "select c from CompanyEntity c ";

    private static final String[] RESTRICTIONS = {  };

    

    public CompanyQuery()
    {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        // filter on model
//        setOrderColumn("p.createdDate");
//        setOrderDirection("desc");
        setMaxResults(25);
    }

    

}
