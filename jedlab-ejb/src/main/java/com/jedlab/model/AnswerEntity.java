package com.jedlab.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Table(name = "question_answers")
@Entity
public class AnswerEntity extends BasePO
{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_question_id")
    private CourseQuestion question;

    @Column(name = "created_date", updatable = false, insertable = false, columnDefinition = " timestamp with time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    @OrderBy
    private Date createdDate;

    @Column(name = "c_value")
    private String value;

    @Column(name = "is_correct")
    @Type(type = "yes_no")
    private boolean correct;

    @Column(name = "c_sequence")
    private Integer sequence;

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

    public Integer getSequence()
    {
        return sequence;
    }

    public void setSequence(Integer sequence)
    {
        this.sequence = sequence;
    }

    public boolean isCorrect()
    {
        return correct;
    }

    public void setCorrect(boolean correct)
    {
        this.correct = correct;
    }

    public CourseQuestion getQuestion()
    {
        return question;
    }

    public void setQuestion(CourseQuestion question)
    {
        this.question = question;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

}
