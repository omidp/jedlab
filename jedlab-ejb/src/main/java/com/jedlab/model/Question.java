package com.jedlab.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.jboss.seam.contexts.Contexts;
import org.ocpsoft.prettytime.PrettyTime;

import com.jedlab.action.Constants;
import com.jedlab.framework.CollectionUtil;

@Table(name = "question")
@Entity
public class Question extends BasePO
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
    @Length(min = 2)
    private String description;

    @Column(name = "points")
    private double points;

    @Column(name = "attempt_count")
    private int attemptCount;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    private List<TestCase> testcases = new ArrayList<>(0);

    public List<TestCase> getTestcases()
    {
        return testcases;
    }

    public void setTestcases(List<TestCase> testcases)
    {
        this.testcases = testcases;
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

    public double getPoints()
    {
        return points;
    }

    public void setPoints(double points)
    {
        this.points = points;
    }

    public int getAttemptCount()
    {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount)
    {
        this.attemptCount = attemptCount;
    }

}
