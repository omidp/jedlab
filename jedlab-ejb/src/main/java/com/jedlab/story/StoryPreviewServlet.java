package com.jedlab.story;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.commonmark.node.Node;

import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TransactionalContextualHttpServletRequest;
import com.jedlab.story.HtmlMarkdownProcessor.HtmlMarkdownHolder;

public class StoryPreviewServlet extends HttpServlet
{

    
    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        new TransactionalContextualHttpServletRequest(req) {

            @Override
            protected void workInTransaction() throws Exception
            {
                String mdcontent = req.getParameter("mdcontent");
                if(StringUtil.isEmpty(mdcontent))
                {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
                else
                {
                    HtmlMarkdownHolder holder =  (HtmlMarkdownHolder) getServletContext().getAttribute(HtmlMarkdownProcessor.MARKDOWN);
                    Node node = holder.getParser().parse(StringUtil.escapeJavascript(mdcontent));
                    String render = holder.getRenderer().render(node);
                    resp.setContentType("text/html;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.print(render);
                    out.flush();
                }
                
            }
        }.run();
    }
    
}
