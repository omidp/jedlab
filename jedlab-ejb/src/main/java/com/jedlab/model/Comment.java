package com.jedlab.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import org.hibernate.annotations.Type;

import com.jedlab.framework.CollectionUtil;

@Table(name = "comments")
@Entity
public class Comment extends BasePO
{

    @Column(name = "created_date", updatable = false, insertable = false, columnDefinition = " timestamp with time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    @OrderBy
    private Date createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "content")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_id")
    private Comment comment;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "comment")
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

    public Comment getComment()
    {
        return comment;
    }

    public void setComment(Comment comment)
    {
        this.comment = comment;
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

}
