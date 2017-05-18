package com.jedlab.dao.home;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.NoResultException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.bpm.BeginTask;
import org.jboss.seam.annotations.bpm.EndTask;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesManager;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.security.Identity;

import com.jedlab.action.Constants;
import com.jedlab.action.WorkflowAction.ProcessModel;
import com.jedlab.framework.ByteUtil;
import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.framework.PageExceptionHandler;
import com.jedlab.framework.RegexUtil;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TxManager;
import com.jedlab.framework.WebContext;
import com.jedlab.framework.WebUtil;
import com.jedlab.model.Curate;
import com.jedlab.model.Member;
import com.jedlab.model.Page;
import com.jedlab.model.PageBlock;

@Name("pageHome")
@Scope(ScopeType.CONVERSATION)
public class PageHome extends EntityHome<Page>
{

    @Logger
    Log log;

    @In
    Session hibernateSession;

    public Long getPageId()
    {
        return (Long) getId();
    }

    public void setPageId(Long pageId)
    {
        setId((Long) pageId);
    }

    public void load()
    {
        if (isIdDefined())
        {
            // check load instance
        }
    }

    public void wire()
    {
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        Member mem = new Member();
        mem.setId(uid);
        getInstance().setMember(mem);
    }

    @Override
    @Transactional
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

    @Override
    protected Page loadInstance()
    {
        Criteria criteria = hibernateSession.createCriteria(Page.class, "p");
        criteria.createCriteria("p.member", "m", Criteria.LEFT_JOIN);
        criteria.createCriteria("p.blocks", "b", Criteria.LEFT_JOIN);
        criteria.createCriteria("b.curates", "c", Criteria.LEFT_JOIN);
        criteria.add(Restrictions.idEq(getPageId()));
        Page p = (Page) criteria.uniqueResult();
        if(Identity.instance().hasRole(Constants.ROLE_ADMIN) == false)
        {
            if (p.isPublished() == false && p.isOwner() == false)
                FacesManager.instance().redirect("/curate/curateList.seam", new HashMap<String, Object>(), false, false);
        }
        return p;
    }

    @Override
    public boolean isIdDefined()
    {
        return super.isIdDefined();
    }

    // /
    @Transactional
    public PageBlock createPageBlock(Page page)
    {
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        PageBlock pb = new PageBlock();
        pb.setPage(page);
        pb.setTitle("Java");
        getEntityManager().persist(pb);
        getEntityManager().flush();
        return pb;
    }

    @Transactional
    public Curate createCurate(Curate curate)
    {
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        Criteria criteria = hibernateSession.createCriteria(PageBlock.class, "pb");
        criteria.createCriteria("pb.page", "p", Criteria.LEFT_JOIN);
        criteria.createCriteria("p.member", "m", Criteria.LEFT_JOIN);
        criteria.add(Restrictions.idEq(curate.getPageBlock().getId()));
        PageBlock pb = (PageBlock) criteria.uniqueResult();
        if (pb.getPage().isOwner() == false)
            throw new IllegalArgumentException("invalid owner");
        updatePagePublished(pb.getPage().getId());
        Curate c = new Curate();
        c.setPageBlock(pb);
        c.setPage(pb.getPage());
        c.setUrl(curate.getUrl());
        c.setDescription(curate.getDescription());
        c.setTitle(curate.getTitle());
        c.setKeywords(curate.getKeywords());
        getEntityManager().persist(c);
        getEntityManager().flush();
        return c;
    }

    private void updatePagePublished(Long pageId)
    {
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        getEntityManager().createQuery("update Page p set p.published = false, p.processId = null where p.id = :pageId").setParameter("pageId", pageId)
                .executeUpdate();
    }

    @Transactional
    public void updatePageBlockTitle(Long id, String title)
    {
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        PageBlock pb = getEntityManager().find(PageBlock.class, id);
        if (pb.getPage().isOwner() == false)
            throw new IllegalArgumentException("invalid owner");
        pb.setTitle(title);
        getEntityManager().flush();

    }

    public boolean urlIsValid(String url)
    {
        return RegexUtil.URL_REGEX.matcher(url).find();
    }

    public void redirectToCurateList()
    {
        Map<String, Object> parameters = new HashMap<String, Object>();
        FacesManager.instance().redirect("/curate/curateList.seam", parameters, false, false);
    }

    @Transactional
    public String sendForPublish()
    {
        if (getPageId() == null)
            throw new PageExceptionHandler("page id is empty");
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        try
        {
            Long cnt = (Long) getEntityManager().createQuery("select count(c) from Curate c where c.page.id = :pid ")
                    .setParameter("pid", getPageId()).getSingleResult();
            if (cnt < 2)
            {
                getStatusMessages().addFromResourceBundle(Severity.ERROR, "Curate_Error_Count");
            }
            else
            {
                Page page = getEntityManager().find(Page.class, getPageId());
                if (page.isOwner() == false)
                    throw new PageExceptionHandler("invalid owner");
                if(page.getProcessId() != null)
                    throw new PageExceptionHandler("wait for approval");
                ProcessModel pm = new ProcessModel(Pages.getCurrentViewId().replace(".xhtml", ".seam"), "JEDLink");
                pm.getVariables().put("pageId", getPageId());
                getBusinessProcessContext().set(Constants.FLOW_OBJECT_NAME, ByteUtil.convertObjectToByte(pm));

                BusinessProcess.instance().createProcess(Constants.BP_PROCESS_NAME);
                page.setProcessId(BusinessProcess.instance().getProcessId());
                getStatusMessages().addFromResourceBundle("Send_For_Activation");
            }
        }
        catch (NoResultException nre)
        {
            getStatusMessages().addFromResourceBundle(Severity.ERROR, "Curate_Error_Count");
        }
        getEntityManager().flush();
        return "sendForPublish";
    }

    @Transactional
    @BeginTask(taskIdParameter="taskId")
    @EndTask(transition="toEnd")
    public void accept()
    {
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        String pidParam = WebUtil.getParameterValue("pageId");
        if (StringUtil.isEmpty(pidParam))
            throw new ErrorPageExceptionHandler("page id is empty");
        Long pid = Long.parseLong(pidParam);
        Page page = getEntityManager().find(Page.class, pid);
        page.setProcessId(null);
        page.setPublished(true);
        getEntityManager().flush();
        getStatusMessages().addFromResourceBundle("Approved");
        WebContext.instance().redirectIt(true, false);
    }

    @Transactional
    @BeginTask(taskIdParameter="taskId")
    @EndTask(transition="toEnd")
    public void reject()
    {
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        String pidParam = WebUtil.getParameterValue("pageId");
        if (StringUtil.isEmpty(pidParam))
            throw new ErrorPageExceptionHandler("page id is empty");
        Long pid = Long.parseLong(pidParam);
        getEntityManager().createQuery("delete from Curate c where c.page.id = :pid").setParameter("pid", pid).executeUpdate();
        getEntityManager().createQuery("delete from PageBlock pb where pb.page.id = :pid").setParameter("pid", pid).executeUpdate();
        getEntityManager().createQuery("delete from Page p where p.id = :pid").setParameter("pid", pid).executeUpdate();
        getEntityManager().flush();
        getStatusMessages().addFromResourceBundle("Rejected");
        WebContext.instance().redirectIt(true, false);
    }

    @Transactional
    public boolean curateExists(String url)
    {
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        if (uid == null)
            return true;
        try
        {
            getEntityManager()
                    .createQuery("select c from Curate c left join c.page p left join p.member m where c.url = :url and m.id =:memId ")
                    .setParameter("url", url).setParameter("memId", uid).setMaxResults(1).getSingleResult();
            return true;
        }
        catch (NoResultException e)
        {
            
        }
        return false;
    }

}
