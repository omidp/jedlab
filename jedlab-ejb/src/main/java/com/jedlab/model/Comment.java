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

@Table(name = "comments")
@Entity
public class Comment extends BasePO
{

    @Column(name = "created_date", updatable = false, insertable = false, columnDefinition = " timestamp with time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    @OrderBy(value="DESC")
    private Date createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "content", nullable = false)
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    @Length(min = 2)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Comment reply;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reply", cascade = CascadeType.REMOVE)
    private List<Comment> replies = new ArrayList<Comment>();

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

    public Course getCourse()
    {
        return course;
    }

    public void setCourse(Course course)
    {
        this.course = course;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Member getMember()
    {
        return member;
    }

    public void setMember(Member member)
    {
        this.member = member;
    }

    public Comment getReply()
    {
        return reply;
    }

    public void setReply(Comment reply)
    {
        this.reply = reply;
    }

    public List<Comment> getReplies()
    {
        return replies;
    }

    public void setReplies(List<Comment> replies)
    {
        this.replies = replies;
    }

    public boolean isHasReplies()
    {
        return CollectionUtil.isNotEmpty(replies);
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
        if(member == null || currentLogginId == null)
            return false;
        return member.getId().longValue() == ((Long)currentLogginId).longValue();
    }

}
