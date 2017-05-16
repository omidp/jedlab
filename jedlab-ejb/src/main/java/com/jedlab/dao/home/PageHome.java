package com.jedlab.dao.home;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

import com.jedlab.action.Constants;
import com.jedlab.framework.TxManager;
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
            //check load instance
        }
    }

    public void wire()
    {
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        Member mem = new Member();
        // mem.setId(uid);
        mem.setId(16L);
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
        criteria.createCriteria("p.blocks", "b", Criteria.LEFT_JOIN);
        criteria.createCriteria("b.curates", "c", Criteria.LEFT_JOIN);
        criteria.add(Restrictions.idEq(getPageId()));        
        return (Page) criteria.uniqueResult();
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

}
