package com.jedlab.story;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.commonmark.node.Node;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.persistence.PersistenceContexts;

import com.jedlab.Env;
import com.jedlab.action.Constants;
import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TxManager;
import com.jedlab.framework.WebContext;
import com.jedlab.framework.WebUtil;
import com.jedlab.model.Member;
import com.jedlab.model.Story;
import com.jedlab.model.StoryBookmark;
import com.jedlab.model.StoryBookmarkId;
import com.jedlab.story.HtmlMarkdownProcessor.HtmlMarkdownHolder;

@Name("storyHome")
@Scope(ScopeType.CONVERSATION)
public class StoryHome extends EntityHome<Story>
{

    @In
    Session hibernateSession;

    private String htmlResult;

    private String markdownContent;

    private byte[] uploadImage;

    private Integer fileSize;

    private String uuid;

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public byte[] getUploadImage()
    {
        return uploadImage;
    }

    public void setUploadImage(byte[] uploadImage)
    {
        this.uploadImage = uploadImage;
    }

    public Integer getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Integer fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getMarkdownContent()
    {
        return markdownContent;
    }

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

    public void load()
    {
        Story story = null;
        if (StringUtil.isNotEmpty(getUuid()))
        {
            story = (Story) hibernateSession.createCriteria(Story.class, "s").add(Restrictions.eq("s.uuid", getUuid()))
                    .createCriteria("member", "m", Criteria.LEFT_JOIN).uniqueResult();
        }
        if (isIdDefined() && story == null)
        {
            story = (Story) hibernateSession.createCriteria(Story.class, "s").add(Restrictions.idEq(getStoryId()))
                    .createCriteria("member", "m", Criteria.LEFT_JOIN).uniqueResult();
            if(story == null)
                throw new EntityNotFoundException("story not found");
            if(story.isOwner() == false)
                throw new ErrorPageExceptionHandler("invalid owner");
        }
        if (story != null)
        {
            setInstance(story);
            HtmlMarkdownHolder holder = (HtmlMarkdownHolder) ServletLifecycle.getServletContext().getAttribute(
                    HtmlMarkdownProcessor.MARKDOWN);
            try
            {
                Path path = Paths.get(story.getFilePath());
                String content = new String(Files.readAllBytes(path));
                this.markdownContent = content;
                Node node = holder.getParser().parse(StringUtil.escapeJavascript(content));
                this.htmlResult = holder.getRenderer().render(node);
            }
            catch (IOException e)
            {
                getLog().info("IOEXCEPTION");
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
    public Long publishContent(String mdcontent, String storyId, String storyTitle) throws IOException
    {
        Story story = createStory(storyId, storyTitle);
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
    public Long draftContent(String mdcontent, String storyId, String storyTitle) throws IOException
    {
        Story story = createStory(storyId, storyTitle);
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        saveContent(uid, mdcontent, story);
        if (story.isNew())
            getEntityManager().persist(story);
        getEntityManager().createQuery("update Story s set s.published = false where s.id = :sid").setParameter("sid", story.getId())
                .executeUpdate();
        getEntityManager().flush();
        return story.getId();
    }

    private Story createStory(String storyIdParam, String storyTitle)
    {
        Story story = new Story();
        if (StringUtil.isNotEmpty(storyIdParam))
            story = getEntityManager().find(Story.class, Long.parseLong(storyIdParam));
        Member m = new Member();
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        m.setId(uid);
        story.setMember(m);
        story.setTitle(storyTitle);
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

    @Transactional
    public String uploadImage()
    {
        if (getFileSize() != null && getFileSize() > 107371)
        {
            getStatusMessages().addFromResourceBundle(Severity.ERROR, "File_Size_Exceed");
        }
        else
        {
            String sidParam = WebUtil.getParameterValue("storyFileId");
            if (StringUtil.isNotEmpty(sidParam))
            {
                PersistenceContexts.instance().changeFlushMode(FlushModeType.MANUAL);
                TxManager.joinTransaction(getEntityManager());
                Story s = getEntityManager().find(Story.class, Long.parseLong(sidParam));
                if (s.isOwner())
                {
                    s.setImage(getUploadImage());
                    getEntityManager().flush();
                    getStatusMessages().addFromResourceBundle(Severity.INFO, "Story_updated");
                }
            }
        }
        return null;
    }

    @Override
    public String remove()
    {
        String sidParam = WebUtil.getParameterValue("storyId");
        if (StringUtil.isNotEmpty(sidParam))
            setId(Long.parseLong(sidParam));
        return super.remove();
    }

    @Transactional
    public void bookmark()
    {
        String storyParam = WebUtil.getParameterValue("storyId");
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        if (uid != null && StringUtil.isNotEmpty(storyParam))
        {
            TxManager.beginTransaction();
            TxManager.joinTransaction(getEntityManager());
            StoryBookmarkId id = new StoryBookmarkId(uid, Long.parseLong(storyParam));
            StoryBookmark bookmark = new StoryBookmark();
            bookmark.setStoryBookmarkId(id);
            Member m = new Member();
            m.setId(uid);
            bookmark.setMember(m);
            Story st = new Story();
            st.setId(Long.parseLong(storyParam));
            bookmark.setStory(st);
            getEntityManager().persist(bookmark);
            getEntityManager().flush();
            WebContext.instance().redirectIt(true, true);
        }
    }

    @Transactional
    public void unbookmark()
    {
        String storyParam = WebUtil.getParameterValue("storyId");
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        if (uid != null && StringUtil.isNotEmpty(storyParam))
        {
            TxManager.beginTransaction();
            TxManager.joinTransaction(getEntityManager());
            getEntityManager().createQuery("delete from StoryBookmark sb where sb.story.id = :stId and sb.member.id = :memId")
                    .setParameter("stId", Long.parseLong(storyParam)).setParameter("memId", uid).executeUpdate();
            getEntityManager().flush();
            WebContext.instance().redirectIt(true, true);
        }
    }

}
