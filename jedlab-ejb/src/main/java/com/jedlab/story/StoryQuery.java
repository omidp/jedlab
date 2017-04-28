package com.jedlab.story;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingController;
import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Chapter;
import com.jedlab.model.Comment;
import com.jedlab.model.Course;
import com.jedlab.model.Question;
import com.jedlab.model.Story;

@Name("storyQuery")
@Scope(ScopeType.CONVERSATION)
public class StoryQuery extends PagingController<Story>
{

    
    List<Story> resultList;
    Long resultCount;
    
    
    
    public StoryQuery()
    {
        setMaxResults(45);
    }

    @Override
    public List<Story> getResultList()
    {
        if (isAnyParameterDirty())
        {
            refresh();
        }
        if (resultList != null)
            return truncResultList(resultList);
        Criteria criteria = getSession().createCriteria(Story.class, "s");
        applyFilter(criteria);
        if (getFirstResult() != null)
            criteria.setFirstResult(getFirstResult());
        if (getMaxResults() != null)
            criteria.setMaxResults(getMaxResults() + 1);
        resultList = criteria.list();
        return truncResultList(resultList);
    }

    private void refresh()
    {
        resultList = null;
        resultCount = null;
    }

    private void applyFilter(Criteria criteria)
    {
        criteria.createCriteria("member",  "m", Criteria.LEFT_JOIN);
        
    }

    @Override
    public Long getResultCount()
    {
        if (isAnyParameterDirty())
        {
            refresh();
        }
        if (resultCount != null)
            return resultCount;
        Criteria criteria = getSession().createCriteria(Story.class, "s");
        applyFilter(criteria);
        criteria.setProjection(Projections.rowCount());
        resultCount = (Long) criteria.uniqueResult();
        return resultCount;
    }

    @Override
    public boolean isNextExists()
    {
        return resultList != null && getMaxResults() != null && resultList.size() > getMaxResults();
    }

    
}
