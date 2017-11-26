package com.jedlab.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Table(name = "user_answers")
@Entity
public class UserAnswerEntity extends BasePO
{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_question_id")
    private CourseQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "c_value")
    private String value;

    public Member getMember()
    {
        return member;
    }

    public void setMember(Member member)
    {
        this.member = member;
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
