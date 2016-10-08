package com.jedlab;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jedlab.framework.CacheManager;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TransactionalContextualHttpServletRequest;

public class DeleteTokenServlet extends HttpServlet
{

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {

    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {

        new TransactionalContextualHttpServletRequest(req) {

            @Override
            protected void workInTransaction()
            {
                String token = req.getParameter("tk");
                if (StringUtil.isEmpty(token))
                    return;
                token = token.trim();
                getEntityManager().createQuery("delete from VideoToken vt where vt.token = :token").setParameter("token", token)
                        .executeUpdate();
                CacheManager.remove(token);
                getEntityManager().flush();
            }
        }.run();
    }

}
