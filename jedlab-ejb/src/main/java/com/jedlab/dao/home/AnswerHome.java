package com.jedlab.dao.home;

import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import com.jedlab.framework.PageExceptionHandler;
import com.jedlab.model.AnswerEntity;
import com.jedlab.model.Course;
import com.jedlab.model.CourseQuestion;
import com.jedlab.model.Question;

@Name("answerHome")
@Scope(ScopeType.CONVERSATION)
public class AnswerHome extends EntityHome<AnswerEntity>
{

    @RequestParameter
    private Long questionId;

    private CourseQuestion question;

    public Long getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(Long questionId)
    {
        this.questionId = questionId;
    }

    public CourseQuestion getQuestion()
    {
        return question;
    }

    public void setAnswerId(Long id)
    {
        setId(id);
    }

    public Long getAnswerId()
    {
        return (Long) getId();
    }

    public void load()
    {
        try
        {
            this.question = (CourseQuestion) getEntityManager().createQuery("select cq from CourseQuestion cq left join fetch cq.course c where cq.id = :id")
                    .setParameter("id", getQuestionId()).setMaxResults(1).getSingleResult();
        }
        catch (NoResultException e)
        {
        }
    }

    private void wire()
    {
        if (getQuestionId() == null || getQuestion() == null)
            throw new PageExceptionHandler("course can not be null");
        getInstance().setQuestion(getQuestion());
        Integer seq = 0;
        try
        {
            seq = (Integer) getEntityManager()
                    .createQuery("select a.sequence from AnswerEntity a where a.question.id = :qId order by a.createdDate desc")
                    .setParameter("qId", getQuestionId()).setMaxResults(1).getSingleResult();
        }
        catch (Exception e)
        {
        }
        getInstance().setSequence(seq + 10);
    }

    @Override
    protected AnswerEntity createInstance()
    {
        AnswerEntity cq = new AnswerEntity();
        cq.setQuestion(getQuestion());
        return cq;
    }

    public boolean isWired()
    {
        return true;
    }

    public AnswerEntity getDefinedInstance()
    {
        return isIdDefined() ? getInstance() : null;
    }

    @Override
    public String persist()
    {
        wire();
        return super.persist();
    }

    @Override
    public String update()
    {
        wire();
        return super.update();
    }

}
