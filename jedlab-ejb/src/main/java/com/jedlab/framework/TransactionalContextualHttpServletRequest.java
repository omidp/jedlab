package com.jedlab.framework;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Omid Pourhadi
 *
 */
public abstract class TransactionalContextualHttpServletRequest extends ContextualHttpServletRequest
{

    protected final static Logger logger = LoggerFactory.getLogger(TransactionalContextualHttpServletRequest.class);

    private EntityManager entityManager;

    public TransactionalContextualHttpServletRequest(HttpServletRequest request)
    {
        super(request);
    }

    @Override
    public void process() throws Exception
    {
        work();
    }

    private void work()
    {
        try
        {
            TxManager.beginTransaction();
            joinTransaction();
            workInTransaction();
            TxManager.commitTransaction();
        }
        catch (SecurityException e)
        {
            logger.info(e.getMessage());
        }
        catch (IllegalStateException e)
        {
            logger.info(e.getMessage());
        }
        catch (Exception e)
        {
            logger.info(e.getMessage());
        }

    }

    public UserTransaction getTransation()
    {
        return Transaction.instance();
    }

    private void joinTransaction()
    {
        entityManager = (EntityManager) Component.getInstance("entityManager");
        TxManager.joinTransaction(entityManager);
    }

    protected EntityManager getEntityManager()
    {
        return entityManager;
    }

    protected Session getSession()
    {
        return (Session) entityManager.getDelegate();
    }

    protected abstract void workInTransaction();

}
