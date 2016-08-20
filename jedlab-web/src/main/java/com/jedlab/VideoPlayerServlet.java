package com.jedlab;

import java.io.File;
import java.io.IOException;
import java.util.Date;
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
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.persistence.PersistenceContexts;
import org.jboss.seam.security.Identity;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

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

        try
        {
            Identity identity = Identity.instance();
            if (identity.isLoggedIn() == false)
                throw new RequestException("not loggedin");
            Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
            if (uid == null)
                throw new RequestException("not loggedin");
            // only allowed partial content
            String range = req.getHeader("Range");
            if (StringUtil.isEmpty(range))
                throw new RequestException("matcher not found");
            Matcher matcher = pattern.matcher(range);
            if (matcher.find() == false)
                throw new RequestException("not in range");
            // Long r = Long.parseLong(matcher.group(1));

            Session sess = (Session) Component.getInstance("hibernateSession");
            PersistenceContexts.instance().changeFlushMode(FlushModeType.MANUAL);
            String token = getToken(req.getRequestURL().toString());
            if (StringUtil.isEmpty(token))
                throw new RequestException("invalid token");
            Criteria criteria = sess.createCriteria(VideoToken.class, "vt");
            criteria.createCriteria("vt.chapter", "chap", Criteria.LEFT_JOIN);
            criteria.add(Restrictions.eq("vt.memberId", uid));
            criteria.add(Restrictions.eq("vt.token", token));
            criteria.setMaxResults(1);
            VideoToken vt = (VideoToken) criteria.uniqueResult();
            if (vt == null)
            {
                throw new RequestException("user not registered in course, can't find video token");
            }
            Chapter chapter = vt.getChapter();
            long expire = chapter.getDuration().getTime() + new Date().getTime();
            //
            String filePath = chapter.getUrl();
            String res = req.getParameter("resolution");
            if (StringUtil.isNotEmpty(res))
            {
                if ("medium".equals(res))
                    filePath = filePath + "_medium";
                if ("small".equals(res))
                    filePath = filePath + "_small";
            }
            File file = new File(filePath);
            if (file.exists() == false)
                throw new RequestException("file  not found");
            MultipartFileSender.fromFile(file).with(req).with(resp).with(expire).serveResource();
            // if (r >= file.length())
            // {
            // sess.createQuery("delete from VideoToken vt where vt.id = :vtId").setParameter("vtId",
            // vt.getId()).executeUpdate();
            // sess.flush();
            // }

        }
        catch (Exception e)
        {
            LOGGER.info("exception occured VideServletPlayer : " + e.getMessage());
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    }

    private static String getToken(String location)
    {
        String name = location.substring(location.lastIndexOf("/") + 1);
        return name.substring(0, name.indexOf("."));
    }

}
