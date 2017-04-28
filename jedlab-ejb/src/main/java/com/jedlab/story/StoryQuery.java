package com.jedlab.story;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("storyQuery")
@Scope(ScopeType.CONVERSATION)
public class StoryQuery extends AbstractStoryQuery
{

    public StoryQuery()
    {
        setMaxResults(45);
    }

    @Override
    protected void applyFilter(Criteria criteria)
    {
        super.applyFilter(criteria);
        criteria.add(Restrictions.eq("s.published", true));
    }
    
    
}
