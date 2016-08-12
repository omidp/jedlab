package com.jedlab;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

import com.jedlab.action.Constants;
import com.jedlab.framework.MultipartFileSender;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.exceptions.RequestException;
import com.jedlab.model.VideoToken;

public class VideoPlayerServlet extends HttpServlet
{

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        new ContextualHttpServletRequest(req) {

            @Override
            public void process() throws Exception
            {
                doWork(req, resp);
            }
        }.run();
    }

    private static void doWork(HttpServletRequest req, HttpServletResponse resp) throws ServletException
    {
        Identity identity = Identity.instance();
        if (identity.isLoggedIn() == false)
            throw new RequestException("not loggedin");
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        if (uid == null)
            throw new RequestException("not loggedin");
        Session sess = (Session) Component.getInstance("hibernateSession");
        String token = getToken(req.getRequestURL().toString());
        if (StringUtil.isEmpty(token))
            throw new RequestException("not loggedin");

        Criteria criteria = sess.createCriteria(VideoToken.class, "vt");
        criteria.createCriteria("vt.chapter", "chap", Criteria.LEFT_JOIN);
        criteria.add(Restrictions.eq("vt.memberId", uid));
        criteria.add(Restrictions.eq("vt.token", token));
        criteria.setMaxResults(1);
        VideoToken vt = (VideoToken) criteria.uniqueResult();
        if (vt == null)
            throw new RequestException("user not registered in course");
        String filePath = vt.getChapter().getUrl();
        File file = new File(filePath);
        if (file.exists() == false)
            throw new RequestException("file  not found");
        try
        {
            MultipartFileSender.fromFile(file).with(req).with(resp).serveResource();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    }

    private static String getToken(String location)
    {
        String name = location.substring(location.lastIndexOf("/") + 1);
        return name.substring(0, name.indexOf("."));
    }

}
