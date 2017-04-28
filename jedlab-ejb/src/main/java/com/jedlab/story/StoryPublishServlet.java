package com.jedlab.story;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;

import com.jedlab.Env;
import com.jedlab.action.Constants;
import com.jedlab.framework.PageExceptionHandler;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TransactionalContextualHttpServletRequest;
import com.jedlab.model.Member;
import com.jedlab.model.Story;

public class StoryPublishServlet extends HttpServlet
{

    
    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        new TransactionalContextualHttpServletRequest(req) {

            @Override
            protected void workInTransaction() throws Exception
            {
                String mdcontent = req.getParameter("mdcontent");
                String storyId = req.getParameter("storyId");
                PrintWriter out = resp.getWriter();
                resp.setContentType("text/plain;charset=utf-8");
                Long id = StoryHome.instance().publishContent(mdcontent, storyId);
                out.print(id);
                out.flush();
            }
        }.run();
    }
    
}
