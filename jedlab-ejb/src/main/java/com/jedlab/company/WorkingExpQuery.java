package com.jedlab.company;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.JedLab;
import com.jedlab.framework.PagingController;
import com.jedlab.model.WorkingExprienceEntity;

@Name("workingExpQuery")
@Scope(ScopeType.CONVERSATION)
public class WorkingExpQuery extends PagingController<WorkingExprienceEntity>
{

    
    private List<WorkingExprienceEntity> resultList;

    private Long resultCount;

    WorkingExprienceEntity exp = new WorkingExprienceEntity();
    
    public WorkingExpQuery()
    {
        setMaxResults(9);
        
        addToExpressions("#{workingExpQuery.exp.id}");
        
    }

    @Override
    public List<WorkingExprienceEntity> getResultList()
    {
        if (isAnyParameterDirty())
        {
            refresh();
        }
        if (resultList != null)
            return truncResultList(resultList);
        Criteria criteria = getSession().createCriteria(WorkingExprienceEntity.class, "we");
        criteria.createCriteria("we.position", "p", Criteria.LEFT_JOIN);
        criteria.createCriteria("we.contract", "c", Criteria.LEFT_JOIN);
        criteria.createCriteria("we.salary", "s", Criteria.LEFT_JOIN);
        applyFilter(criteria);
        
        addToValueParams("#{workingExpQuery.exp.id}");
        if (getFirstResult() != null)
            criteria.setFirstResult(getFirstResult());
        if (getMaxResults() != null)
            criteria.setMaxResults(getMaxResults() + 1);
        criteria.addOrder(Order.desc("createdDate"));
        resultList = criteria.list();
        return truncResultList(resultList);
    }

    private void applyFilter(Criteria criteria)
    {
//        criteria.add(Restrictions.eq("we.member.id", JedLab.instance().getCurrentUserId()));
        criteria.add(Restrictions.eq("we.approved", true));
    }

    private void refresh()
    {
        resultCount = null;
        resultList = null;
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
        Criteria criteria = getSession().createCriteria(WorkingExprienceEntity.class, "we");
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

    public WorkingExprienceEntity getExp()
    {
        return exp;
    }
    
    
    

}
