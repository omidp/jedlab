package com.jedlab;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.ServletLifecycle;

import com.jedlab.framework.MultipartFileSender;
import com.jedlab.model.Podcast;
import com.jedlab.model.Preview;

public class PodcastPlayerServlet extends HttpServlet
{

    private final static Logger LOGGER = Logger.getLogger(PodcastPlayerServlet.class.getName());

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
        Long podcastId = Long.parseLong(req.getParameter("podcastId"));
        Session sess = (Session) Component.getInstance("hibernateSession");
        //
        Podcast item = (Podcast) sess.get(Podcast.class, podcastId);
        String filePath = item.getUrl();
        File file = new File(filePath);
        if (file.exists() == false)
            sendError(resp);
        MultipartFileSender.fromFile(file).with(req).with("audio/mp3").with(resp).serveResource();

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
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
