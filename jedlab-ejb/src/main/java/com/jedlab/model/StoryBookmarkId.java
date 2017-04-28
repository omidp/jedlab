package com.jedlab.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class StoryBookmarkId implements Serializable
{

    @Column(name = "member_id", nullable = false)
    private long memberId;

    @Column(name = "story_id", nullable = false)
    private long storyId;

    public long getMemberId()
    {
        return memberId;
    }

    public void setMemberId(long memberId)
    {
        this.memberId = memberId;
    }

    public long getStoryId()
    {
        return storyId;
    }

    public void setStoryId(long storyId)
    {
        this.storyId = storyId;
    }

    public StoryBookmarkId()
    {
    }

    public StoryBookmarkId(long memberId, long storyId)
    {
        this.memberId = memberId;
        this.storyId = storyId;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (memberId ^ (memberId >>> 32));
        result = prime * result + (int) (storyId ^ (storyId >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StoryBookmarkId other = (StoryBookmarkId) obj;
        if (memberId != other.memberId)
            return false;
        if (storyId != other.storyId)
            return false;
        return true;
    }

}
