package com.jedlab;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.IOUtils;
import org.jboss.seam.Component;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

import com.jedlab.framework.StringUtil;
import com.jedlab.model.Course;

public class VideoImagePosterServlet extends HttpServlet
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

    private static void doWork(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String courseId = req.getParameter("videoCourseId");
        if (StringUtil.isEmpty(courseId))
        {
            return;
        }

        EntityManager em = (EntityManager) Component.getInstance("entityManager");
        try
        {
            Course c = em.find(Course.class, Long.parseLong(courseId));
            if (c.getHasImage())
            {
                byte[] image = c.getImage();
                ByteArrayInputStream bis = new ByteArrayInputStream(image);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try
                {
                    Thumbnails.of(bis).size(250, 250).toOutputStream(bos);
                    byte[] imageData = bos.toByteArray();
                    resp.setContentType("image/png");
                    resp.setContentLength(imageData.length);
                    ServletOutputStream ouputStream = resp.getOutputStream();
                    ouputStream.write(imageData, 0, imageData.length);
                    ouputStream.flush();
                    ouputStream.close();
                }
                catch (IOException e)
                {
                }
                finally
                {
                    IOUtils.closeQuietly(bis);
                    IOUtils.closeQuietly(bos);
                }
            }

        }
        catch (NoResultException e)
        {
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    }

}
