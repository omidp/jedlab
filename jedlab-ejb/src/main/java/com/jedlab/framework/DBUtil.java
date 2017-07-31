package com.jedlab.framework;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.framework.EntityController;


@Name("dbUtil")
@Scope(ScopeType.EVENT)
@Install(true)
@BypassInterceptors
public class DBUtil extends EntityController
{

    public <T> T executeScalar(String sql, Class<T> type, Object... params)
    {
        ScalarWork<T> sw = new ScalarWork<T>(sql, params);
        getSession().doWork(sw);
        getLog().info("executing : {0} ", sql);
        return sw.getResult();
    }

    public List<Map<String, Object>> executeQuery(String sql, Object... params)
    {
        DbWork dbWork = new DbWork(sql, params);
        getSession().doWork(dbWork);
        getLog().info("executing : {0} ", sql);
        return dbWork.getResult();
    }

    public <T> T executeQuery(Class<T> clz, String sql, Object... params)
    {
        DbObjectWork<T> dbWork = new DbObjectWork<T>(sql, params, clz);
        getSession().doWork(dbWork);
        getLog().info("executing : {0} ", sql);
        return dbWork.getInstance();
    }

    public <E> List<E> executeQueryList(Class<E> clz, String sql, Object... params)
    {
        DbListWork<E> dbWork = new DbListWork<E>(sql, params, clz);
        getSession().doWork(dbWork);
        getLog().info("executing : {0} ", sql);
        return dbWork.getInstance();
    }

    public <E> List<E> executeQueryListSingleColumnPrimitive(Class<E> clz, String sql, Object... params)
    {
        List<E> arr = new ArrayList<E>();
        List<Map<String, Object>> query = executeQuery(sql, params);
        if (CollectionUtil.isEmpty(query))
            return arr;
        String key = query.iterator().next().keySet().iterator().next();
        for (Map<String, Object> map : query)
        {
            Object val = map.get(key);
            if(val != null)
                arr.add(ReflectionUtil.cast(val, clz));
        }        
        return arr;
    }
    
    public void executeUpdate(String sql, final Object... params)
    {
        getLog().info("executing : {0} ", sql);
        DbWorker w = new DbWorker(sql) {

            @Override
            public void exec(Connection con, String q) throws SQLException
            {
                QueryRunner qr = new QueryRunner();
                qr.update(con, q, params);
            }
        };
        getSession().doWork(w);
    }

    private Session getSession()
    {
        return (Session) Component.getInstance("hibernateSession");
    }

    public static DBUtil instance()
    {

        DBUtil instance = (DBUtil) Component.getInstance(DBUtil.class, ScopeType.EVENT);

        if (instance == null)
        {
            throw new IllegalStateException("No DBUtil could be created");
        }

        return instance;
    }

}
