package com.jedlab.framework;

import org.jboss.seam.framework.HibernateEntityQuery;

/**
 * @author Omid Pourhadi
 *
 */
public class HibernatePagingEntityQuery<E> extends HibernateEntityQuery<E>
{

    public Integer pageNumber;

    public Integer getPageNumber()
    {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber)
    {
        this.pageNumber = pageNumber;
    }

    @Override
    public Integer getFirstResult()
    {
        if (pageNumber != null)
        {
            if (pageNumber >= getPageCount() && getPageCount() > 0)
            {
                pageNumber = (getPageCount() - 1);
            }
            return pageNumber * getMaxResults();
        }
        return super.getFirstResult();
    }
    
}
