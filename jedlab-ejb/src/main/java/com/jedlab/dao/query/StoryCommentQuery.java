package com.jedlab.dao.query;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Chapter;
import com.jedlab.model.Comment;
import com.jedlab.model.Course;
import com.jedlab.model.Story;

@Name("storyCommentQuery")
@Scope(ScopeType.CONVERSATION)
public class StoryCommentQuery extends PagingEntityQuery<Comment>
{

    private static final String EJBQL = "select c from StoryComment c LEFT OUTER JOIN c.story story LEFT OUTER JOIN c.member m where c.reply is null";

    private static final String[] RESTRICTIONS = { 
            "c.story.id = #{storyCommentQuery.story.id}"
            ,"c.story.id = #{storyHome.instance.id}"};
    
    Story story = new Story();
    
    public StoryCommentQuery()
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
