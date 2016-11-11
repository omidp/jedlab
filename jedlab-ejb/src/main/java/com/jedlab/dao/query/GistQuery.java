package com.jedlab.dao.query;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

import com.jedlab.action.Constants;
import com.jedlab.framework.PagingController;
import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.framework.StringUtil;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;
import com.jedlab.model.Gist;

@Name("gistQuery")
@Scope(ScopeType.CONVERSATION)
public class GistQuery extends PagingController<Gist>
{

    private List<Gist> resultList;
    private Long resultCount;

    public GistQuery()
    {
        setMaxResults(25);
    }

    @Override
    public List<Gist> getResultList()
    {
        if (isAnyParameterDirty())
        {
            refresh();
        }
        if (resultList != null)
            return resultList;
        Criteria criteria = getSession().createCriteria(Gist.class, "g");
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
        criteria.createCriteria("g.member", "m", Criteria.LEFT_JOIN);
        if (Identity.instance().isLoggedIn())
        {
            if (Identity.instance().hasRole(Constants.ROLE_ADMIN) == false)
            {
                Disjunction dis = Restrictions.disjunction();
                Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
                dis.add(Restrictions.and(Restrictions.eq("privateGist", false), Restrictions.ne("m.id", uid)));
                dis.add(Restrictions.eq("m.id", uid));
                criteria.add(dis);
            }
        }
        else
        {
            criteria.add(Restrictions.eq("privateGist", false));
        }
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
        Criteria criteria = getSession().createCriteria(Gist.class, "g");
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
