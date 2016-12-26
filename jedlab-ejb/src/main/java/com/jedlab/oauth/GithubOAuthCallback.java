package com.jedlab.oauth;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityController;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.persistence.PersistenceContexts;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.PasswordHash;
import org.jboss.seam.util.RandomStringUtils;
import org.json.JSONObject;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.jedlab.action.RegisterAction;
import com.jedlab.framework.CookieUtil;
import com.jedlab.framework.CryptoUtil;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TxManager;
import com.jedlab.model.Student;

@Name("githubOAuthCallback")
@Scope(ScopeType.CONVERSATION)
public class GithubOAuthCallback extends EntityController
{

    private static final String PROTECTED_RESOURCE_URL = "https://api.github.com/user";

    @In
    HttpServletRequest httpRequest;

    private String errorMessage;

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    @Transactional
    public void load() throws IOException
    {
        this.errorMessage = StatusMessage.getBundleMessage("Github_Success", "");
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        PersistenceContexts.instance().changeFlushMode(FlushModeType.MANUAL);
        OAuth20Service service = (OAuth20Service) Expressions.instance().createValueExpression("#{githubOAuth}").getValue();
        String code = httpRequest.getParameter("code");
        if (StringUtil.isNotEmpty(code))
        {
            final OAuth2AccessToken accessToken = service.getAccessToken(code);
            final OAuthRequest req = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service.getConfig());
            service.signRequest(accessToken, req);
            final Response resp = req.send();
            if (resp.getCode() == 200)
            {
                JSONObject json = new JSONObject(resp.getBody());
                String username = json.getString("login");
                String email = json.getString("email");
                if (StringUtil.isNotEmpty(username) && StringUtil.isNotEmpty(email))
                {
                    HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                    Cookie c = CookieUtil.findCookieByName(httpRequest, "captchaRequired");
                    if (c != null)
                        CookieUtil.removeCookie(res, c);
                    try
                    {
                        Student st = (Student) getEntityManager().createQuery("select s from Student s where s.email = :email")
                                .setParameter("email", email).setMaxResults(1).getSingleResult();
                        if (st.isActive())
                        {
                            Identity identity = Identity.instance();
                            identity.getCredentials().setUsername(st.getUsername());
                            identity.acceptExternallyAuthenticatedPrincipal(new GithubPrincipal(st.getUsername()));
                            if (Events.exists())
                                Events.instance().raiseEvent(Identity.EVENT_LOGIN_SUCCESSFUL);
                            identity.login();
                        }
                        else
                        {
                            this.errorMessage = StatusMessage.getBundleMessage("Deactive_User", "");
                        }
                    }
                    catch (NoResultException e)
                    {
                        Student student = new Student();
                        String passwd = RandomStringUtils.randomAlphabetic(8);
                        String passwordKey = PasswordHash.instance().generateSaltedHash(passwd, username, "md5");
                        student.setEmail(email);
                        student.setUsername(username);
                        student.setPassword(passwordKey);
                        student.setActivationCode(null);
                        student.setActive(Boolean.TRUE);
                        getEntityManager().persist(student);
                        getEntityManager().flush();
                        Identity identity = Identity.instance();
                        identity.getCredentials().setUsername(email);
                        identity.getCredentials().setPassword(CryptoUtil.encodeBase64(passwd));
                        identity.login();
                    }
                }
                else
                {
                    this.errorMessage = StatusMessage.getBundleMessage("Github_Error", "");
                }
            }
            else
            {
                this.errorMessage = StatusMessage.getBundleMessage("Github_Error", "");
            }
        }
    }

}