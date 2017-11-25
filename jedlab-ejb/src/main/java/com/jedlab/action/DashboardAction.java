package com.jedlab.action;

import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityController;

import com.jedlab.JedLab;
import com.jedlab.model.MemberStatisticsView;

@Name("dashboardAction")
@Scope(ScopeType.CONVERSATION)
public class DashboardAction extends EntityController
{

    private MemberStatisticsView statistic;

    public MemberStatisticsView getStatistic()
    {
        if (statistic == null)
        {
            try
            {
                statistic = (MemberStatisticsView) getEntityManager().createNamedQuery(MemberStatisticsView.FIND_BY_MEMBER_ID)
                        .setParameter("memberId", JedLab.instance().getCurrentUserId()).setMaxResults(1).getSingleResult();
            }
            catch (NoResultException e)
            {
            }
        }
        return statistic;
    }

    public void load()
    {

    }

}
