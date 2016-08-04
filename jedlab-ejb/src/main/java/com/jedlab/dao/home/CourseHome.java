package com.jedlab.dao.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityHome;

import com.jedlab.model.Course;

@Name("courseHome")
@Scope(ScopeType.CONVERSATION)
public class CourseHome extends EntityHome<Course>
{

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
        if (isIdDefined())
        {
            wire();
        }
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

    @Override
    protected Course loadInstance()
    {
        try
        {

            return (Course) getEntityManager()
                    .createQuery("select c from Course c LEFT OUTER JOIN c.chapters chapters where c.id = :courseId")
                    .setParameter("courseId", getId()).getSingleResult();
        }
        catch (Exception e)
        {
        }
        return null;
    }

}
