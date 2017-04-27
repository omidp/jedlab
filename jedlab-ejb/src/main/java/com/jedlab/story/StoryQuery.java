package com.jedlab.story;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Chapter;
import com.jedlab.model.Comment;
import com.jedlab.model.Course;
import com.jedlab.model.Story;

@Name("storyQuery")
@Scope(ScopeType.CONVERSATION)
public class StoryQuery extends PagingEntityQuery<Comment>
{

    private static final String EJBQL = "select s from Story s  LEFT OUTER JOIN s.member m ";

    private static final String[] RESTRICTIONS = { "s.id = #{storyQuery.story.id}" };

    Story story = new Story();

    public StoryQuery()
    {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        // setOrderColumn("memoType");
        setOrderColumn("c.createdDate");
        setOrderDirection("desc");
        setMaxResults(100);
    }

    public Story getStory()
    {
        return story;
    }

}
