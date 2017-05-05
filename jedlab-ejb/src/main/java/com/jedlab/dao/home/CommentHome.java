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
import com.jedlab.model.Course;
import com.jedlab.model.Member;
import com.jedlab.model.StoryComment;

@Name("commentHome")
@Scope(ScopeType.CONVERSATION)
public class CommentHome extends EntityHome<Comment>
{

    @Override
    protected Comment createInstance()
    {
        return new Comment();
    }

    public void load()
    {
        getInstance();
    }

    @Override
    public String persist()
    {
        String courseId = WebUtil.getParameterValue("courseId");
        if (StringUtil.isEmpty(courseId))
            return null;
        Comment reply = null;
        String replyId = WebUtil.getParameterValue("replyId");
        if (StringUtil.isNotEmpty(replyId))
        {
            reply = new Comment();
            reply.setId(Long.parseLong(replyId));
            getInstance().setReply(reply);
        }
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        Member member = new Member();
        member.setId(uid);
        getInstance().setMember(member);
        //
        Course c = new Course();
        c.setId(Long.parseLong(courseId));
        getInstance().setCourse(c);
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
        List<Comment> resultList = getEntityManager().createQuery("select c from Comment c where c.id = :cmId AND c.member.id = :memId")
                .setParameter("cmId", Long.parseLong(cmId)).setParameter("memId", uid).getResultList();
        for (Comment comment : resultList)
        {
            deletereplies(comment);
            getEntityManager().remove(comment);
        }
        getEntityManager().flush();
        return "removed";
    }
    
    private void deletereplies(Comment cm)
    {
        List<Comment> replies = cm.getReplies();
        if(CollectionUtil.isNotEmpty(replies))
        {
            for (Comment reply : replies)
            {
                deletereplies(reply);
                getEntityManager().remove(reply);
            }
        }
    }


}
