package com.jedlab.dao.query;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;

@Name("chapterQuery")
@Scope(ScopeType.CONVERSATION)
public class ChapterQuery extends PagingEntityQuery<Chapter>
{

    private static final String EJBQL = "select c from Chapter c LEFT OUTER JOIN c.course course";

    private static final String[] RESTRICTIONS = { "lower(c.name) like lower(concat('%',concat(#{chapterQuery.chapter.name},'%')))",
            "c.course.id = #{chapterQuery.course.id}", "c.id = #{chapterQuery.chapter.id}"};

    Chapter chapter = new Chapter();
    
    Course course = new Course();

    public ChapterQuery()
    {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        // setOrderColumn("memoType");
        setMaxResults(15);
    }

    public Chapter getChapter()
    {
        return chapter;
    }

    public Course getCourse()
    {
        return course;
    }
    
    

}
