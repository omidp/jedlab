package com.jedlab.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Basic;
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

import org.hibernate.annotations.Type;
import org.jboss.seam.contexts.Contexts;
import org.ocpsoft.prettytime.PrettyTime;
import org.omidbiz.core.axon.internal.IgnoreElement;

import com.jedlab.action.Constants;
import com.jedlab.framework.StringUtil;

@Table(name = "pages")
@Entity
public class Page extends BasePO
{

    @Column(name = "created_date", updatable = false, insertable = false, columnDefinition = " timestamp with time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    @OrderBy(value = "DESC")
    private Date createdDate;

    @Column(name = "updated_date", updatable = false, insertable = false, columnDefinition = " timestamp with time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.ALWAYS)
    private Date updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "is_publish", nullable = false)
    private boolean published;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "image", length = 2147483647)
    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    @Column(name = "process_id")
    private Long processId;

    @Column(name = "view_count")
    private long viewCount;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "page")
    Set<PageBlock> blocks = new HashSet<>(0);
    
    @Transient
    private PageStatisticsView statistic;
    
    

    public PageStatisticsView getStatistic()
    {
        return statistic;
    }

    public void setStatistic(PageStatisticsView statistic)
    {
        this.statistic = statistic;
    }

    public long getViewCount()
    {
        return viewCount;
    }

    public void setViewCount(long viewCount)
    {
        this.viewCount = viewCount;
    }

    public Long getProcessId()
    {
        return processId;
    }

    public void setProcessId(Long processId)
    {
        this.processId = processId;
    }

    public boolean isPublished()
    {
        return published;
    }

    public void setPublished(boolean published)
    {
        this.published = published;
    }

    public Set<PageBlock> getBlocks()
    {
        return blocks;
    }

    public void setBlocks(Set<PageBlock> blocks)
    {
        this.blocks = blocks;
    }

    public Date getUpdatedDate()
    {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate)
    {
        this.updatedDate = updatedDate;
    }

    public byte[] getImage()
    {
        return image;
    }

    public void setImage(byte[] image)
    {
        this.image = image;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

    @IgnoreElement
    public Member getMember()
    {
        return member;
    }

    public void setMember(Member member)
    {
        this.member = member;
    }

    @Transient
    public String getSocialDate()
    {
        PrettyTime p = new PrettyTime(new Locale("fa", "IR"));
        return p.format(getCreatedDate());
    }

    @Transient
    public boolean isOwner()
    {
        Object currentLogginId = Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        if (member == null || currentLogginId == null)
            return false;
        return member.getId().longValue() == ((Long) currentLogginId).longValue();
    }

    @Transient
    public boolean isInProgress()
    {
        return getProcessId() != null;
    }

    @Transient
    public boolean getHasImage()
    {
        return getImage() != null && getImage().length > 0;
    }
    
    @Transient
    public String getViewCountFormatted()
    {
        return StringUtil.formatViewCount(getViewCount());
    }

}
