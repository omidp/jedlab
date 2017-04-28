package com.jedlab.story;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TransactionalContextualHttpServletRequest;

public class StoryDraftServlet extends HttpServlet
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
                String storyTitle = req.getParameter("storyTitle");
                if (StringUtil.isNotEmpty(storyTitle) && StringUtil.isNotEmpty(mdcontent))
                {
                    Long id = StoryHome.instance().draftContent(mdcontent, storyId, storyTitle);
                    PrintWriter out = resp.getWriter();
                    resp.setContentType("text/plain;charset=utf-8");
                    out.print(id);
                    out.flush();
                }
            }
        }.run();
    }
    
}
