package com.jedlab.dao.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import com.jedlab.framework.PageExceptionHandler;
import com.jedlab.model.Course;
import com.jedlab.model.CourseQuestion;
import com.jedlab.model.Question;

@Name("courseQuestionHome")
@Scope(ScopeType.CONVERSATION)
public class CourseQuestionHome extends EntityHome<CourseQuestion>
{

    @RequestParameter
    private Long courseId;

    private Course course;

    public Course getCourse()
    {
        return course;
    }

    public Long getCourseId()
    {
        return courseId;
    }

    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    public void setQuestionId(Long id)
    {
        setId(id);
    }

    public Long getQuestionId()
    {
        return (Long) getId();
    }

    public void load()
    {
        this.course = getEntityManager().find(Course.class, getCourseId());
    }

    private void wire()
    {
        if (getCourseId() == null || getCourse() == null)
            throw new PageExceptionHandler("course can not be null");
        getInstance().setCourse(getCourse());
    }
    
    
    @Override
    protected CourseQuestion createInstance()
    {
        CourseQuestion cq = new CourseQuestion();
        cq.setCourse(getCourse());
        return cq;
    }

    public boolean isWired()
    {
        return true;
    }

    public CourseQuestion getDefinedInstance()
    {
        return isIdDefined() ? getInstance() : null;
    }
    
    
    @Override
    public String persist()
    {
        wire();
        return super.persist();
    }
    
    @Override
    public String update()
    {
        wire();
        return super.update();
    }

}
