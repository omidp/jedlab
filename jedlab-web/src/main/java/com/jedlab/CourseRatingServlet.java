package com.jedlab;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;

import com.jedlab.action.Constants;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TransactionalContextualHttpServletRequest;
import com.jedlab.model.Course;
import com.jedlab.model.CourseRating;
import com.jedlab.model.Member;

public class CourseRatingServlet extends HttpServlet
{

    private final static Logger LOGGER = Logger.getLogger(CourseRatingServlet.class.getName());

    private static ThreadLocal<AtomicInteger> count = new ThreadLocal<AtomicInteger>();

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {

    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
    {
        new TransactionalContextualHttpServletRequest(req) {

            @Override
            protected void workInTransaction() throws Exception
            {
                String rateParam = req.getParameter("rate");
                String courseIdParam = req.getParameter("courseId");
                if (StringUtil.isNotEmpty(rateParam) && StringUtil.isNotEmpty(courseIdParam))
                {
                    int rateVal = Integer.parseInt(rateParam);
                    long courseId = Long.parseLong(courseIdParam);
                    Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
                    if (uid != null)
                    {
                        CourseRating cr = new CourseRating();
                        cr.setStar(rateVal);
                        try
                        {
                            CourseRating rating = (CourseRating) getEntityManager()
                                    .createQuery("select cr from CourseRating cr where cr.member.id = :memId AND cr.course.id = :courseId")
                                    .setParameter("memId", uid).setParameter("courseId", courseId).setMaxResults(1).getSingleResult();
                            cr = rating;
                            getEntityManager().createQuery("update CourseRating cr set cr.star = :rate where cr.member.id = :memId AND cr.course.id = :courseId")
                                    .setParameter("memId", uid).setParameter("courseId", courseId).setParameter("rate", rateVal).executeUpdate();
                        }
                        catch (NoResultException e)
                        {
                            Course c = new Course();
                            c.setId(courseId);
                            cr.setCourse(c);
                            Member m = new Member();
                            m.setId(uid);
                            cr.setMember(m);
                            getEntityManager().persist(cr);
                        }
                        getEntityManager().flush();
                        //
                        Session session = (Session) Component.getInstance("hibernateSession");
                        Criteria criteria = session.createCriteria(CourseRating.class, "cr");
                        criteria.setProjection(Projections.projectionList().add(Projections.groupProperty("star")).add(Projections.rowCount()));
                        List<Object[]> ratings = criteria.add(Restrictions.eq("cr.course.id", courseId)).list();
                        if (CollectionUtil.isNotEmpty(ratings))
                        {
                            double soratKasr = 0;
                            double makhrajeKasr = 0;
                            for (Object[] rate : ratings)
                            {
                                int star = Integer.parseInt(String.valueOf(rate[0]));
                                Long starCount = new Long(String.valueOf(rate[1]));
                                soratKasr = soratKasr + (star * starCount);
                                makhrajeKasr = makhrajeKasr + starCount;
                            }
                            if (makhrajeKasr != 0)
                            {
                                int result = (int) (soratKasr / makhrajeKasr);
                                resp.setHeader("Content-Type", "text/plain");
                                PrintWriter writer = resp.getWriter();
                                writer.write(String.valueOf(result));
                                writer.close();
                            }
                        }
                    }
                }
            }
        }.run();
    }

}
