package com.jedlab.story;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.commonmark.node.Node;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.framework.EntityHome;

import com.jedlab.Env;
import com.jedlab.action.Constants;
import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.framework.StringUtil;
import com.jedlab.model.Member;
import com.jedlab.model.Story;
import com.jedlab.story.HtmlMarkdownProcessor.HtmlMarkdownHolder;

@Name("storyHome")
@Scope(ScopeType.CONVERSATION)
public class StoryHome extends EntityHome<Story>
{

    @In
    Session hibernateSession;

    private String htmlResult;

    public Long getStoryId()
    {
        return (Long) getId();
    }

    public void setStoryId(Long storyId)
    {
        setId(storyId);
    }

    public String getHtmlResult()
    {
        return htmlResult;
    }

    public void setHtmlResult(String htmlResult)
    {
        this.htmlResult = htmlResult;
    }

    public void load()
    {
        if (isIdDefined())
        {
            Story story = (Story) hibernateSession.createCriteria(Story.class, "s").add(Restrictions.idEq(getStoryId())).createCriteria("member", "m", Criteria.LEFT_JOIN)
                    .uniqueResult();
            if(story == null)
                throw new ErrorPageExceptionHandler("story is null");
            setInstance(story);
            HtmlMarkdownHolder holder = (HtmlMarkdownHolder) ServletLifecycle.getServletContext().getAttribute(
                    HtmlMarkdownProcessor.MARKDOWN);
            try
            {
                Path path = Paths.get(story.getFilePath());
                String content = new String(Files.readAllBytes(path));
                Node node = holder.getParser().parse(StringUtil.escapeJavascript(content));
                htmlResult = holder.getRenderer().render(node);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
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

    @Transactional
    public Long publishContent(String mdcontent, String storyId) throws IOException
    {
        Story story = createStory(storyId);
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        saveContent(uid, mdcontent, story);
        if (story.isNew())
            getEntityManager().persist(story);
        getEntityManager().createQuery("update Story s set s.published = true where s.id = :sid").setParameter("sid", story.getId())
                .executeUpdate();
        getEntityManager().flush();
        return story.getId();
    }

    @Transactional
    public Long draftContent(String mdcontent, String storyId) throws IOException
    {
        Story story = createStory(storyId);
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        saveContent(uid, mdcontent, story);
        if (story.isNew())
            getEntityManager().persist(story);
        getEntityManager().createQuery("update Story s set s.published = false where s.id = :sid").setParameter("sid", story.getId())
                .executeUpdate();
        getEntityManager().flush();
        return story.getId();
    }

    private Story createStory(String storyIdParam)
    {
        Story story = new Story();
        if (StringUtil.isNotEmpty(storyIdParam))
            story = getEntityManager().find(Story.class, Long.parseLong(storyIdParam));
        Member m = new Member();
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        m.setId(uid);
        story.setMember(m);
        return story;
    }

    private void saveContent(Long uid, String mdcontent, Story story) throws IOException
    {
        String storyLocation = Env.getStoryLocation() + uid + Env.FILE_SEPARATOR + RandomStringUtils.randomNumeric(5);
        Path path = Paths.get(storyLocation);
        Files.createDirectories(path);
        String filePath = storyLocation + Env.FILE_SEPARATOR + "1.md";
        Path fpath = Paths.get(filePath);
        Files.deleteIfExists(fpath);
        Files.createFile(fpath);
        Files.write(fpath, mdcontent.getBytes("UTF-8"));
        story.setFilePath(filePath);
    }

    public static StoryHome instance()
    {
        if (!Contexts.isConversationContextActive())
        {
            throw new IllegalStateException("No active conversation context");
        }

        StoryHome instance = (StoryHome) Component.getInstance(StoryHome.class, ScopeType.CONVERSATION);

        if (instance == null)
        {
            throw new IllegalStateException("No StoryHome could be created");
        }

        return instance;
    }

}
