package com.jedlab;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.omidbiz.core.axon.Axon;
import org.omidbiz.core.axon.AxonBuilder;

import com.jedlab.action.Constants;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.MultipartFileSender;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.exceptions.RequestException;
import com.jedlab.model.Course;
import com.jedlab.model.VideoToken;

public class CourseSearchServlet extends HttpServlet
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
        String term = req.getParameter("term");
        if(StringUtil.isEmpty(term))
            return;
        term = term.trim();
        Session sess = (Session) Component.getInstance("hibernateSession");
        Criteria criteria = sess.createCriteria(Course.class, "c");
        criteria.createCriteria("c.chapters", "chapters", Criteria.LEFT_JOIN);
        Disjunction dis = Restrictions.disjunction();
        dis.add(Restrictions.ilike("c.name", term, MatchMode.ANYWHERE));
        dis.add(Restrictions.ilike("chapters.name", term, MatchMode.ANYWHERE));
        criteria.add(dis);
        criteria.setMaxResults(10);
        List<Course> courseList = criteria.list();
        if (CollectionUtil.isEmpty(courseList))
            return;
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        List<CourseItem> list = new ArrayList<>();
        for (Course course : courseList)
        {
            list.add(new CourseItem(course.getId(), course.getName(), course.getName(), course.getName()));
        }
        Axon axon = new AxonBuilder().create();
        String json = axon.toJson(list);
        writer.write(json);
        resp.setContentLength(json.length());
        writer.flush();
        writer.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
    }


}
