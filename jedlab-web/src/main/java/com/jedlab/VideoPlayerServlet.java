package com.jedlab;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.security.Identity;

import com.jedlab.action.Constants;
import com.jedlab.framework.MultipartFileSender;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.exceptions.RequestException;
import com.jedlab.model.Chapter;
import com.jedlab.model.VideoToken;

public class VideoPlayerServlet extends HttpServlet
{

    private static final Pattern pattern = Pattern.compile("=(.+?)-");

    private final static Logger LOGGER = Logger.getLogger(VideoPlayerServlet.class.getName());

    private static ThreadLocal<AtomicInteger> count = new ThreadLocal<AtomicInteger>();

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        if (getCounterValue() == 0)
        {
            // Lifecycle.setupApplication(new ServletApplicationMap(context));
            ServletLifecycle.beginReinitialization(req, getServletContext());
        }

        try
        {
            incrementCounterValue();

            doWork(req, resp);

            decrementCounterValue();

            // End request only if it is not nested ContextualHttpServletRequest
            if (getCounterValue() == 0)
            {
                ServletLifecycle.endReinitialization();
                // Lifecycle.cleanupApplication();
            }
        }
        catch (IOException ioe)
        {
            removeCounter();
            Lifecycle.endRequest();
            // throw ioe;
        }
        catch (ServletException se)
        {
            removeCounter();
            Lifecycle.endRequest();
            // throw se;
        }
        catch (Exception e)
        {
            removeCounter();
            Lifecycle.endRequest();
            // throw new ServletException(e);
        }
        finally
        {
            // request ended
        }
    }

    private void doWork(HttpServletRequest req, HttpServletResponse resp) throws Exception
    {

        Identity identity = Identity.instance();
        if (identity.isLoggedIn() == false)
            throw new RequestException("not loggedin");
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        if (uid == null)
            sendError(resp);
        // only allowed partial content
        String range = req.getHeader("Range");
        if (StringUtil.isEmpty(range))
            sendError(resp);
        Matcher matcher = pattern.matcher(range);
        if (matcher.find() == false)
            sendError(resp);
        // Long r = Long.parseLong(matcher.group(1));

        Session sess = (Session) Component.getInstance("hibernateSession");
        String token = getToken(req.getRequestURL().toString());
        if (StringUtil.isEmpty(token))
            sendError(resp);
        Criteria criteria = sess.createCriteria(VideoToken.class, "vt");
        criteria.createCriteria("vt.chapter", "chap", Criteria.LEFT_JOIN);
        criteria.add(Restrictions.eq("vt.memberId", uid));
        criteria.add(Restrictions.eq("vt.token", token));
        criteria.setMaxResults(1);
        VideoToken vt = (VideoToken) criteria.uniqueResult();
        if (vt == null)
        {
            sendError(resp);
        }
        Chapter chapter = vt.getChapter();
        long expire = chapter.getDuration().getTime() + new Date().getTime();
        //
        String filePath = chapter.getUrl();
        String res = req.getParameter("resolution");
        filePath = getFileName(res, filePath);
        File file = new File(filePath);
        if (file.exists() == false)
            sendError(resp);
        MultipartFileSender.fromFile(file).with(req).with(resp).with(expire).serveResource();
        // PersistenceContexts.instance().changeFlushMode(FlushModeType.MANUAL);
        // if (r >= file.length())
        // {
        // sess.createQuery("delete from VideoToken vt where vt.id = :vtId").setParameter("vtId",
        // vt.getId()).executeUpdate();
        // sess.flush();
        // }

    }

    private String getFileName(String res, String origFilePath)
    {
        String extension = origFilePath.substring(origFilePath.lastIndexOf("."));
        String fname = origFilePath.substring(0, origFilePath.lastIndexOf("."));
        if (StringUtil.isNotEmpty(res))
        {
            if ("medium".equals(res))
                fname = fname + "_medium";
            if ("small".equals(res))
                fname = fname + "_small";
        }
        return fname + extension;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    }

    private String getToken(String location)
    {
        String name = location.substring(location.lastIndexOf("/") + 1);
        return name.substring(0, name.indexOf("."));
    }

    private void sendError(HttpServletResponse resp) throws IOException
    {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.sendRedirect("error.seam");
    }

    private int getCounterValue()
    {
        AtomicInteger i = count.get();
        if (i == null || i.intValue() < 0)
        {
            return 0;
        }
        else
        {
            return i.intValue();
        }
    }

    /*
     * Increments ThreadLocal counter value
     */
    private void incrementCounterValue()
    {
        AtomicInteger i = count.get();
        if (i == null || i.intValue() < 0)
        {
            i = new AtomicInteger(0);
            count.set(i);
        }
        i.incrementAndGet();
    }

    /*
     * Decrements ThreadLocal counter value
     */
    private void decrementCounterValue()
    {
        AtomicInteger i = count.get();
        if (i == null)
        {
            // we should never get here...
            throw new IllegalStateException("Counter for nested ContextualHttpServletRequest was removed before it should be!");
        }
        if (i.intValue() > 0)
        {
            i.decrementAndGet();
        }
    }

    /*
     * Removes ThreadLocal counter
     */
    private void removeCounter()
    {
        count.remove();
    }

}
