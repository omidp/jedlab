package com.jedlab.story;

import java.util.Arrays;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Story;
import com.jedlab.model.StoryBookmark;

@Name("storyBookmarkQuery")
@Scope(ScopeType.CONVERSATION)
public class StoryBookmarkQuery extends PagingEntityQuery<StoryBookmark>
{

    private static final String EJBQL = "select sb from StoryBookmark sb LEFT JOIN  sb.member m  LEFT JOIN sb.story s";
    
    private static final String[] RESTRICTIONS = { 
        "s.id = #{storyBookmarkQuery.story.id}"
        ,"m.id = #{jedLab.currentUserId}"};
    
    private Story story = new Story();

    public Story getStory()
    {
        return story;
    }

    public StoryBookmarkQuery()
    {
        setMaxResults(45);
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    }

}
