package com.jedlab.model;

import java.io.Serializable;
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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.jboss.seam.contexts.Contexts;
import org.ocpsoft.prettytime.PrettyTime;
import org.omidbiz.core.axon.internal.IgnoreElement;

import com.jedlab.action.Constants;
import com.jedlab.framework.StringUtil;

@Table(name = "page_statistic_view")
@Entity
@Immutable
public class PageStatisticsView implements Serializable
{

    @Id
    @Column(name = "page_id")
    private long id;

    @Column(name = "page_view_count")
    private long pageViewCount;

    @Column(name = "block_count")
    private long blockCount;

    @Column(name = "curate_count")
    private long curateCount;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public long getPageViewCount()
    {
        return pageViewCount;
    }

    public void setPageViewCount(long pageViewCount)
    {
        this.pageViewCount = pageViewCount;
    }

    public long getBlockCount()
    {
        return blockCount;
    }

    public void setBlockCount(long blockCount)
    {
        this.blockCount = blockCount;
    }

    public long getCurateCount()
    {
        return curateCount;
    }

    public void setCurateCount(long curateCount)
    {
        this.curateCount = curateCount;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }
    
    @Transient
    public String getPageViewCountFormatted()
    {
        return StringUtil.formatViewCount(getPageViewCount());
    }
    
    

}
