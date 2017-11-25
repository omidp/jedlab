package com.jedlab.story;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.StringUtil;
import com.jedlab.model.Course;
import com.jedlab.model.Member;
import com.jedlab.model.Story;

@Name("storyQuery")
@Scope(ScopeType.CONVERSATION)
public class StoryQuery extends AbstractStoryQuery
{

    public StoryQuery()
    {
        setMaxResults(15);
        setOrderColumn("createdDate");
        setOrderDirection("desc");
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
        if (StringUtil.isNotEmpty(getMember().getUsername()))
        {
            criteria.add(Restrictions.eq("m.username", getMember().getUsername()));
        }

    }
    
    
    public List<Story> getTop5()
    {
        List<Story> list = getResultList();
        if(list.size() > 5)
            return list.subList(0, 5);
        return list;
    }

}
