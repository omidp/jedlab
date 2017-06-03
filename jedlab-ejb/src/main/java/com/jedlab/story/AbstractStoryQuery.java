package com.jedlab.story;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import com.jedlab.framework.PagingController;
import com.jedlab.model.Story;

public abstract class AbstractStoryQuery extends PagingController<Story>
{

    List<Story> resultList;
    Long resultCount;


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
        criteria.addOrder(Order.desc("createdDate"));
        resultList = criteria.list();
        return truncResultList(resultList);
    }

    private void refresh()
    {
        resultList = null;
        resultCount = null;
    }

    protected void applyFilter(Criteria criteria)
    {
        criteria.createCriteria("member", "m", Criteria.LEFT_JOIN);
        
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
