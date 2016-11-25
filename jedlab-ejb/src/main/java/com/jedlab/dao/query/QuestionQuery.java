package com.jedlab.dao.query;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

import com.jedlab.action.Constants;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.PagingController;
import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;
import com.jedlab.model.MemberQuestion;
import com.jedlab.model.Question;

@Name("questionQuery")
@Scope(ScopeType.CONVERSATION)
public class QuestionQuery extends PagingController<Question>
{

    private List<Question> resultList;
    private Long resultCount;
    Question question = new Question();

    public QuestionQuery()
    {
        setMaxResults(9);
    }

    @Override
    public List<Question> getResultList()
    {
        if (isAnyParameterDirty())
        {
            refresh();
        }
        if (resultList != null)
            return truncResultList(resultList);
        Criteria criteria = getSession().createCriteria(Question.class, "q");
        if (Identity.instance().isLoggedIn())
        {
            DetachedCriteria dc = DetachedCriteria.forClass(MemberQuestion.class, "mq");
            dc.add(Restrictions.eq("mq.member.id", (Long)Contexts.getSessionContext().get(Constants.CURRENT_USER_ID)));
            dc.setProjection(Projections.property("mq.question.id"));
            criteria.add(Subqueries.propertyNotIn("id", dc));
        }
        if (getFirstResult() != null)
            criteria.setFirstResult(getFirstResult());
        if (getMaxResults() != null)
            criteria.setMaxResults(getMaxResults() + 1);
        resultList = criteria.list();
        addUserSolvedCount(resultList);
        return truncResultList(resultList);
    }

    private void addUserSolvedCount(List<Question> qList)
    {
        if(CollectionUtil.isNotEmpty(qList))
        {
            Criteria criteria = getSession().createCriteria(MemberQuestion.class, "mq");        
            criteria.setProjection(Projections.projectionList().add(Projections.count("question"))
                    .add(Projections.groupProperty("question")));
            criteria.add(Restrictions.in("question", qList));
            List<Object[]> obj = criteria.list();
            for (Object[] items : obj)
            {
                Long userSolvedCount = Long.parseLong(String.valueOf(items[0]));
                Question q = (Question) items[1];
                for (Question item : qList)
                {
                    if(q.getId().longValue() == item.getId().longValue())
                    {
                        item.setUserCount(userSolvedCount);
                    }
                }
            }
        }
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
        Criteria criteria = getSession().createCriteria(Question.class, "q");
        applyFilter(criteria);
        criteria.setProjection(Projections.rowCount());
        resultCount = (Long) criteria.uniqueResult();
        return resultCount;
    }

    private void applyFilter(Criteria criteria)
    {
        // TODO Auto-generated method stub

    }

    private void refresh()
    {
        resultList = null;
        resultCount = null;
    }

    @Override
    public boolean isNextExists()
    {
        return resultList != null && getMaxResults() != null && resultList.size() > getMaxResults();
    }

    public Question getQuestion()
    {
        return question;
    }

}
