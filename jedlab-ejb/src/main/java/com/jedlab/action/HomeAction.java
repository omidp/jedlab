package com.jedlab.action;

import java.util.Collections;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.HibernateEntityController;

import com.jedlab.model.Course;

@Name("homeAction")
@Scope(ScopeType.CONVERSATION)
public class HomeAction extends HibernateEntityController
{

    private List<Course> randomCourses;

    public List<Course> getRandomCourses()
    {
        if (randomCourses != null)
            return randomCourses;
        randomCourses = getSession().createQuery("select c from Course c where c.price = 0 order by c.viewCount").setMaxResults(10).list();
        Collections.shuffle(randomCourses);
        if (randomCourses.size() > 3)
            randomCourses = randomCourses.subList(0, 3);
        return randomCourses;
    }

}
