package com.jedlab.dao.query;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.security.Restrict;

import com.jedlab.framework.PagingController;
import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.WebUtil;
import com.jedlab.model.Chapter;
import com.jedlab.model.Comment;
import com.jedlab.model.Course;
import com.jedlab.model.Invoice;
import com.jedlab.model.Member;

@Name("adminInvoiceQuery")
@Scope(ScopeType.CONVERSATION)
@Restrict(value = "#{s:hasRole('Admin')}")
public class AdminInvoiceQuery extends PagingController<Invoice>
{

    List<Invoice> resultList;

    Long resultCount;

    public AdminInvoiceQuery()
    {
        setMaxResults(15);
    }

    @Override
    public List<Invoice> getResultList()
    {
        if (resultList != null)
            return truncResultList(resultList);
        Criteria criteria = createCriteria();
        if (getFirstResult() != null)
            criteria.setFirstResult(getFirstResult());
        if (getMaxResults() != null)
            criteria.setMaxResults(getMaxResults() + 1);
        if(StringUtil.isNotEmpty(getOrderColumn()))
        {
            if("asc".equals(getOrderDirection()))
                    criteria.addOrder(Order.asc(getOrderColumn()));
            if("desc".equals(getOrderDirection()))
                criteria.addOrder(Order.desc(getOrderColumn()));
        }
        resultList = criteria.list();
        return truncResultList(resultList);
    }

    private Criteria createCriteria()
    {
        Criteria criteria = getSession().createCriteria(Invoice.class, "i");
        criteria.createCriteria("i.member", "m", Criteria.LEFT_JOIN);
        criteria.createCriteria("i.course", "c", Criteria.LEFT_JOIN);
        return criteria;
    }

    @Override
    public Long getResultCount()
    {
        if (resultCount != null)
            return resultCount;
        Criteria criteria = createCriteria();
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
