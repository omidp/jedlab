package com.jedlab.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "story_bookmark")
@Entity
public class StoryBookmark implements Serializable
{

    @EmbeddedId
    @AttributeOverrides({ @AttributeOverride(name = "memberId", column = @Column(name = "member_id", nullable = false)),
            @AttributeOverride(name = "storyId", column = @Column(name = "story_id", nullable = false)) })
    @NotNull
    private StoryBookmarkId storyBookmarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, insertable = false, updatable = false)
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false, insertable = false, updatable = false)
    @NotNull
    private Story story;

    public StoryBookmarkId getStoryBookmarkId()
    {
        return storyBookmarkId;
    }

    public void setStoryBookmarkId(StoryBookmarkId storyBookmarkId)
    {
        this.storyBookmarkId = storyBookmarkId;
    }

    public Member getMember()
    {
        return member;
    }

    public void setMember(Member member)
    {
        this.member = member;
    }

    public Story getStory()
    {
        return story;
    }

    public void setStory(Story story)
    {
        this.story = story;
    }

}
