package com.jedlab.dao.home;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.security.Identity;

import com.jedlab.action.Constants;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.PageExceptionHandler;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.WebUtil;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;
import com.jedlab.model.Member;
import com.jedlab.model.MemberCourse;
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
        return course;
    }

    public void load()
    {

    }

    private void wire()
    {

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
                course.setRegistered(registerInCourse);
            }

            return course;
        }
        catch (Exception e)
        {
        }
        return null;
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
            Course course = (Course) getEntityManager()
                    .createQuery("select c from Course c LEFT OUTER JOIN c.chapters chapters where c.id = :courseId")
                    .setParameter("courseId", Long.parseLong(courseParamId)).getSingleResult();
            if (course.isFree())
            {
                List<Chapter> chapters = course.getChapters();
                if (CollectionUtil.isNotEmpty(chapters))
                {
                    Long uid = (Long) getSessionContext().get(Constants.CURRENT_USER_ID);
                    for (Chapter chapter : chapters)
                    {
                        MemberCourse mc = new MemberCourse();
                        mc.setChapter(chapter);
                        mc.setCourse(course);
                        //
                        Member m = new Member();
                        m.setId(uid);
                        mc.setMember(m);
                        getEntityManager().persist(mc);
                    }
                }
                return "registered";
            }
        }
        catch (NoResultException e)
        {
            throw new PageExceptionHandler("emkane sabte nam vojod nadarad");
        }
        return "notRegistered";
    }

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
        return "persisted";
    }

}
