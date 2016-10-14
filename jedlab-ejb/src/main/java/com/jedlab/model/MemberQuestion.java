package com.jedlab.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "member_question")
public class MemberQuestion extends BasePO
{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    public enum QuestionStatus
    {
        FAILED, RESOLVED;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "question_status")
    private QuestionStatus status;

    public Member getMember()
    {
        return member;
    }

    public void setMember(Member member)
    {
        this.member = member;
    }

    public Question getQuestion()
    {
        return question;
    }

    public void setQuestion(Question question)
    {
        this.question = question;
    }

    public QuestionStatus getStatus()
    {
        return status;
    }

    public void setStatus(QuestionStatus status)
    {
        this.status = status;
    }

    // private String code;

}
