package com.jedlab;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.ServletLifecycle;

import com.jedlab.framework.MultipartFileSender;

public class FreePlayerServlet extends HttpServlet
{

    private final static Logger LOGGER = Logger.getLogger(FreePlayerServlet.class.getName());

    private static ThreadLocal<AtomicInteger> count = new ThreadLocal<AtomicInteger>();

    private static final String REPOSITORY = Env.getRepositoryLocation();

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
        int cut = req.getContextPath().length() + "/player".length()+1;
        String filePath = REPOSITORY + req.getRequestURI().substring(cut);
        File file = new File(filePath);
        if (file.exists() == false)
            sendError(req, resp);
        Metadata metadata = new Metadata();
        TikaConfig tika = new TikaConfig();
        metadata.set(Metadata.RESOURCE_NAME_KEY, file.toString());
        MediaType mimetype = tika.getDetector().detect(TikaInputStream.get(file), metadata);
        log("File " + file + " is " + mimetype.toString());
        MultipartFileSender.fromFile(file).with(req).with(mimetype.toString()).with(resp).serveResource();
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    }

    private void sendError(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.sendRedirect(req.getContextPath() + "/error.seam");
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
