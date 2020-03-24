package com.jedlab.action;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityController;

import com.jedlab.JedLab;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.DBUtil;
import com.jedlab.framework.XmlParser;
import com.jedlab.model.MemberStatisticsView;

@Name("dashboardAction")
@Scope(ScopeType.CONVERSATION)
public class DashboardAction extends EntityController
{

    private MemberStatisticsView statistic;

    private String skillPercent;

    public String getSkillPercent()
    {
        if (skillPercent == null)
        {
            String query = XmlParser.findNativeQuery("dashboard", "skill");
            List<Map<String, Object>> qResult = DBUtil.instance().executeQuery(query, JedLab.instance().getCurrentUserId());
            if (CollectionUtil.isNotEmpty(qResult))
            {
                BigDecimal bd = (BigDecimal) qResult.iterator().next().get("calculated_skill");
                if(bd.doubleValue() > 100)
                    bd = new BigDecimal(100);
                DecimalFormat df = new DecimalFormat("###.##");
                skillPercent = df.format(bd.doubleValue());
            }
        }
        return  skillPercent;
    }

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
