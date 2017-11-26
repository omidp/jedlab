package com.jedlab.dao.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityHome;

import com.jedlab.model.Question;

@Name("questionHome")
@Scope(ScopeType.CONVERSATION)
@Deprecated
public class QuestionHome extends EntityHome<Question>
{

    public void setQuestionId(Long id)
    {
        setId(id);
    }

    public Long getQuestionId()
    {
        return (Long) getId();
    }

    @Override
    protected Question createInstance()
    {
        Question q = new Question();
        return q;
    }

    public void load()
    {

    }

    private void wire()
    {

    }

    public boolean isWired()
    {
        return true;
    }

    public Question getDefinedInstance()
    {
        return isIdDefined() ? getInstance() : null;
    }

}
