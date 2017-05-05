package com.jedlab.story;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.StringUtil;
import com.jedlab.model.Member;

@Name("storyQuery")
@Scope(ScopeType.CONVERSATION)
public class StoryQuery extends AbstractStoryQuery
{

    
    
    public StoryQuery()
    {
        setMaxResults(45);
    }
    
    private Member member = new Member();
    
    

    public Member getMember()
    {
        return member;
    }



    @Override
    protected void applyFilter(Criteria criteria)
    {
        super.applyFilter(criteria);
        criteria.add(Restrictions.eq("s.published", true));
        if(StringUtil.isNotEmpty(getMember().getUsername()))
        {
            criteria.add(Restrictions.eq("m.username", getMember().getUsername()));    
        }
    }
    
    
}
