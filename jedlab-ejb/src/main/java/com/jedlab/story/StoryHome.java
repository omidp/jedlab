package com.jedlab.story;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.tika.exception.TikaException;
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
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.persistence.PersistenceContexts;
import org.xml.sax.SAXException;

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
import com.jedlab.tika.parser.HtmlContentParser;
import com.jedlab.tika.parser.HtmlContentParser.ContentParser;

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

    @Transactional
    public void updateViewCount()
    {
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        getEntityManager().createQuery("update Story s set s.viewCount = (s.viewCount+1)  where s.uuid = :uuid").setParameter("uuid", getUuid()).executeUpdate();        
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
    public Long publishContent(String mdcontent, String storyId, String storyTitle, boolean commentEnabled) throws IOException, SAXException, TikaException
    {
        Story story = createStory(storyId, storyTitle);
        story.setCommentEnabled(commentEnabled);
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
    public Long draftContent(String mdcontent, String storyId, String storyTitle, boolean commentEnabled) throws IOException, SAXException, TikaException
    {
        Story story = createStory(storyId, storyTitle);
        story.setCommentEnabled(commentEnabled);
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

    private void saveContent(Long uid, String mdcontent, Story story) throws IOException, SAXException, TikaException
    {
        Path fpath = null;
        if(StringUtil.isEmpty(story.getFilePath()))
        {
            String storyLocation = Env.getStoryLocation() + uid + Env.FILE_SEPARATOR + RandomStringUtils.randomNumeric(5);
            Path path = Paths.get(storyLocation);
            Files.createDirectories(path);
            String filePath = storyLocation + Env.FILE_SEPARATOR + "1.md";
            fpath = Paths.get(filePath);
            Files.createFile(fpath);
            story.setFilePath(filePath);
        }
        else
        {
            fpath = Paths.get(story.getFilePath());
            Files.deleteIfExists(fpath);
            Files.createFile(fpath);
        }
        HtmlMarkdownHolder holder = (HtmlMarkdownHolder) ServletLifecycle.getServletContext().getAttribute(
                HtmlMarkdownProcessor.MARKDOWN);
        Node node = holder.getParser().parse(StringUtil.escapeJavascript(mdcontent));
        String render = holder.getRenderer().render(node);
        ContentParser cp = HtmlContentParser.instance();
        cp.parse(render);
        String content = cp.handler().toString();
        if(content.length() > 400)
            story.setContent(content.substring(0, 400));
        else
            story.setContent(content);
        Files.write(fpath, mdcontent.getBytes("UTF-8"));
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
        else
            return "removed";
        getEntityManager().createQuery("delete from StoryBookmark sb where sb.story.id = :stId").setParameter("stId", getId()).executeUpdate();
        super.remove();        
        return "removed";
    }
    
    @Transactional
    public String removeByOwner()
    {
        String sidParam = WebUtil.getParameterValue("storyId");
        if (StringUtil.isNotEmpty(sidParam))
            setId(Long.parseLong(sidParam));
        else
            return "removed";
        if(getInstance().isOwner() == false)
            throw new ErrorPageExceptionHandler("invalid owner");
        getEntityManager().createQuery("delete from StoryBookmark sb where sb.story.id = :stId").setParameter("stId", getId()).executeUpdate();
        super.remove();        
        return "removed";
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
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uuid", getUuid());
            WebContext.instance().redirectIt(true, false, params);
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
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uuid", getUuid());
            WebContext.instance().redirectIt(true, false, params);
        }
    }
    
    private String currentView;
    
    public String getCurrentView()
    {
        if (currentView != null)
            return currentView;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String viewId = Pages.getCurrentViewId();
        String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext, Pages.getCurrentViewId());
        url = Pages.instance().encodeScheme(viewId, facesContext, url);
        url = url.substring(0, url.lastIndexOf("/") + 1);
        currentView = url + String.format("story/%d", getUuid());
        return currentView;
    }

}
