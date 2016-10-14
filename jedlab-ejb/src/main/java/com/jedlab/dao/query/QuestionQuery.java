package com.jedlab.dao.query;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.PagingEntityQuery;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;
import com.jedlab.model.Question;

@Name("questionQuery")
@Scope(ScopeType.CONVERSATION)
public class QuestionQuery extends PagingEntityQuery<Question>
{

    private static final String EJBQL = "select q from Question q";

    private static final String[] RESTRICTIONS = { };

    Question question = new Question();


    public QuestionQuery()
    {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
//        setOrderColumn("createdDate");
//        setOrderDirection("desc");
        setMaxResults(15);
    }


    public Question getQuestion()
    {
        return question;
    }

    

}
