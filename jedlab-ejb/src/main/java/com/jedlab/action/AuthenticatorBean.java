package com.jedlab.action;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.PasswordHash;

import com.jedlab.framework.CacheManager;
import com.jedlab.framework.CryptoUtil;
import com.jedlab.model.LoginActivity;
import com.jedlab.model.Member;

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
    
    @In(create=true)
    LoginActionManager loginActionManager;

    public boolean authenticate()
    {
        log.info("authenticating {0}", credentials.getUsername());
        try
        {
            Member m = (Member) entityManager.createQuery("select m from Member m where m.username = :uname or m.email = :email")
                    .setParameter("uname", credentials.getUsername()).setParameter("email", credentials.getUsername()).setMaxResults(1)
                    .getSingleResult();
            if (m.isActive() == false)
                return false;
            if ("admin".equals(credentials.getUsername()))
            {
                identity.addRole(Constants.ROLE_ADMIN);
            }
            String pass = CryptoUtil.decodeBase64(credentials.getPassword());
            String passwordKey = PasswordHash.instance().generateSaltedHash(pass, credentials.getUsername(), "md5");
            if (passwordKey.equals(m.getPassword()))
            {
                LoginActivity la = loginActionManager.addActivity(m.getUsername());
                Contexts.getSessionContext().set(Constants.CURRENT_USER_ID, m.getId());
                Contexts.getSessionContext().set(Constants.CURRENT_USER_NAME, m.getUsername());
                Contexts.getSessionContext().set(LoginActivity.TOKEN, la.getToken());
                CacheManager.put(Constants.CURRENT_USER, m);
                return true;
            }
            //check token
            //comes from filter
//            LoginActivity la = loginActionManager.findLastActiveLogin(credentials.getUsername());
//            if(la != null)
//            {
//                if(credentials.getPassword().equals(la.getToken()))
//                {
//                    Contexts.getSessionContext().set(Constants.CURRENT_USER_ID, m.getId());
//                    Contexts.getSessionContext().set(Constants.CURRENT_USER_NAME, m.getUsername());
//                    return true;
//                }
//            }
        }
        catch (NoResultException e)
        {
        }

        return false;
    }
}
