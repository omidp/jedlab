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
import com.jedlab.framework.exceptions.RequestException;
import com.jedlab.model.MemberCourse;

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
        String corId = getCourseId(req.getRequestURL().toString());
        Long chapterId = Long.parseLong(corId);
        Criteria criteria = sess.createCriteria(MemberCourse.class, "mc");
        criteria.createCriteria("mc.chapter", "chap", Criteria.LEFT_JOIN);
        criteria.add(Restrictions.eq("mc.member.id", uid));
        criteria.add(Restrictions.eq("chap.id", chapterId));
        MemberCourse mc = (MemberCourse) criteria.uniqueResult();
        if (mc == null)
            throw new RequestException("user not registered in course");
        String filePath = mc.getChapter().getUrl();
        File file = new File(filePath);
        if (file.exists() == false)
            throw new RequestException("file  not found");
        InputStream is = null;
        OutputStream os = null;
        try
        {
            is = new FileInputStream(file);
            BufferedInputStream boa = new BufferedInputStream(new FileInputStream(new File("/home/omidp/temp/jedlab_video/content.ogv")));
            byte[] b = IOUtils.toByteArray(is);
            os = resp.getOutputStream();
            resp.setHeader("Content-Length", String.valueOf(b.length));
            String l = String.valueOf(b.length);
            String l2 = String.valueOf(b.length / 2);
            resp.setHeader("Content-Range", String.format("bytes %s-%s/%s", "0", l, l));

            IOUtils.copy(boa, os);
            os.flush();
        }
        catch (Exception e)
        {
        }
        finally
        {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    }

    private static String getCourseId(String location)
    {
        String name = location.substring(location.lastIndexOf("/") + 1);
        return name.substring(0, name.indexOf("."));
    }

}
