package org.jedlab.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;

/**
 * @author Omid Pourhadi
 *
 */
public class DBConnection
{

    protected Connection c = null;

    @Before
    public void setUp()
    {
        try
        {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/jedlabdb", "jedlab", "jedlab");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
    @After
    public void tearDown()
    {
        try
        {
            c.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    
}
