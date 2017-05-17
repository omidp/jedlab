package com.jedlab.dao.query;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingController;
import com.jedlab.model.Page;

@Name("pageQuery")
@Scope(ScopeType.CONVERSATION)
public class PageQuery extends PagingController<Page>
{

    
    List<Page> resultList;
    Long resultCount;
    
    
    public PageQuery()
    {
        setMaxResults(30);
    }

    @Override
    public List<Page> getResultList()
    {
        if (isAnyParameterDirty())
        {
            refresh();
        }
        if (resultList != null)
            return truncResultList(resultList);
        Criteria criteria = getSession().createCriteria(Page.class, "p");
        applyFilter(criteria);
        if (getFirstResult() != null)
            criteria.setFirstResult(getFirstResult());
        if (getMaxResults() != null)
            criteria.setMaxResults(getMaxResults() + 1);
        resultList = criteria.list();
        return truncResultList(resultList);
    }

    private void applyFilter(Criteria criteria)
    {
        criteria.createCriteria("p.member", "m", Criteria.LEFT_JOIN);
    }

    private void refresh()
    {
        this.resultCount = null;
        this.resultList = null;
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
        Criteria criteria = getSession().createCriteria(Page.class, "p");
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
