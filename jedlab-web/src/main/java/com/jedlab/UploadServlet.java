package com.jedlab;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

import com.jedlab.dao.home.ChapterHome;
import com.jedlab.framework.CacheManager;
import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TransactionalContextualHttpServletRequest;
import com.jedlab.framework.TxManager;
import com.jedlab.model.Chapter;

/**
 * Servlet implementation class UploadServlet
 */
public class UploadServlet extends HttpServlet
{

    /**
     * Default constructor.
     */
    public UploadServlet()
    {
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {

        new TransactionalContextualHttpServletRequest(request) {

            @Override
            protected void workInTransaction()
            {
                try
                {
                    String header = request.getHeader("x_filename");
                    String name = request.getHeader("x_cname");
                    String dur = request.getHeader("x_dur");
                    String courseId = request.getHeader("x_cid");
                    if (StringUtil.isNotEmpty(courseId) && StringUtil.isNotEmpty(dur) && StringUtil.isNotEmpty(name)
                            && StringUtil.isNotEmpty(header))
                    {
                        int fileSize = request.getContentLength();
                        ChapterHome ch = (ChapterHome) Component.getInstance(ChapterHome.class, ScopeType.CONVERSATION);
                        ServletInputStream inputStream = request.getInputStream();
                        byte[] byteArray = IOUtils.toByteArray(inputStream);
                        ch.getUploadItem().setData(byteArray);
                        ch.getUploadItem().setFileName(header);
                        ch.getUploadItem().setFileSize(fileSize);
                        ch.getCourse().setId(Long.parseLong(courseId));
                        ch.setDuration(dur);
                        Chapter c = new Chapter();
                        c.setName(name);
                        ch.setInstance(c);
                        ch.persist();
                    }

                }
                catch (Exception e)
                {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    TxManager.commitTransaction();
                    TxManager.beginTransaction();
                }
            }
        }.run();
    }

}
