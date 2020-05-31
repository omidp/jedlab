package com.jedlab.company;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

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
import com.jedlab.JedLab;
import com.jedlab.action.Constants;
import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.framework.RegexUtil;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TxManager;
import com.jedlab.framework.WebContext;
import com.jedlab.framework.WebUtil;
import com.jedlab.model.CompanyEntity;
import com.jedlab.model.Member;
import com.jedlab.model.Story;
import com.jedlab.model.StoryBookmark;
import com.jedlab.model.StoryBookmarkId;
import com.jedlab.model.StoryInvoice;
import com.jedlab.story.HtmlMarkdownProcessor.HtmlMarkdownHolder;
import com.jedlab.tika.parser.HtmlContentParser;
import com.jedlab.tika.parser.HtmlContentParser.ContentParser;

@Name("companyHome")
@Scope(ScopeType.CONVERSATION)
public class CompanyHome extends EntityHome<CompanyEntity>
{

    @In
    Session hibernateSession;

    private String slug;

    public String getSlug()
    {
        return slug;
    }

    public void setSlug(String slug)
    {
        this.slug = slug;
    }
    
    @Transactional
    public void updateViewCount()
    {
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        getEntityManager().createQuery("update CompanyEntity s set s.viewCount = (s.viewCount+1)  where s.companySlug = :sl")
                .setParameter("sl", getSlug()).executeUpdate();
    }
    
    
    public void load()
    {
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        CompanyEntity company = (CompanyEntity) hibernateSession.createCriteria(CompanyEntity.class, "s").add(Restrictions.eq("s.companySlug", getSlug()))
        .uniqueResult();
        setInstance(company);
    }
    
}
