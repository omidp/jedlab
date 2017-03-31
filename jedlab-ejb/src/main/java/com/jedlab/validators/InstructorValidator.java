package com.jedlab.validators;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.framework.EntityController;

import com.jedlab.JedLab;
import com.jedlab.framework.StringUtil;


@Name("instructorValidator")
@Scope(ScopeType.EVENT)
@BypassInterceptors
public class InstructorValidator extends EntityController
{

    
    public boolean exists(String param)
    {
        if(StringUtil.isEmpty(param))
            return true;
        EntityManager em = getEntityManager();
        try
        {
            em.createQuery("select i from Member i where i.username = :uname  or i.email = :email")
            .setParameter("uname", param)
            .setParameter("email", param).setMaxResults(1).getSingleResult();
            return true;
        }
        catch (NoResultException e)
        {
            try
            {                    
                em.createQuery("select i from Instructor i where i.mobileNo = :mno ")
                .setParameter("mno", param)
                .getSingleResult();
                return true;
            }
            catch (NoResultException nre)
            {
                
            }
        }
        return false;
    }
    
    
    public static InstructorValidator instance()
    {
        if (!Contexts.isEventContextActive())
        {
            throw new IllegalStateException("No active event context");
        }

        InstructorValidator instance = (InstructorValidator) Component.getInstance(InstructorValidator.class, ScopeType.EVENT);

        if (instance == null)
        {
            throw new IllegalStateException("No InstructorValidator could be created");
        }

        return instance;
    }
    
}
