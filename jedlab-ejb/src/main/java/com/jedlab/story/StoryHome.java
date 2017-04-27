package com.jedlab.story;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.AuthorizationException;

import com.jedlab.Env;
import com.jedlab.action.Constants;
import com.jedlab.model.Gist;
import com.jedlab.model.Story;

@Name("storyHome")
@Scope(ScopeType.CONVERSATION)
public class StoryHome extends EntityHome<Story>
{

    
    public Long getStoryId()
    {
        return (Long) getId();
    }

    public void setStoryId(Long storyId)
    {
        setId(storyId);
    }

    
    public void load()
    {
        if (isIdDefined())
        {
            Story story = getInstance();
        }
    }
    
    private void wire()
    {
        
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

    public String getMdServer()
    {
        return Env.getMdServer();
    }
    
}
