package com.jedlab.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityController;
import org.jboss.seam.framework.HibernateEntityController;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.web.Parameters;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.jedlab.JedLab;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.WebContext;
import com.jedlab.framework.WebUtil;
import com.jedlab.model.AnswerEntity;
import com.jedlab.model.CourseQuestion;
import com.jedlab.model.MemberAnswerEntity;

@Name("quizAction")
@Scope(ScopeType.CONVERSATION)
public class QuizAction extends HibernateEntityController
{

    private Long courseId;

    private CourseQuestion question;

    private List<AnswerEntity> answers;

    private List<MemberAnswerEntity> memberAnswers;

    private Long memberCorrectAnswer;

    private Integer sequence = 10;

    private Long questionCount;

    private boolean lastQuestionPassedSuccesful;
    
    private boolean courseHasQuestion = true;
    
    

    public boolean isCourseHasQuestion()
    {
        return courseHasQuestion;
    }

    public Long getMemberCorrectAnswer()
    {
        if (memberCorrectAnswer == null)
        {
            Criteria criteria = getSession().createCriteria(MemberAnswerEntity.class, "ma");
            criteria.add(Restrictions.eq("ma.member.id", JedLab.instance().getCurrentUserId()));
            criteria.add(Restrictions.eq("ma.correct", true));
            //
            DetachedCriteria dc = DetachedCriteria.forClass(CourseQuestion.class, "cq");
            dc.add(Restrictions.eq("cq.course.id", getCourseId()));
            dc.setProjection(Projections.id());
            criteria.add(Subqueries.propertyIn("ma.question.id", dc));            
            criteria.setProjection(Projections.rowCount());
            memberCorrectAnswer = (Long) criteria.uniqueResult();
        }
        return memberCorrectAnswer;
    }

    public List<MemberAnswerEntity> getMemberAnswers()
    {
        if (memberAnswers == null)
        {
            Criteria criteria = getSession().createCriteria(MemberAnswerEntity.class, "ma");
            criteria.createCriteria("ma.question", "q", Criteria.LEFT_JOIN);
            criteria.add(Restrictions.eq("ma.member.id", JedLab.instance().getCurrentUserId()));
            DetachedCriteria dc = DetachedCriteria.forClass(CourseQuestion.class, "cq");
            dc.add(Restrictions.eq("cq.course.id", getCourseId()));
            dc.setProjection(Projections.id());
            criteria.add(Subqueries.propertyIn("ma.question.id", dc));  
            memberAnswers = criteria.list();
        }
        return memberAnswers;
    }

    public boolean isLastQuestionPassedSuccesful()
    {
        return lastQuestionPassedSuccesful;
    }

    public void setLastQuestionPassedSuccesful(boolean lastQuestionPassedSuccesful)
    {
        this.lastQuestionPassedSuccesful = lastQuestionPassedSuccesful;
    }

    public Long getQuestionCount()
    {
        if (questionCount == null)
        {
            Criteria criteria = getSession().createCriteria(CourseQuestion.class, "cq");
            criteria.add(Restrictions.eq("cq.course.id", getCourseId()));
            criteria.setProjection(Projections.rowCount());
            questionCount = (Long) criteria.uniqueResult();
        }
        return questionCount;
    }

    public Integer getSequence()
    {
        return sequence;
    }

    public void setSequence(Integer sequence)
    {
        this.sequence = sequence;
    }

    public CourseQuestion getQuestion()
    {
        return question;
    }

    public void setQuestion(CourseQuestion question)
    {
        this.question = question;
    }

    public Long getCourseId()
    {
        return courseId;
    }

    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    public List<AnswerEntity> getAnswers()
    {
        return answers;
    }

    public void setAnswers(List<AnswerEntity> answers)
    {
        this.answers = answers;
    }

    public void load()
    {
        // agar tedad javab ha ba soalat barabar bood test tamoom shode
        Long countMemberAnswer = getMemberCorrectAnswer();
        if (countMemberAnswer > 0 && getQuestionCount().longValue() == countMemberAnswer.longValue())
        {
            lastQuestionPassedSuccesful = true;
        }
        else
        {
            // akharin soali ke taraf javab dade chist
            CourseQuestion currentQuestion = (CourseQuestion) getSession()
                    .createQuery(
                            "select mae.question from  MemberAnswerEntity mae where mae.member.id = :memId and mae.question.id in (select q.id from CourseQuestion q where q.course.id = :courseId) order by mae.createdDate desc")
                    .setParameter("memId", JedLab.instance().getCurrentUserId()).setParameter("courseId", getCourseId()).setMaxResults(1).uniqueResult();
            if (currentQuestion != null)
            {

                this.question = (CourseQuestion) getSession()
                        .createQuery("select q from  CourseQuestion q where q.course.id = :courseId and  q.sequence = :seq order by q.createdDate desc")
                        .setParameter("seq", currentQuestion.getSequence() + 10).setParameter("courseId", getCourseId()).setMaxResults(1).uniqueResult();
                //TODO:handle in database with trigger when sequence order is distorted
                if(this.question == null)
                {
                    int cnt=1;
                    int seq = currentQuestion.getSequence() + 10;
                    while(this.question == null && cnt != getQuestionCount().intValue())
                    {
                        seq+=10;
                        this.question = (CourseQuestion) getSession()
                                .createQuery("select q from  CourseQuestion q where q.course.id = :courseId and  q.sequence = :seq order by q.createdDate desc")
                                .setParameter("seq", seq).setParameter("courseId", getCourseId()).setMaxResults(1).uniqueResult();
                        cnt++;
                    }
                }
            }
            else
            {

                // hich soali javab nadade avalin soal ro peyda kon
                Criteria criteria = getSession().createCriteria(CourseQuestion.class, "cq");
                criteria.add(Restrictions.eq("cq.course.id", getCourseId()));
                criteria.addOrder(Order.asc("cq.createdDate"));
                criteria.addOrder(Order.asc("cq.sequence"));
                criteria.setMaxResults(1);
                this.question = (CourseQuestion) criteria.uniqueResult();
                if(this.question == null)
                    courseHasQuestion = false;
            }
            //
            if (this.question != null)
            {
                // not reach to end
                this.answers = this.question.getAnswers();
            }
            else
            {
                if(courseHasQuestion)
                    lastQuestionPassedSuccesful = true;
            }
        }
    }

    @Transactional
    public void done()
    {
        String[] parameterValue = Parameters.instance().getRequestParameters().get("ma");
        if (parameterValue != null && parameterValue.length > 0)
        {
            MemberAnswerEntity ma = new MemberAnswerEntity();
            ma.setMember(JedLab.instance().getCurrentUser());
            ma.setQuestion(this.question);
            ma.setCorrect(isAccomplished(Arrays.asList(parameterValue)));
            getSession().persist(ma);
            getSession().flush();
        }
        Map<String, Object> params = new HashMap<>();
        // params.put("sequence", getSequence());
        params.put("courseId", getCourseId());
        WebContext.instance().redirectIt(false, false, params);

    }

    @Transactional
    private boolean isAccomplished(List<String> userAnswers)
    {
        List<AnswerEntity> correctAnswers = getSession()
                .createQuery("select a from AnswerEntity a where a.correct = true and a.question.id = :qid")
                .setParameter("qid", this.question.getId()).list();
        List<AnswerEntity> alist = new ArrayList<>(correctAnswers);
        for (String ua : userAnswers)
        {
            for (AnswerEntity answerEntity : correctAnswers)
            {
                if (answerEntity.getValue().equals(ua))
                    alist.remove(answerEntity);
            }
        }
        return alist.size() == 0;
    }
    
    
    public boolean isGoodLevel()
    {
        return (getMemberCorrectAnswer().longValue() > (getQuestionCount().longValue() / 2)) && (getMemberCorrectAnswer().longValue() < getQuestionCount().longValue());
    }
    
    public boolean isBadLevel()
    {
        return getMemberCorrectAnswer().longValue() <= (getQuestionCount().longValue() / 2);
    }
    
    public boolean isAnswerAll()
    {
        return getMemberCorrectAnswer().longValue() == getQuestionCount().longValue(); 
    }
    
    @Transactional
    public String startOver()
    {
        CourseQuestion currentQuestion = (CourseQuestion) getSession()
                .createQuery(
                        "select mae.question from  MemberAnswerEntity mae where mae.member.id = :memId and mae.question.id in (select q.id from CourseQuestion q where q.course.id = :courseId) order by mae.createdDate asc")
                .setParameter("memId", JedLab.instance().getCurrentUserId()).setParameter("courseId", getCourseId()).setMaxResults(1).uniqueResult();
        Period p = new Period(currentQuestion.getCreatedDate().getTime(), new Date().getTime(), PeriodType.hours());
        if(p.getHours() <= 5)
        {
            getStatusMessages().addFromResourceBundle(Severity.WARN,"Try_Later");
            return null;
        }
        Query query = getSession().createQuery("delete from MemberAnswerEntity me where me.member.id = :memId and me.question.id in (select cq.id from CourseQuestion cq where cq.course.id = :courseId)");
        query.setParameter("memId", JedLab.instance().getCurrentUserId());
        query.setParameter("courseId", getCourseId());
        query.executeUpdate();
        getSession().clear();
        return "startOver";
    }
}
