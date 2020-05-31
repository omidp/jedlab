package com.jedlab;

import javax.servlet.http.HttpSession;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.framework.HibernateEntityController;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.security.Identity;
import org.omidbiz.core.axon.Axon;
import org.omidbiz.core.axon.filters.RecursionControlFilter;
import org.omidbiz.core.axon.hibernate.AxonBuilder;

import com.github.scribejava.apis.GitHubApi;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.jedlab.action.Constants;
import com.jedlab.framework.CacheManager;
import com.jedlab.model.Course.Language;
import com.jedlab.model.Course.Level;
import com.jedlab.model.Member;
import com.jedlab.model.enums.Gender;
import com.jedlab.model.enums.Privacy;
import com.jedlab.model.enums.QuestionType;

@Name("jedLab")
@Scope(ScopeType.CONVERSATION)
public class JedLab extends HibernateEntityController
{

    public Member getCurrentUser()
    {
        Object u = CacheManager.get(Constants.CURRENT_USER);
        if (u != null)
            return (Member) u;
        Member currentUser = (Member) getSession().get(Member.class,
                Long.parseLong(String.valueOf(getSessionContext().get(Constants.CURRENT_USER_ID))));
        CacheManager.put(Constants.CURRENT_USER, currentUser);
        return currentUser;
    }

    @Factory("courseLevels")
    public Level[] courseLevels()
    {
        return Level.values();
    }
    
    @Factory("questionTypes")
    public QuestionType[] questionTypes()
    {
        return QuestionType.values();
    }

    @Factory("courseLangs")
    public Language[] courseLangs()
    {
        return Language.values();
    }

    @Factory("genders")
    public Gender[] genders()
    {
        return Gender.values();
    }

    @Factory("privacies")
    public Privacy[] privacies()
    {
        return Privacy.values();
    }

    @Factory(value = "axon", scope = ScopeType.EVENT)
    public Axon axon()
    {
        return new AxonBuilder().addFilter(new RecursionControlFilter()).create();
    }
    
    @Factory(value = "githubOAuth", scope = ScopeType.EVENT)
    public OAuth20Service githubOAuth()
    {
        return new ServiceBuilder().apiKey(Env.getGithubKey())
                .apiSecret(Env.getGithubSecret()).callback(Env.getGithubCallback()).build(GitHubApi.instance());
    }
    
    @Factory(value = "googleOAuth", scope = ScopeType.EVENT)
    public OAuth20Service googleOAuth()
    {
        return new ServiceBuilder().apiKey(Env.getGoogleKey())
                .apiSecret(Env.getGoogleSecret()).callback(Env.getGoogleCallback()).scope("https://www.googleapis.com/auth/userinfo.email").build(GoogleApi20.instance());
    }

    public String getPageDescription()
    {
        String desc = Pages.instance().getDescription(Pages.getCurrentViewId());
        if (desc == null)
            return null;
        return interpolate(desc);
    }

    public String getSessionId()
    {
        HttpSession sess = (HttpSession) Component.getInstance("httpSession");
        if (sess == null)
            return "";
        return sess.getId();
    }
    
    public Long getCurrentUserId()
    {
        Long currentId = (Long) getSessionContext().get(Constants.CURRENT_USER_ID);
//        currentId = Identity.instance().get
        return currentId;
    }
    
    public String getCurrentUsername()
    {
        return (String) getSessionContext().get(Constants.CURRENT_USER_NAME);
    }

    public static JedLab instance()
    {
        if (!Contexts.isConversationContextActive())
        {
            throw new IllegalStateException("No active conversation context");
        }

        JedLab instance = (JedLab) Component.getInstance(JedLab.class, ScopeType.CONVERSATION);

        if (instance == null)
        {
            throw new IllegalStateException("No JedLab could be created");
        }

        return instance;
    }

}
