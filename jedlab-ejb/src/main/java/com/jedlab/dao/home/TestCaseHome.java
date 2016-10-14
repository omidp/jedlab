package com.jedlab.dao.home;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityHome;

import com.jedlab.framework.PageExceptionHandler;
import com.jedlab.framework.StringUtil;
import com.jedlab.model.Question;
import com.jedlab.model.TestCase;

@Name("testCaseHome")
@Scope(ScopeType.CONVERSATION)
public class TestCaseHome extends EntityHome<TestCase>
{

    private String inputParams;

    private Long questionId;

    private Question question;

    public Question getQuestion()
    {
        return question;
    }

    public Long getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(Long questionId)
    {
        this.questionId = questionId;
    }

    public String getInputParams()
    {
        return inputParams;
    }

    public void setInputParams(String inputParams)
    {
        this.inputParams = inputParams;
    }

    public void setTestCaseId(Long id)
    {
        setId(id);
    }

    public Long getTestCaseId()
    {
        return (Long) getId();
    }

    @Override
    protected TestCase createInstance()
    {
        TestCase tc = new TestCase();
        return tc;
    }

    public void load()
    {
        question = getEntityManager().find(Question.class, getQuestionId());
        if(isIdDefined())
        {
            StringBuilder sb = new StringBuilder();
            String[] pars = getInstance().getInputParams();
            for (int i = 0; i < pars.length; i++)
            {
                sb.append(pars[i]).append("\r\n");
            }
            setInputParams(sb.toString());
        }            
    }

    private void wire()
    {
        if (StringUtil.isEmpty(getInputParams()))
            throw new PageExceptionHandler("input param can not be null");
        getInstance().setInputParams(getInputParams().trim().split("\\r?\\n"));
        if(question == null)
            throw new PageExceptionHandler("question can not be null");
        getInstance().setQuestion(question);
    }

    public boolean isWired()
    {
        return true;
    }

    public TestCase getDefinedInstance()
    {
        return isIdDefined() ? getInstance() : null;
    }

    @Override
    public String persist()
    {
        wire();
        return super.persist();
    }

    @Override
    public String update()
    {
        wire();
        return super.update();
    }

}
