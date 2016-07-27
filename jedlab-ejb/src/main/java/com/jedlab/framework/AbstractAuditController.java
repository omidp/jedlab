package com.jedlab.framework;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.exception.RevisionDoesNotExistException;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditCriterion;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;

/**
 * @author omidbiz
 * 
 * @param <T>
 */
@Scope(ScopeType.CONVERSATION)
@AutoCreate
@Transactional
public abstract class AbstractAuditController<T> extends PagingController<Revision<T>>
{

    @Logger
    Log log;

    private static final String REV_SORT = "revpo.";

    private Class<T> clz;

    AuditReader auditReader;

    AuditQuery auditQuery;

    private Date from;

    private Date to;

    private String username;

    private String ipAddress;

    private RevisionType revisionType;

    
    public Object getId()
    {
       return id;
    }

    public void setId(Object id)
    {
       this.id = id;
    }
    private List<Revision<T>> auditList;
    
    private Object id;

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public RevisionType getRevisionType()
    {
        return revisionType;
    }

    public void setRevisionType(RevisionType revisionType)
    {
        this.revisionType = revisionType;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public Date getTo()
    {
        return to;
    }

    public void setTo(Date to)
    {
        this.to = to;
    }

    public Date getFrom()
    {
        return from;
    }

    public void setFrom(Date from)
    {
        this.from = from;
    }

    public Class<T> getEntityClass()
    {
        ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    public String restore(T instance)
    {
        if (instance != null)
        {
            getEntityManager().merge(instance);
            getEntityManager().flush();
        }
        return null;
    }

    @Override
    public List<Revision<T>> getResultList()
    {
        if (auditList != null)
            return truncResultList(auditList);
        try
        {
            AuditQuery query = createQuery(getAuditQuery(false, true));
            //show versioning
            if(getId() != null)
            {
                //List<Number> revisions = getAuditReader().getRevisions(getEntityClass(), getId());
                query.add(AuditEntity.id().eq(getId()));
            }
            //
            applyOrder(query);
            // pagination
            if (getFirstResult() != null)
                query.setFirstResult(getFirstResult());
            if (getMaxResults() != null)
                query.setMaxResults(getMaxResults() + 1);
            //
            List resultList = query.getResultList();
            final List<Object[]> queryResult = (List<Object[]>) resultList;
            final List<Revision<T>> result = new ArrayList<Revision<T>>(resultList.size());
            for (final Object[] array : queryResult)
            {
                T instance = (T) array[0];
                com.jedlab.framework.RevisionPO revpo = (RevisionPO) array[1];
                RevisionType rt = (RevisionType) array[2];
                result.add(new Revision<T>(instance, revpo, rt));
            }
            auditList = result;
        }
        catch (RevisionDoesNotExistException e)
        {
            StatusMessages.instance().add("Revision yaft nashod");
        }
        return truncResultList(auditList);
    }

    public List<Revision<T>> getAuditList()
    {
        return getResultList();
    }

    private AuditQuery createQuery(AuditQuery query)
    {
        if(getFrom() != null)
        {
            query.add(AuditEntity.revisionProperty("timestamp").ge(getFrom().getTime()));
        }
        if(getTo() != null)
        {
            query.add(AuditEntity.revisionProperty("timestamp").lt(getTo().getTime()));
        }
        if (StringUtil.isNotEmpty(getIpAddress()))
        {
            query.add(AuditEntity.revisionProperty("ipAddress").eq(getIpAddress()));
        }
        if (StringUtil.isNotEmpty(getUsername()))
        {
            query.add(AuditEntity.revisionProperty("username").eq(getUsername()));
        }
        if (getRevisionType() != null)
        {
            query.add(AuditEntity.revisionType().eq(getRevisionType()));
        }
        if (CollectionUtil.isNotEmpty(getCriterions()))
        {
            for (AuditCriterion ac : getCriterions())
            {
                query.add(ac);
            }
        }

        return query;
    }

    private void applyOrder(AuditQuery query)
    {        
        query.addOrder(AuditEntity.revisionProperty("timestamp").desc());
        if (StringUtil.isNotEmpty(getOrderDirection()) && StringUtil.isNotEmpty(getOrderColumn()))
        {
            if ("asc".equals(getOrderDirection()))
            {
                if (isRevisionProperty())
                    query.addOrder(AuditEntity.revisionProperty(getOrderColumn().substring(REV_SORT.length())).asc());
                else
                    query.addOrder(AuditEntity.property(getOrderColumn()).asc());
            }
            else
            {
                if (isRevisionProperty())
                    query.addOrder(AuditEntity.revisionProperty(getOrderColumn().substring(REV_SORT.length())).desc());
                else
                    query.addOrder(AuditEntity.property(getOrderColumn()).desc());
            }
        }
    }

    private Long resultCount;

    @Override
    public Long getResultCount()
    {
        if (resultCount != null)
            return resultCount;
        AuditQuery query = createQuery(getAuditQuery(false, true));
        if(getId() != null)
        {
            //List<Number> revisions = getAuditReader().getRevisions(getEntityClass(), getId());
            query.add(AuditEntity.id().eq(getId()));
        }
//        resultCount = (Long) query.addProjection(AuditEntity.id().count("id")).getSingleResult();
        resultCount = (Long) query.addProjection(AuditEntity.revisionNumber().count()).getSingleResult();
        return resultCount;
    }

    @Override
    public boolean isNextExists()
    {
        return auditList != null && getMaxResults() != null && auditList.size() > getMaxResults();
    }

    private boolean isRevisionProperty()
    {
        return getOrderColumn().startsWith(REV_SORT);
    }

    public AuditReader getAuditReader()
    {
        if (auditReader == null)
            auditReader = AuditReaderFactory.get(getEntityManager());
        return auditReader;
    }

    public AuditQuery getAuditQuery(boolean selectedEntities, boolean deletedEntities)
    {
        if (clz == null)
            clz = getEntityClass();
        auditQuery = getAuditReader().createQuery().forRevisionsOfEntity(clz, selectedEntities, deletedEntities);
        return auditQuery;
    }

    protected List<AuditCriterion> getCriterions()
    {
        return null;
    }

}
