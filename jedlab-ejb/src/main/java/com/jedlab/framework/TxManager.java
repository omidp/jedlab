package com.jedlab.framework;

import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.jboss.seam.transaction.Transaction;

/**
 * @author Omid Pourhadi
 *
 */
public class TxManager
{

    
    public static void beginTransaction()
    {
        try
        {
            if (org.jboss.seam.transaction.Transaction.instance().isNoTransaction())
                Transaction.instance().begin();
        }
        catch (NotSupportedException e)
        {
            e.printStackTrace();
        }
        catch (SystemException e)
        {
            e.printStackTrace();
        }
    }

    public static void commitTransaction()
    {
        try
        {
            if (Transaction.instance().isActive())
                Transaction.instance().commit();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        catch (RollbackException e)
        {
            e.printStackTrace();
        }
        catch (HeuristicMixedException e)
        {
            e.printStackTrace();
        }
        catch (HeuristicRollbackException e)
        {
            e.printStackTrace();
        }
        catch (SystemException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isNoTransaction()
    {
        try
        {
            return Transaction.instance().isNoTransaction();
        }
        catch (SystemException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static void joinTransaction(EntityManager entityManager)
    {
        try
        {
            Transaction.instance().enlist(entityManager);
        }
        catch (SystemException e)
        {
            e.printStackTrace();
        }
    }
    
}
