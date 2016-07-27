package com.jedlab.action;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

@Stateless
@Name("authenticator")
public class AuthenticatorBean implements Authenticator
{
    @Logger
    private Log log;

    @In
    Identity identity;

    @In
    Credentials credentials;

    @In
    EntityManager entityManager;

    public boolean authenticate()
    {
        log.info("authenticating {0}", credentials.getUsername());

//        try
//        {
//            User u = (User) entityManager.createQuery("select u from User u where u.userName = :uname")
//                    .setParameter("uname", credentials.getUsername()).setMaxResults(1).getSingleResult();
//            if (u.getActive() == null || u.getActive().booleanValue() == false)
//                return false;
//            String passwordKey = PasswordHash.instance().generateSaltedHash(credentials.getPassword(), credentials.getUsername(), "md5");
//            if (passwordKey.equals(u.getPassword()))
//            {
//                Contexts.getSessionContext().set(Constants.CURRENT_USER_ID, u.getId());
//                CacheManager.put(Constants.CURRENT_USER, u);
//                return true;
//            }
//        }
//        catch (NoResultException e)
//        {
//        }

        return false;
    }
    
}
