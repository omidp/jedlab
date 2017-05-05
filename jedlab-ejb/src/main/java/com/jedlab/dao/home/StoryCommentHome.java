package com.jedlab.dao.home;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;

import com.jedlab.action.Constants;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.WebUtil;
import com.jedlab.framework.jsf.FlashScope;
import com.jedlab.model.Comment;
import com.jedlab.model.StoryComment;
import com.jedlab.model.Course;
import com.jedlab.model.Member;
import com.jedlab.model.Story;

@Name("storyCommentHome")
@Scope(ScopeType.CONVERSATION)
public class StoryCommentHome extends EntityHome<StoryComment>
{

    @Override
    protected StoryComment createInstance()
    {
        return new StoryComment();
    }

    public void load()
    {
        getInstance();
    }

    @Override
    public String persist()
    {
        String storyId = WebUtil.getParameterValue("storyId");
        if (StringUtil.isEmpty(storyId))
            return null;
        StoryComment reply = null;
        String replyId = WebUtil.getParameterValue("replyId");
        if (StringUtil.isNotEmpty(replyId))
        {
            reply = new StoryComment();
            reply.setId(Long.parseLong(replyId));
            getInstance().setReply(reply);
        }
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        Member member = new Member();
        member.setId(uid);
        getInstance().setMember(member);
        //
        Story story = new Story();
        story.setId(Long.parseLong(storyId));
        getInstance().setStory(story);
        FlashScope.instance().addMessage(Severity.INFO, StatusMessage.getBundleMessage(getCreatedMessageKey(), ""));
        return super.persist();
    }

    @Override
    public String remove()
    {
        String cmId = WebUtil.getParameterValue("cmId");
        if (StringUtil.isEmpty(cmId))
        {
            return "removed";
        }
        FlashScope.instance().addMessage(Severity.INFO, StatusMessage.getBundleMessage("Deleted", ""));
        clearInstance();
        Long uid = (Long) getSessionContext().get(Constants.CURRENT_USER_ID);
        List<StoryComment> resultList = getEntityManager().createQuery("select c from StoryComment c where c.id = :cmId AND c.member.id = :memId")
                .setParameter("cmId", Long.parseLong(cmId)).setParameter("memId", uid).getResultList();
        for (StoryComment storyComment : resultList)
        {
            deletereplies(storyComment);
            getEntityManager().remove(storyComment);
        }
        getEntityManager().flush();
        return "removed";
    }
    
    
    private void deletereplies(StoryComment cm)
    {
        List<StoryComment> replies = cm.getReplies();
        if(CollectionUtil.isNotEmpty(replies))
        {
            for (StoryComment reply : replies)
            {
                deletereplies(reply);
                getEntityManager().remove(reply);
            }
        }
    }

}
