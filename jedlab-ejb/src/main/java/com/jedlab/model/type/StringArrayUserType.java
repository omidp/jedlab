package com.jedlab.model.type;

import java.io.Serializable;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * @author Omid Pourhadi
 *
 */
public class StringArrayUserType implements UserType
{

    protected static final int[] SQL_TYPES = { java.sql.Types.ARRAY };

    @Override
    public int[] sqlTypes()
    {
        return SQL_TYPES;
    }

    @Override
    public Class returnedClass()
    {
        return String[].class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException
    {
        if (x == y)
        {
            return true;
        }
        else if (x == null || y == null)
        {
            return false;
        }
        else
        {
            return x.equals(y);
        }
    }

    @Override
    public int hashCode(Object x) throws HibernateException
    {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException
    {
        if (rs.wasNull())
        {
            return null;
        }
        Array rsArr = rs.getArray(names[0]);
        if (rsArr == null)
            return null;
        String[] array = (String[]) rsArr.getArray();
        return array;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException
    {
        if (value == null)
        {
            st.setNull(index, SQL_TYPES[0]);
        }
        else
        {
            String[] castObject = (String[]) value;
            Array array = st.getConnection().createArrayOf("text", castObject);
            st.setArray(index, array);
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException
    {
        return value;
    }

    @Override
    public boolean isMutable()
    {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException
    {
        return null;
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException
    {
        return null;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException
    {
        return original;
    }

}
