package com.jedlab.dao.query;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

import com.jedlab.action.Constants;
import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.framework.PagingController;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.WebContext;
import com.jedlab.model.Course;
import com.jedlab.model.Invoice;

@Name("incomeQuery")
@Scope(ScopeType.CONVERSATION)
public class IncomeQuery extends PagingController<Invoice>
{

    private List<Invoice> resultList;
    private Long resultCount;
    private Course course = new Course();;

    public IncomeQuery()
    {
        setMaxResults(10);
    }

    public Course getCourse()
    {
        return course;
    }

    @Override
    public List<Invoice> getResultList()
    {
        if (isAnyParameterDirty())
        {
            refresh();
        }
        if (resultList != null)
            return truncResultList(resultList);
        Criteria criteria = getSession().createCriteria(Invoice.class, "i");
        applyFilter(criteria);
        if (getFirstResult() != null)
            criteria.setFirstResult(getFirstResult());
        if (getMaxResults() != null)
            criteria.setMaxResults(getMaxResults() + 1);
        if (StringUtil.isNotEmpty(getOrderColumn()))
        {
            if ("asc".equals(getOrderDirection()))
                criteria.addOrder(Order.asc(getOrderColumn()));
            else
                criteria.addOrder(Order.desc(getOrderColumn()));
        }
        else
        {
            criteria.addOrder(Order.desc("i.paid")).addOrder(Order.desc("c.id"));
        }
        resultList = criteria.list();
        return truncResultList(resultList);
    }

    private void applyFilter(Criteria criteria)
    {
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        if (uid == null)
            throw new ErrorPageExceptionHandler("user id is null");
        criteria.createCriteria("i.course", "c", Criteria.LEFT_JOIN);
        criteria.createCriteria("i.member", "m", Criteria.LEFT_JOIN);
        criteria.createCriteria("c.instructor", "owner", Criteria.LEFT_JOIN);
        criteria.add(Restrictions.eq("owner.id", uid));
        //
        if(getCourse().getId() != null)
        {
            criteria.add(Restrictions.eq("c.id", getCourse().getId()));
        }
        else
        {
            if(StringUtil.isNotEmpty(getCourse().getName()))
            {
                criteria.add(Restrictions.like("c.name", getCourse().getName(), MatchMode.ANYWHERE));
            }
        }
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
        Criteria criteria = getSession().createCriteria(Invoice.class, "i");
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

    private BigDecimal totalIncome;

    public BigDecimal getTotalIncome()
    {
        if (totalIncome == null)
        {
            totalIncome = BigDecimal.ZERO;
            Criteria criteria = getSession().createCriteria(Invoice.class, "i");
            criteria.add(Restrictions.eq("i.paid", true));
            applyFilter(criteria);
            criteria.setProjection(Projections.sum("paymentAmount"));
            totalIncome = (BigDecimal) criteria.uniqueResult();
        }
        return totalIncome;
    }
    
    public void redirectIt()
    {
        Map<String, Object> params = new HashMap<>();
        params.put("pageNumber", 0);
        WebContext.instance().redirectIt(false, true, params);
    }

}
