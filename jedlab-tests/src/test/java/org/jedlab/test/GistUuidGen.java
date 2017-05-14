package org.jedlab.test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.junit.Test;

public class GistUuidGen extends DBConnection
{
    
    @Test
    public void test() throws SQLException
    {
        PreparedStatement ps = c.prepareStatement("select id from gist");
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        {
            System.out.println(String.format("update gist set uuid='%s' where id=%s;", UUID.randomUUID(), rs.getLong("id")));
        }
    }
    
}
