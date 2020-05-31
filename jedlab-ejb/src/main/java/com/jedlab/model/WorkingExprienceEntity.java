package com.jedlab.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "working_exprience", schema = "public")
public class WorkingExprienceEntity extends BasePO
{

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member memebr;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @NotNull
    private CompanyEntity company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private PositionEntity position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private ContractEntity contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_id")
    private SalaryEntity salary;

    @Column(name = "created_date", updatable = false, insertable = false, columnDefinition = " timestamp with time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    @OrderBy(value = "DESC")
    private Date createdDate;

    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(name = "description")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "title")
    private String title;

    @Column(name = "approved")
    @Type(type = "yes_no")
    private boolean approved;

    @Column(name = "recommend")
    @Type(type = "yes_no")
    private boolean recommend;

    @Column(name = "show_title")
    @Type(type = "yes_no")
    private boolean showTitle;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "work_life_balance")
    private StartRating workLifeBalance;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "salary_benefit")
    private StartRating salaryBenefit;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "job_security")
    private StartRating jobSecurity;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "management")
    private StartRating management;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "culture")
    private StartRating culture;

    public enum StartRating
    {
        ONE, TWO, THREE, FOUR, FIVE;
    }
    
    

    public Member getMemebr()
    {
        return memebr;
    }

    public void setMemebr(Member memebr)
    {
        this.memebr = memebr;
    }

    public CompanyEntity getCompany()
    {
        return company;
    }

    public void setCompany(CompanyEntity company)
    {
        this.company = company;
    }

    public PositionEntity getPosition()
    {
        return position;
    }

    public void setPosition(PositionEntity position)
    {
        this.position = position;
    }

    public ContractEntity getContract()
    {
        return contract;
    }

    public void setContract(ContractEntity contract)
    {
        this.contract = contract;
    }

    public SalaryEntity getSalary()
    {
        return salary;
    }

    public void setSalary(SalaryEntity salary)
    {
        this.salary = salary;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Long getViewCount()
    {
        return viewCount;
    }

    public void setViewCount(Long viewCount)
    {
        this.viewCount = viewCount;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public boolean isApproved()
    {
        return approved;
    }

    public void setApproved(boolean approved)
    {
        this.approved = approved;
    }

    public boolean isRecommend()
    {
        return recommend;
    }

    public void setRecommend(boolean recommend)
    {
        this.recommend = recommend;
    }

    public boolean isShowTitle()
    {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle)
    {
        this.showTitle = showTitle;
    }

    public StartRating getWorkLifeBalance()
    {
        return workLifeBalance;
    }

    public void setWorkLifeBalance(StartRating workLifeBalance)
    {
        this.workLifeBalance = workLifeBalance;
    }

    public StartRating getSalaryBenefit()
    {
        return salaryBenefit;
    }

    public void setSalaryBenefit(StartRating salaryBenefit)
    {
        this.salaryBenefit = salaryBenefit;
    }

    public StartRating getJobSecurity()
    {
        return jobSecurity;
    }

    public void setJobSecurity(StartRating jobSecurity)
    {
        this.jobSecurity = jobSecurity;
    }

    public StartRating getManagement()
    {
        return management;
    }

    public void setManagement(StartRating management)
    {
        this.management = management;
    }

    public StartRating getCulture()
    {
        return culture;
    }

    public void setCulture(StartRating culture)
    {
        this.culture = culture;
    }

}
