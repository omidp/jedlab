package com.jedlab.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;

/**
 * @author Omid Pourhadi
 * 
 */
public abstract class PagingController<E> implements Serializable
{

    
    private List<ValueExpression> expresionsList = new ArrayList<ValueExpression>();
    List<Object> lastParameterValues = new ArrayList<Object>();
    private Integer firstResult;
    private Integer maxResults;

    public Integer pageNumber;

    public Integer getPageNumber()
    {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber)
    {
        this.pageNumber = pageNumber;
    }

    public abstract List<E> getResultList();

    public abstract Long getResultCount();

    /**
     * Move the result set cursor to the beginning of the last page
     * 
     */
    @Transactional
    public void last()
    {
        setFirstResult(getLastFirstResult().intValue());
    }

    /**
     * Move the result set cursor to the beginning of the next page
     * 
     */
    public void next()
    {
        setFirstResult(getNextFirstResult());
    }

    /**
     * Move the result set cursor to the beginning of the previous page
     * 
     */
    public void previous()
    {
        setFirstResult(getPreviousFirstResult());
    }

    /**
     * Move the result set cursor to the beginning of the first page
     * 
     */
    public void first()
    {
        setFirstResult(0);
    }

    /**
     * Get the index of the first result of the last page
     * 
     */
    @Transactional
    public Long getLastFirstResult()
    {
        Integer pc = getPageCount();
        return pc == null ? null : (pc.longValue() - 1) * getMaxResults();
    }

    /**
     * Get the index of the first result of the next page
     * 
     */
    public int getNextFirstResult()
    {
        Integer fr = getFirstResult();
        return (fr == null ? 0 : fr) + getMaxResults();
    }

    /**
     * Get the index of the first result of the previous page
     * 
     */
    public int getPreviousFirstResult()
    {
        Integer fr = getFirstResult();
        Integer mr = getMaxResults();
        return mr >= (fr == null ? 0 : fr) ? 0 : fr - mr;
    }

    /**
     * Get the total number of pages
     * 
     */
    @Transactional
    public Integer getPageCount()
    {
        if (getMaxResults() == null)
        {
            return null;
        }
        else
        {
            int rc = getResultCount().intValue();
            int mr = getMaxResults().intValue();
            int pages = rc / mr;
            return rc % mr == 0 ? pages : pages + 1;
        }
    }

    /**
     * Returns the index of the first result of the current page
     */
    public Integer getFirstResult()
    {
        if (pageNumber != null && getPageCount() != null)
        {
            if (pageNumber >= getPageCount() && getPageCount() > 0)
            {
                pageNumber = (getPageCount() - 1);
            }
            return pageNumber * getMaxResults();
        }
        return firstResult;
    }

    /**
     * Returns true if the previous page exists
     */
    public boolean isPreviousExists()
    {
        return getFirstResult() != null && getFirstResult() != 0;
    }

    /**
     * Returns true if next page exists
     */
    public abstract boolean isNextExists();

    /**
     * Returns true if the query is paginated, revealing whether navigation
     * controls are needed.
     */
    public boolean isPaginated()
    {
        return isNextExists() || isPreviousExists();
    }

    /**
     * Set the index at which the page to display should start
     */
    public void setFirstResult(Integer firstResult)
    {
        this.firstResult = firstResult;
    }

    /**
     * The page size
     */
    public Integer getMaxResults()
    {
        return maxResults;
    }

    public void setMaxResults(Integer maxResults)
    {
        this.maxResults = maxResults;
    }

    protected List<E> truncResultList(List<E> results)
    {
        Integer mr = getMaxResults();
        if (mr != null && results.size() > mr)
        {
            return results.subList(0, mr);
        }
        else
        {
            return results;
        }
    }

    //
    private String orderColumn;

    private String orderDirection;

    public String getOrderColumn()
    {
        return orderColumn;
    }

    public void setOrderColumn(String orderColumn)
    {
        this.orderColumn = orderColumn;
    }

    public String getOrderDirection()
    {
        return orderDirection;
    }

    public void setOrderDirection(String orderDirection)
    {
        this.orderDirection = orderDirection;
    }

    protected Session getSession()
    {
        return (Session) Component.getInstance("hibernateSession");
    }
    
    protected Session getRestrictedSession()
    {
      //TODO : multitenancy
        return (Session) Component.getInstance("restrictedHibernateSession");
    }

    protected EntityManager getEntityManager()
    {
        //TODO : multitenancy
        return (EntityManager) Component.getInstance("entityManager");
    }
    
    protected EntityManager getRestrictedEntityManager()
    {
        return (EntityManager) Component.getInstance("restrictedEntityManager");
    }

    protected void sortCriteria(Criteria criteria)
    {
        if (StringUtil.isNotEmpty(getOrderDirection()) && StringUtil.isNotEmpty(getOrderColumn()))
        {
            if (getOrderDirection().equals("asc"))
                criteria.addOrder(Order.asc(getOrderColumn()));
            if (getOrderDirection().equals("desc"))
                criteria.addOrder(Order.desc(getOrderColumn()));
        }
    }
    
    protected boolean isAnyParameterDirty()
    {
        for (int i = 0; i < expresionsList.size(); i++)
        {
            if (lastParameterValues.size() == expresionsList.size())
            {
                Object expressionValue = expresionsList.get(i).getValue();
                Object value = lastParameterValues.get(i);
                if (expressionValue != value && (expressionValue == null || !expressionValue.equals(value)))
                    return true;
            }
        }

        return false;
    }
    
    protected void addToExpressions(String expression)
    {
        expresionsList.add(Expressions.instance().createValueExpression(expression));
    }
    
    protected void addToValueParams(String expression)
    {
        lastParameterValues.add(Expressions.instance().createValueExpression(expression).getValue());
    }

}
