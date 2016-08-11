package com.jedlab;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

import com.jedlab.action.Constants;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.exceptions.RequestException;
import com.jedlab.model.MemberCourse;
import com.jedlab.model.VideoToken;

public class VideoPlayer extends HttpServlet
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
        InputStream is = null;
        OutputStream os = null;
        BufferedInputStream boa = null;
        InputStream isbyte = null;
        try
        {
            is = new FileInputStream(file);
            isbyte = new FileInputStream(file);
            boa = new BufferedInputStream(isbyte);
            byte[] b = IOUtils.toByteArray(is);
            os = resp.getOutputStream();
            resp.setHeader("Content-Length", String.valueOf(b.length));
            // String l = String.valueOf(b.length);
            // String l2 = String.valueOf(b.length / 2);
            // resp.setHeader("Content-Range", String.format("bytes %s-%s/%s",
            // "0", l, l));

            IOUtils.copy(boa, os);
            os.flush();
            sess.delete(vt);
            sess.flush();
        }
        catch (Exception e)
        {
        }
        finally
        {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(boa);
            IOUtils.closeQuietly(isbyte);
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
