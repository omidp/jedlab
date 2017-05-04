package com.jedlab.story;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

import com.jedlab.action.Constants;

@Name("personalStoryQuery")
@Scope(ScopeType.CONVERSATION)
public class PersonalStoryQuery extends AbstractStoryQuery
{

    public PersonalStoryQuery()
    {
        setMaxResults(15);
        setOrderColumn("createdDate");
        setOrderDirection("desc");
    }

    @Override
    protected void applyFilter(Criteria criteria)
    {
        super.applyFilter(criteria);
        if (Identity.instance().isLoggedIn())
        {
            Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
            criteria.add(Restrictions.eq("m.id", uid));
        }
    }

}
