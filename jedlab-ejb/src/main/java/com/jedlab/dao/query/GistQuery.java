package com.jedlab.dao.query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.lucene.search.Query;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesManager;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.security.Identity;

import com.jedlab.action.Constants;
import com.jedlab.framework.PagingController;
import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.WebContext;
import com.jedlab.framework.WebUtil;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;
import com.jedlab.model.Gist;

@Name("gistQuery")
@Scope(ScopeType.CONVERSATION)
public class GistQuery extends PagingController<Gist>
{

    @In
    private FullTextEntityManager entityManager;
    
    private List<Gist> resultList;
    private Long resultCount;
    private String searchPattern;

    public GistQuery()
    {
        setMaxResults(10);
    }

    public String getSearchPattern()
    {
        return searchPattern;
    }

    public void setSearchPattern(String searchPattern)
    {
        this.searchPattern = searchPattern;
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
        if(StringUtil.isEmpty(getSearchPattern()))
        {
            Criteria criteria = getSession().createCriteria(Gist.class, "g");
            applyFilter(criteria);
            if (getFirstResult() != null)
                criteria.setFirstResult(getFirstResult());
            if (getMaxResults() != null)
                criteria.setMaxResults(getMaxResults() + 1);
            resultList = criteria.list();
            return truncResultList(resultList);
        }
        else
        {
            resultList = entityManager.createFullTextQuery(getFullTextSearch(), Gist.class).setMaxResults(100).getResultList();
            setMaxResults(100);
            return resultList;
        }
    }
    
    
    private Query getFullTextSearch()
    {
        QueryBuilder queryBuilder = entityManager.getSearchFactory().buildQueryBuilder().forEntity(Gist.class).get();

        // A fulltext query using English Analyzer
        Query queryUsingEnglishStemmer = queryBuilder.keyword().onFields("title:en").boostedTo(4f).andField("description:en")
                .matching(searchPattern).createQuery();

        // A fulltext query using Persian Analyzer
        Query queryUsingPersianStemmer = queryBuilder.keyword().onFields("description:fa").boostedTo(4f).matching(searchPattern)
                .createQuery();

        // A fulltext query using ngrams
        Query queryUsingNGrams = queryBuilder.keyword().onFields("title:ngrams").boostedTo(2f).matching(searchPattern).createQuery();
        // Combine them for best results:
        Query fullTextQuery = queryBuilder.bool().should(queryUsingEnglishStemmer).should(queryUsingNGrams)
                .should(queryUsingPersianStemmer).createQuery();
        
        return fullTextQuery;

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
    
    public void redirect()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("pageNumber", 0);
        parameters.put("searchPattern", getSearchPattern());
        FacesManager.instance().redirect(Pages.getCurrentViewId(), parameters, false, false);
    }

}
