package com.jedlab.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import com.jedlab.model.enums.QuestionType;

@Table(name = "course_questions")
@Entity
public class CourseQuestion extends BasePO
{

    @Column(name = "created_date", updatable = false, insertable = false, columnDefinition = " timestamp with time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    @OrderBy
    private Date createdDate;

    @Column(name = "title")
    private String title;

    @Column(name = "description", nullable = false)
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "question_type")
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @Column(name = "c_sequence")
    private Integer sequence;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question", orphanRemoval = true)
    List<AnswerEntity> answers = new ArrayList<>(0);

    public List<AnswerEntity> getAnswers()
    {
        return answers;
    }

    public void setAnswers(List<AnswerEntity> answers)
    {
        this.answers = answers;
    }

    public Integer getSequence()
    {
        return sequence;
    }

    public void setSequence(Integer sequence)
    {
        this.sequence = sequence;
    }

    public QuestionType getQuestionType()
    {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType)
    {
        this.questionType = questionType;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Course getCourse()
    {
        return course;
    }

    public void setCourse(Course course)
    {
        this.course = course;
    }

    @PrePersist
    public void prePersist()
    {
        setCreatedDate(new Date());
    }
    
    @Transient
    public boolean isSingleQuestion()
    {
        return QuestionType.SINGLE.equals(getQuestionType());
    }
    
    @Transient
    public boolean isMultipleQuestion()
    {
        return QuestionType.MULTIPLE.equals(getQuestionType());
    }
    
    @Transient
    public boolean isBlankQuestion()
    {
        return QuestionType.BLANK.equals(getQuestionType());
    }

}
