package com.jedlab.model;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.jboss.seam.contexts.Contexts;
import org.ocpsoft.prettytime.PrettyTime;

import com.jedlab.action.Constants;
import com.jedlab.framework.StringUtil;

@Table(name = "podcasts")
@Entity
@Deprecated
public class Podcast extends BasePO
{

    @Column(name = "created_date", updatable = false, insertable = false, columnDefinition = " timestamp with time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    @OrderBy(value = "DESC")
    private Date createdDate;

    @Column(name = "file_url", nullable = false)
    @NotNull
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "image", length = 2147483647)
    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "description", nullable = true)
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Column(name = "view_count", nullable = false, columnDefinition = " bigint DEFAULT 0 ")
    private long viewCount;

    public long getViewCount()
    {
        return viewCount;
    }

    public void setViewCount(long viewCount)
    {
        this.viewCount = viewCount;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public byte[] getImage()
    {
        return image;
    }

    public void setImage(byte[] image)
    {
        this.image = image;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

    public Member getMember()
    {
        return member;
    }

    public void setMember(Member member)
    {
        this.member = member;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean isPublished()
    {
        return published;
    }

    public void setPublished(boolean published)
    {
        this.published = published;
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
    public boolean isNew()
    {
        return getId() == null;
    }

    @Transient
    public boolean getHasImage()
    {
        return getImage() != null && getImage().length > 0;
    }

    @PrePersist
    public void prePersist()
    {
        setUuid(UUID.randomUUID().toString());
    }

    @Transient
    public String getShortTitle()
    {
        if (StringUtil.isNotEmpty(title))
        {
            if (title.length() > 30)
                return title.substring(0, 30).concat(" ...");
        }
        return title;
    }

    @Transient
    public String getViewCountFormatted()
    {
        return StringUtil.formatViewCount(getViewCount());
    }

}
