package com.jedlab.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Table(name = "member_statistic_view")
@Entity
@Immutable
@NamedQuery(name = MemberStatisticsView.FIND_BY_MEMBER_ID, query = "select msv from MemberStatisticsView msv where msv.id = :memberId")
public class MemberStatisticsView implements Serializable
{

    public static final String FIND_BY_MEMBER_ID = "msv.findByMemberId";

    @Id
    @Column(name = "member_id")
    private long id;

    @Column(name = "course_count")
    private Long courseCount;

    @Column(name = "code_count")
    private Long gistCount;

    @Column(name = "story_count")
    private Long storyCount;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Long getCourseCount()
    {
        return courseCount;
    }

    public void setCourseCount(Long courseCount)
    {
        this.courseCount = courseCount;
    }

    public Long getGistCount()
    {
        return gistCount;
    }

    public void setGistCount(Long gistCount)
    {
        this.gistCount = gistCount;
    }

    public Long getStoryCount()
    {
        return storyCount;
    }

    public void setStoryCount(Long storyCount)
    {
        this.storyCount = storyCount;
    }

}
