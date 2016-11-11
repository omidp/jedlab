package com.jedlab.model;

import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
import com.jedlab.framework.StringUtil;

@Entity
@Table(name = "gist", schema = "public")
public class Gist extends BasePO
{

    @Column(name = "file_name", nullable = false)
    @NotNull
    private String fileName;

    @Column(name = "content", nullable = false)
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    @Length(min = 2)
    private String content;
    
    @Column(name = "orig_content", nullable = false)
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    @Length(min = 2)
    private String origContent;
    
    @Column(name = "short_content", nullable = false)
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    @Length(min = 2)
    private String shortContent;

    @Column(name = "is_private")
    private boolean privateGist;

    @Column(name = "created_date", updatable = false, insertable = false, columnDefinition = " timestamp with time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    @OrderBy
    private Date createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "description")
    private String description;

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean isPrivateGist()
    {
        return privateGist;
    }

    public void setPrivateGist(boolean privateGist)
    {
        this.privateGist = privateGist;
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
    
    @Transient
    public String getSocialDate()
    {
        PrettyTime p = new PrettyTime(new Locale("fa", "IR"));
        return p.format(getCreatedDate());
    }

    public String getShortContent()
    {
        return shortContent;
    }

    public void setShortContent(String shortContent)
    {
        this.shortContent = shortContent;
    }

    public String getOrigContent()
    {
        return origContent;
    }

    public void setOrigContent(String origContent)
    {
        this.origContent = origContent;
    }

    @Transient
    public boolean isOwner()
    {
        return member != null && member.getId() == Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
    }

}
