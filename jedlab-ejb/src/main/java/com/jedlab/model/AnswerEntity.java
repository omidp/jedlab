package com.jedlab.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Table(name = "question_answers")
@Entity
public class AnswerEntity extends BasePO
{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_question_id")
    private CourseQuestion question;

    @Column(name = "c_value")
    private String value;

    @Column(name = "is_correct")
    @Type(type="yes_no")
    private boolean correct;

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
