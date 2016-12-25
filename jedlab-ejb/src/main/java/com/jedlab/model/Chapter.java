package com.jedlab.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.omidbiz.core.axon.internal.IgnoreElement;

import com.jedlab.framework.DateUtil;

@Entity
@Table(name = "chapter", schema = "public")
public class Chapter extends BasePO implements Comparable<Chapter>
{

    @Column(name = "name")
    private String name;

    @Column(name = "duration")
    @Temporal(TemporalType.TIMESTAMP)
    private Date duration;

    @Column(name = "file_url")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "created_date", updatable = false, insertable = false, columnDefinition = " timestamp without time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    private Date createdDate;

    @Column(name = "c_sequence")
    private Integer sequence;

    @Transient
    private boolean registered;

    @Transient
    private boolean viewed;

    @Transient
    private boolean canDownload;

    @Transient
    private boolean paid;

    @IgnoreElement
    public boolean isPaid()
    {
        return paid;
    }

    public void setPaid(boolean paid)
    {
        this.paid = paid;
    }

    @IgnoreElement
    public boolean isCanDownload()
    {
        return canDownload;
    }

    public void setCanDownload(boolean canDownload)
    {
        this.canDownload = canDownload;
    }

    public Integer getSequence()
    {
        return sequence;
    }

    public void setSequence(Integer sequence)
    {
        this.sequence = sequence;
    }

    @IgnoreElement
    public boolean isViewed()
    {
        return viewed;
    }

    public void setViewed(boolean viewed)
    {
        this.viewed = viewed;
    }

    @IgnoreElement
    public boolean isRegistered()
    {
        return registered;
    }

    public void setRegistered(boolean registered)
    {
        this.registered = registered;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Date getDuration()
    {
        return duration;
    }

    public void setDuration(Date duration)
    {
        this.duration = duration;
    }

    @IgnoreElement
    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Course getCourse()
    {
        return course;
    }

    public void setCourse(Course course)
    {
        this.course = course;
    }

    @Transient
    public String getDurationWithformat()
    {
        if (getDuration() != null)
            return DateUtil.getDuration(getDuration());
        return "";
    }

    @Override
    public int compareTo(Chapter o)
    {
        if (getSequence() == null)
            return 0;
        return getSequence().compareTo(o.getSequence());
    }

}
