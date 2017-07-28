package com.jedlab.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.validation.ConstraintViolationException;

import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.JDBCException;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.HibernateEntityController;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import com.jedlab.framework.jsf.FlashScope;

/**
 * @author Omid Pourhadi
 *
 */
@Scope(ScopeType.CONVERSATION)
public abstract class AbstractDeleteController extends HibernateEntityController
{

    /**
     * only dependency that has records
     */
    private List<Dependency> dependencyList = new ArrayList<Dependency>();

    /**
     * root value for select and delete queries
     */
    private List<Long> entityValues;

    /**
     * hiberante entity class
     */
    private Map<String, Class<?>> mappedClassList = new HashMap<String, Class<?>>();

    public static final String HAS_DEPENDENCY_FLOW = "hasDepFlow";

    public void loadDependency()
    {
        dependencyList.clear();
        setEntityValues(acquireDeleteIds());
        validate();
        createHibernateMetaData();
        createDependencyList();
    }

    private void createHibernateMetaData()
    {
        Map<String, ClassMetadata> metadata = getSession().getSessionFactory().getAllClassMetadata();
        for (Entry<String, ClassMetadata> item : metadata.entrySet())
        {
            Class<?> mappedClass = item.getValue().getMappedClass(EntityMode.POJO);
            Table tbl = (Table) mappedClass.getAnnotation(Table.class);
            if (tbl != null)
            {
                mappedClassList.put(tbl.name(), mappedClass);
            }
        }

    }

    protected abstract List<Long> acquireDeleteIds();

    private void validate()
    {
        if (getEntityClass() == null || CollectionUtil.isEmpty(getEntityValues()))
            throw new FrameworkExceptionHandler("entity/values can not be null", WebUtil.getPageParameters());
    }

    private void createDependencyList()
    {
        String deleteQ = XmlParser.findNativeQuery("framework", "deleteBuilder");
        getLog().info("parsing : " + getTableName());
        List<Map<String, Object>> query = DBUtil.instance().executeQuery(deleteQ, getTableName());
        for (Map<String, Object> map : query)
        {
            String ft = (String) map.get("foreign_table");
            String fc = (String) map.get("foreign_column");
            String mt = (String) map.get("main_table");
            String rc = (String) map.get("ref_column");
            Dependency dependency = new Dependency(ft, fc, mt, rc);
            boolean hasFlow = hasWorkflowForm(ft);
            if (hasFlow)
            {
                dependency.setHasWorkflow(true);
            }
            boolean hasRecord = hasRecord(dependency);
            if (hasRecord)
            {
                dependency.setLabel(Messages.instance().get(ft));
                dependencyList.add(dependency);
            }
            if (hasFlow && hasRecord)
                getPageContext().set(HAS_DEPENDENCY_FLOW, true);
        }
    }

    private boolean hasRecord(Dependency dependency)
    {
        String sql = String
                .format("select count(*) from %s where %s ", dependency.getForeignTableName(), dependency.getForeignColumnName());
        StringBuilder sb = new StringBuilder().append(sql);
        sb.append(" IN ( ").append(CollectionUtil.commaSeparated(getEntityValues())).append(" )");
        Long scalar = DBUtil.instance().executeScalar(sb.toString(), Long.class);
        if (scalar != null && scalar.longValue() > 0)
        {
            dependency.setRecordCount(scalar);
            return true;
        }
        return false;
    }

    @Transactional
    public String doDelete()
    {
        Dependency catchDep = null;
        String outcome = "deleted";
        try
        {
            // dependecy is available
            beforeDelete();
            for (Dependency dependency : dependencyList)
            {
                catchDep = dependency;
                Class<?> clz = mappedClassList.get(dependency.getForeignTableName());
                dependency.setEntityClass(clz);
                beforeDependencyDelete(dependency);
                if (dependency.isHasWorkflow())
                {
                    throw new FrameworkExceptionHandler(interpolate(
                            StatusMessage.getBundleMessage("Delete_Dep_Has_Wf", "Delete_Dep_Has_Wf"), dependency.getLabel()),
                            WebUtil.getPageParameters());
                }
                if (clz != null)
                {
                    Criteria criteria = getSession().createCriteria(clz);
                    StringBuilder where = new StringBuilder();
                    where.append("{alias}.").append(dependency.getForeignColumnName());
                    where.append(" IN ( ").append(CollectionUtil.commaSeparated(getEntityValues())).append(" )");
                    criteria.add(Restrictions.sqlRestriction(where.toString()));
                    List<?> list = criteria.list();
                    int i = 0;
                    for (Object item : list)
                    {
                        if (i % 20 == 0)
                        {
                            i = 0;
                            getSession().flush();
                        }
                        i++;
                        getSession().delete(item);
                    }
                }
                else
                {
                    // delete nested model without separated hibernate class
                    // must be cascade
                    StringBuilder sb = new StringBuilder("delete from ").append(dependency.getForeignTableName());
                    StringBuilder where = new StringBuilder();
                    where.append(" WHERE ").append(dependency.getForeignColumnName());
                    where.append(" IN ( ").append(CollectionUtil.commaSeparated(getEntityValues())).append(" )");
                    sb.append(" ").append(where.toString());
                    // TODO : change this with hibernate
                    DBUtil.instance().executeUpdate(sb.toString());
                }
            }
            // remove main entity after deleting depencies
            Criteria criteria = getSession().createCriteria(getEntityClass());
            StringBuilder where = new StringBuilder();
            where.append("{alias}.").append(findTableIdentifierColumnName());
            where.append(" IN ( ").append(CollectionUtil.commaSeparated(getEntityValues())).append(" )");
            criteria.add(Restrictions.sqlRestriction(where.toString()));
            List<?> list = criteria.list();
            for (Object item : list)
            {
                getSession().delete(item);
            }
            //
            getSession().flush();
            getSession().clear();
            afterDelete();
            if (showDeleteMessage())
            {
                String deleteMsg = StatusMessage.getBundleMessage("Delete_Successfull", "Delete_Successfull");
                deleteMsg = deleteMsg == null ? "" : deleteMsg;
                FlashScope.instance().addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, deleteMsg, deleteMsg));
            }
        }
        catch (ConstraintViolationException e)
        {
            String tblName = extractTableName(e);
            tblName = (StringUtil.isEmpty(tblName) && catchDep != null) ? catchDep.getForeignTableName() : tblName;
            String errorMsg = interpolate(StatusMessage.getBundleMessage("Delete_Dependency_Error", "Delete_Dependency_Error"), Messages
                    .instance().get(tblName));
            outcome = null;
            FlashScope.instance().removeMessage();
            throw new ErrorPageExceptionHandler(errorMsg);
        }
        catch (org.hibernate.exception.ConstraintViolationException e)
        {
            String tblName = extractTableName(e);
            tblName = (StringUtil.isEmpty(tblName) && catchDep != null) ? catchDep.getForeignTableName() : tblName;
            String errorMsg = interpolate(StatusMessage.getBundleMessage("Delete_Dependency_Error", "Delete_Dependency_Error"), Messages
                    .instance().get(tblName));
            outcome = null;
            FlashScope.instance().removeMessage();
            throw new ErrorPageExceptionHandler(errorMsg);
        }
        dependencyList.clear();
        return outcome;
    }

    private String extractTableName(RuntimeException re)
    {
        String message = re.getCause().getMessage();
        if (re.getCause().getClass().equals(java.sql.BatchUpdateException.class))
        {
            message = ((JDBCException) re).getSQLException().getNextException().getMessage();
        }
        Pattern p = Pattern.compile("(from)\\s(table)\\s\"(.+)\"", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(message);
        if (m.find())
            return m.group(3).trim();
        return "";
    }

    protected boolean showDeleteMessage()
    {
        return true;
    }

    /**
     * check if dependency has workflow
     * 
     * @param foreignTableName
     * @return
     */
    private boolean hasWorkflowForm(String foreignTableName)
    {
        // FIXME :
        return false;
        // Form[] values = Form.values();
        // for (Form form : values)
        // {
        // String tableNames = form.getTableName();
        // String[] split = tableNames.split(",");
        // for (String tblItem : split)
        // {
        // if (tblItem.equals(foreignTableName))
        // return true;
        // }
        // }
        // return false;
    }

    protected void beforeDependencyDelete(Dependency dependency)
    {

    }

    protected void beforeDelete()
    {

    }

    protected void afterDelete()
    {

    }

    private String findTableIdentifierColumnName()
    {
        Class<?> clz = getEntityClass();
        ClassMetadata metadata = getSession().getSessionFactory().getClassMetadata(clz);
        String identifierPropertyName = metadata.getIdentifierPropertyName();
        String tableIdentifier = null;
        if (clz.isAnnotationPresent(AttributeOverrides.class))
        {
            AttributeOverrides annotation = clz.getAnnotation(AttributeOverrides.class);
            AttributeOverride[] value = annotation.value();
            for (int i = 0; i < value.length; i++)
            {
                AttributeOverride attributeOverride = value[i];
                String atrrName = attributeOverride.name();
                Column column = attributeOverride.column();
                if (identifierPropertyName.equals(atrrName))
                    tableIdentifier = column.name();
            }
        }
        if (tableIdentifier == null)
            tableIdentifier = identifierPropertyName;
        return tableIdentifier;
    }

    public static class Dependency implements Serializable
    {
        private String foreignTableName;
        private String foreignColumnName;
        private String tableName;
        private String columnName;
        private Long recordCount;
        private boolean hasWorkflow;
        /**
         * message translation
         */
        private String label;
        /**
         * can be null
         */
        private Class<?> entityClass;

        public Dependency(String foreignTableName, String foreignColumnName, String tableName, String columnName)
        {
            this.foreignTableName = foreignTableName;
            this.foreignColumnName = foreignColumnName;
            this.tableName = tableName;
            this.columnName = columnName;
        }

        public Class<?> getEntityClass()
        {
            return entityClass;
        }

        public void setEntityClass(Class<?> entityClass)
        {
            this.entityClass = entityClass;
        }

        public String getLabel()
        {
            return label;
        }

        public void setLabel(String label)
        {
            this.label = label;
        }

        public boolean isHasWorkflow()
        {
            return hasWorkflow;
        }

        public void setHasWorkflow(boolean hasWorkflow)
        {
            this.hasWorkflow = hasWorkflow;
        }

        public Long getRecordCount()
        {
            return recordCount;
        }

        public void setRecordCount(Long recordCount)
        {
            this.recordCount = recordCount;
        }

        public String getForeignTableName()
        {
            return foreignTableName;
        }

        public String getForeignColumnName()
        {
            return foreignColumnName;
        }

        public String getTableName()
        {
            return tableName;
        }

        public String getColumnName()
        {
            return columnName;
        }

    }

    private String getTableName()
    {
        Class<?> clz = getEntityClass();
        if (clz == null)
            throw new FrameworkExceptionHandler("main entity class can not be null", WebUtil.getPageParameters());
        Table tbl = clz.getAnnotation(Table.class);
        if (tbl == null)
            throw new FrameworkExceptionHandler("entity must have table annotation", WebUtil.getPageParameters());
        return tbl.name();
    }

    protected abstract Class<?> getEntityClass();

    protected List<Long> getEntityValues()
    {
        return entityValues;
    }

    protected void setEntityValues(List<Long> entityValues)
    {
        this.entityValues = entityValues;
    }

    public List<Dependency> getDependencyList()
    {
        return dependencyList;
    }

    /**
     * helper method for kill jbpm process before delete
     * 
     * @param form
     */
    protected void killAllJbpmProcess(String formName)
    {
       
    }

    public Boolean getHasDependencyFlow()
    {
        return (Boolean) getPageContext().get(HAS_DEPENDENCY_FLOW);
    }

    protected EntityManager getEntityManager()
    {
        return (EntityManager) Component.getInstance("entityManager");
    }

    @Override
    protected Log getLog()
    {
        if (super.getLog() == null)
            return Logging.getLog(AbstractDeleteController.class);
        return super.getLog();
    }

}
