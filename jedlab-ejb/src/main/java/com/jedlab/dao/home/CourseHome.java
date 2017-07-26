package com.jedlab.dao.home;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.security.Identity;

import com.jedlab.JedLab;
import com.jedlab.action.Constants;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.PageExceptionHandler;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TxManager;
import com.jedlab.framework.WebUtil;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;
import com.jedlab.model.CourseRating;
import com.jedlab.model.Instructor;
import com.jedlab.model.Member;
import com.jedlab.model.MemberCourse;
import com.jedlab.model.Preview;
import com.jedlab.model.Tag;

@Name("courseHome")
@Scope(ScopeType.CONVERSATION)
public class CourseHome extends EntityHome<Course>
{

    private final static Pattern p = Pattern.compile("-?\\d+");

    public void setCourseId(Long id)
    {
        setId(id);
    }

    public Long getCourseId()
    {
        return (Long) getId();
    }

    @Override
    protected Course createInstance()
    {
        Course course = new Course();
        course.setPrice(BigDecimal.ZERO);
        course.setDownloadPrice(1000);
        return course;
    }

    public void load()
    {
        if (isIdDefined())
        {
            TxManager.beginTransaction();
            TxManager.joinTransaction(getEntityManager());
            getEntityManager().createQuery("update Course c set c.viewCount = c.viewCount+1 where c.id = :courseId")
                    .setParameter("courseId", getCourseId()).executeUpdate();
            getEntityManager().flush();
        }
    }

    private void wire()
    {
        if(isIdDefined())
        {
            if(Identity.instance().hasRole(Constants.ROLE_ADMIN))
            {
                if(getInstance().isPublished() == false)
                {
                	getInstance().setPublished(true);
                	getInstance().setActive(true);
                }
            }
            if(Identity.instance().hasRole(Constants.ROLE_INSTRUCTOR) && !Identity.instance().hasRole(Constants.ROLE_ADMIN))
            {
               getInstance().setPublished(false);
               getInstance().setActive(false);
            }
        }
    }

    public boolean isWired()
    {
        return true;
    }

    public Course getDefinedInstance()
    {
        return isIdDefined() ? getInstance() : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Course loadInstance()
    {
        try
        {

            Course course = (Course) getEntityManager()
                    .createQuery("select c from Course c LEFT OUTER JOIN c.chapters chapters where c.id = :courseId")
                    .setParameter("courseId", getId()).getSingleResult();
            try
            {
                this.preview = (Preview) getEntityManager().createQuery("select p from Preview p where p.course.id = :courseId")
                        .setParameter("courseId", course.getId()).setMaxResults(1).getSingleResult();
            }
            catch (NoResultException e)
            {
            }
            calculateRating();
            if (Identity.instance().isLoggedIn())
            {
                Long uid = (Long) getSessionContext().get(Constants.CURRENT_USER_ID);
                //
                List<MemberCourse> memberCourseList = getEntityManager()
                        .createQuery(
                                "select mc from MemberCourse mc LEFT OUTER JOIN mc.chapter chapter where mc.member.id = :memId AND mc.course.id = :courseId")
                        .setParameter("memId", uid).setParameter("courseId", getId()).getResultList();
                List<Chapter> registeredCourses = new ArrayList<>();
                for (MemberCourse memberCourse : memberCourseList)
                {
                    Chapter chapter = memberCourse.getChapter();
                    if (memberCourse.isViewed())
                        chapter.setViewed(true);
                    if (memberCourse.isCanDownload())
                        chapter.setCanDownload(true);
                    if (memberCourse.isPaid())
                    {
                        chapter.setPaid(true);
                        chapter.setCanDownload(true);
                    }
                    registeredCourses.add(chapter);
                }
                // /
                List<Chapter> chapters = course.getChapters();
                boolean registerInCourse = false;
                if (CollectionUtil.isNotEmpty(chapters))
                {
                    for (Chapter chapter : registeredCourses)
                    {
                        for (Chapter item : chapters)
                        {
                            if (chapter.getId().longValue() == item.getId().longValue())
                            {
                                item.setRegistered(true);
                                if (chapter.isViewed())
                                    item.setViewed(true);
                                registerInCourse = true;
                            }
                        }
                    }
                }
                if (registerInCourse && registeredCourses.size() == chapters.size())
                    course.setRegistered(registerInCourse);
            }

            return course;
        }
        catch (Exception e)
        {
        }
        return null;
    }

    private void calculateRating()
    {
        if (isIdDefined())
        {
            Session session = (Session) Component.getInstance("hibernateSession");
            Criteria criteria = session.createCriteria(CourseRating.class, "cr");
            criteria.setProjection(Projections.projectionList().add(Projections.groupProperty("star")).add(Projections.rowCount()));
            List<Object[]> ratings = criteria.add(Restrictions.eq("cr.course.id", getId())).list();
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
                    this.rating = result;
                }
            }
        }
    }

    public String register()
    {
        String courseParamId = WebUtil.getParameterValue("courseId");
        if (StringUtil.isEmpty(courseParamId))
        {
            throw new PageExceptionHandler("unable to find course");
        }
        if (Identity.instance().isLoggedIn() == false)
        {
            throw new PageExceptionHandler("unable to find course");
        }
        try
        {
            Long uid = (Long) getSessionContext().get(Constants.CURRENT_USER_ID);
            Long courseId = Long.parseLong(courseParamId);
            Course course = getEntityManager().find(Course.class, courseId);
            if (course.isFree())
            {
                boolean freeForDownload= course.isFreeForDownload();
                List<Chapter> chapters = getEntityManager()
                        .createQuery(
                                "select c from Chapter c  where c.course.id = :courseId AND c.id  NOT IN (select mc.chapter.id from MemberCourse mc where mc.course.id = c.course.id AND mc.member.id = :memId)")
                        .setParameter("courseId", courseId).setParameter("memId", uid).getResultList();
                if (CollectionUtil.isNotEmpty(chapters))
                {
                    for (Chapter chapter : chapters)
                    {
                        MemberCourse mc = new MemberCourse();
                        mc.setChapter(chapter);
                        mc.setCourse(course);
                        //
                        Member m = new Member();
                        m.setId(uid);
                        mc.setMember(m);
                        if(freeForDownload)
                            mc.setCanDownload(true);
                        getEntityManager().persist(mc);
                    }
                }
                return "registered";
            }
            else
            {
                return "redirectToPaymentGateway";
            }
        }
        catch (NoResultException e)
        {
            // throw new
            // PageExceptionHandler(StatusMessage.getBundleMessage("Enroll_Error",
            // ""));
        }
        return "notRegistered";
    }

    @Transactional
    public String saveTags()
    {
        String tags = WebUtil.getParameterValue("tags");
        if (StringUtil.isNotEmpty(tags))
        {
            Matcher matcher = p.matcher(tags);
            Set<Long> ids = new HashSet<Long>();
            while (matcher.find())
            {
                ids.add(Long.valueOf(matcher.group()));
            }
            //
            StringTokenizer token = new StringTokenizer(tags.replaceAll("-?\\d+", ""), ",");
            List<String> newTags = new ArrayList<>();
            while (token.hasMoreElements())
            {
                String t = (String) token.nextElement();
                if (t != null && !",".equals(t))
                {
                    newTags.add(t);
                }
            }
            for (String tagName : newTags)
            {
                Tag t = new Tag(tagName);
                getEntityManager().persist(t);
                ids.add(t.getId());
            }
            List<Tag> tagList = getEntityManager().createQuery("select t from Tag t where t.id IN :ids").setParameter("ids", ids)
                    .getResultList();
            getInstance().getTags().clear();
            for (Tag tag : tagList)
            {
                getInstance().getTags().add(tag);
            }
            getStatusMessages().addFromResourceBundle("Created");
        }
        getEntityManager().flush();
        return "persisted";
    }

    private String currentView;

    public String getCurrentView()
    {
        if (currentView != null)
            return currentView;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String viewId = Pages.getCurrentViewId();
        String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext, Pages.getCurrentViewId());
        url = Pages.instance().encodeScheme(viewId, facesContext, url);
        url = url.substring(0, url.lastIndexOf("/") + 1);
        currentView = url + String.format("course/%d", getCourseId());
        return currentView;
    }

    Preview preview;

    private Integer rating;

    public Integer getRating()
    {
        if (rating == null)
            rating = 1;
        return rating;
    }

    public Preview getPreview()
    {
        if (preview == null)
            preview = new Preview();
        return preview;
    }

    @Override
    @Transactional
    public String persist()
    {
        wire();
        Instructor instructor = new Instructor();
        instructor.setId(JedLab.instance().getCurrentUserId());
        getInstance().setInstructor(instructor);
        super.persist();
        if (StringUtil.isNotEmpty(getPreview().getUrl()))
        {
            getPreview().setCourse(getInstance());
            getEntityManager().persist(getPreview());
            getEntityManager().flush();
        }
        return "persisted";
    }

    @Override
    @Transactional
    public String update()
    {
        wire();
        Instructor instructor = new Instructor();
        instructor.setId(JedLab.instance().getCurrentUserId());
        getInstance().setInstructor(instructor);
        super.update();
        if (getPreview().getId() == null)
        {
            if (StringUtil.isNotEmpty(getPreview().getUrl()))
            {
                getPreview().setCourse(getInstance());
                getEntityManager().persist(getPreview());
                getEntityManager().flush();
            }
        }
        return "updated";
    }

}
