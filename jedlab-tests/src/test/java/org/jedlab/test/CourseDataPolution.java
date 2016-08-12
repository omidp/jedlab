package org.jedlab.test;

import java.io.IOException;
import java.math.BigDecimal;

import javax.persistence.EntityManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.Component;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jedlab.dao.home.CourseHome;
import com.jedlab.framework.TxManager;
import com.jedlab.model.Course;
import com.jedlab.model.Course.Language;
import com.jedlab.model.Course.Level;

@RunWith(Arquillian.class)
public class CourseDataPolution extends JUnitSeamTest
{

    @Deployment()
    @OverProtocol("Servlet 3.0")
    public static WebArchive seamDeployment() throws IOException
    {
        WebArchive archive = DeploymentResolver.simpleSeamDeployment();        
        archive.addPackages(true, "com.jedlab.model");
        archive.addPackages(true, "com.jedlab.framework");
//        archive.addClass(CourseHome.class);
        return archive;
    }
    
    @Test
    public void insertTest() throws Exception
    {
        new ComponentTest() {
            
            @Override
            protected void testComponents() throws Exception
            {
                TxManager.beginTransaction();
                EntityManager p  = (EntityManager) Component.getInstance("entityManager");
                for (int i = 0; i < 100; i++)
                {
                    Course prog = new Course();
                    prog.setActive(true);
                    prog.setName("course " + i);
                    prog.setLanguage(Language.PERSIAN);
                    prog.setLevel(Level.BEGINNER);
                    prog.setPrice(BigDecimal.ZERO);
                    p.persist(prog);
                }
                TxManager.commitTransaction();
            }
        }.run();
    }
    
}
