package com.jedlab.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.bpm.BeginTask;
import org.jboss.seam.annotations.bpm.CreateProcess;
import org.jboss.seam.annotations.bpm.EndTask;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.core.Events;
import org.jboss.seam.framework.BusinessProcessController;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.navigation.Pages;
import org.jbpm.taskmgmt.exe.TaskInstance;

import com.jedlab.JedLab;
import com.jedlab.framework.ByteUtil;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.PageExceptionHandler;
import com.jedlab.framework.WebContext;
import com.jedlab.framework.WebUtil;
import com.jedlab.framework.XmlParser;
import com.jedlab.model.Course;

@Name("workflowAction")
@Scope(ScopeType.EVENT)
public class WorkflowAction extends BusinessProcessController
{

    @RequestParameter
    private Long courseId;

    @In
    EntityManager entityManager;

    public Long getCourseId()
    {
        return courseId;
    }

    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    // @CreateProcess(definition = "managerProcess")
    @Transactional
    public void sendForActivation()
    {
        if (this.courseId == null)
            this.courseId = Long.parseLong(WebUtil.getParameterValue("courseId"));
        if (this.courseId == null)
            throw new PageExceptionHandler("cours not found");
        try
        {
            Course course = (Course) entityManager.createNamedQuery(Course.FIND_WITH_INSTRUCTOR_BY_ID)
                    .setParameter("courseId", this.courseId).getSingleResult();
            if (course.getProcessInstanceId() == null)
            {
                ProcessModel pm = new ProcessModel(Pages.getCurrentViewId(), ProcessModelType.JEDLAB);
                pm.getVariables().put("courseId", this.courseId);
                getBusinessProcessContext().set(Constants.FLOW_OBJECT_NAME, ByteUtil.convertObjectToByte(pm));
                BusinessProcess.instance().createProcess(Constants.BP_PROCESS_NAME);
                course.setProcessInstanceId(BusinessProcess.instance().getProcessId());
                entityManager.flush();
                getStatusMessages().addFromResourceBundle("Workflow_Started", course.getName());
            }
            else
            {
                getStatusMessages().addFromResourceBundle("Workflow_In_Progress", course.getName());
            }
        }
        catch (NoResultException e)
        {
        }
        WebContext.instance().redirectIt(true, false);
    }

    @BeginTask(taskIdParameter = "taskId")
    @EndTask(transition = "toEnd")
    @Transactional
    public void accept()
    {
        if (this.courseId == null)
            this.courseId = Long.parseLong(WebUtil.getParameterValue("courseId"));
        if (this.courseId == null)
            throw new PageExceptionHandler("cours not found");
        Course course = (Course) entityManager.createNamedQuery(Course.FIND_WITH_INSTRUCTOR_BY_ID).setParameter("courseId", this.courseId)
                .getSingleResult();
        String content = interpolate(StatusMessage.getBundleMessage("Admin_Approve", ""), course.getName());
        Events.instance().raiseAsynchronousEvent(Constants.SEND_MAIL_ANNOUNCEMENT, content, course.getInstructor(), content);
        course.setActive(true);
        course.setPublished(true);
        course.setProcessInstanceId(null);
        entityManager.flush();
        WebContext.instance().redirectIt(true, false);
    }

    @BeginTask(taskIdParameter = "taskId")
    @EndTask(transition = "toEnd")
    @Transactional
    public void reject()
    {
        if (this.courseId == null)
            this.courseId = Long.parseLong(WebUtil.getParameterValue("courseId"));
        if (this.courseId == null)
            throw new PageExceptionHandler("cours not found");
        Course course = (Course) entityManager.createNamedQuery(Course.FIND_WITH_INSTRUCTOR_BY_ID).setParameter("courseId", this.courseId)
                .getSingleResult();
        course.setActive(false);
        course.setPublished(false);
        course.setProcessInstanceId(null);
        entityManager.flush();
        String content = interpolate(StatusMessage.getBundleMessage("Admin_Reject", ""), course.getName());
        Events.instance().raiseTransactionCompletionEvent(Constants.SEND_MAIL_ANNOUNCEMENT, content, course.getInstructor(), content);
        // Events.instance().raiseAsynchronousEvent("com.jedlab.action.announcement.sendMail",
        // content, course.getInstructor(), content);
        WebContext.instance().redirectIt(true, false);
    }

    // /////////////

    //
    private List<TaskInstance> pooledTaskInstanceList;

    private Long pooledTaskInstanceCount;

    public Long getPooledTaskInstanceCount()
    {
        if (pooledTaskInstanceCount != null)
            return pooledTaskInstanceCount;
        String hbmQuery = XmlParser.findHbmQuery("TaskInstance", "taskInstanceCount");
        Actor actor = Actor.instance();
        String actorId = actor.getId();
        if (actorId == null)
            return null;
        ArrayList groupIds = new ArrayList(actor.getGroupActorIds());
        groupIds.add(actorId);
        Query query = getJbpmSession().createQuery(hbmQuery);
        query.setParameterList("actorIds", groupIds);
        pooledTaskInstanceCount = (Long) query.uniqueResult();
        return pooledTaskInstanceCount;
    }

    private Session getJbpmSession()
    {
        return ManagedJbpmContext.instance().getSession();
    }

    @Transactional
    public List<TaskInstance> getPooledTaskInstanceList()
    {
        if (pooledTaskInstanceList != null)
            return pooledTaskInstanceList;
        Actor actor = Actor.instance();
        String actorId = actor.getId();
        if (actorId == null)
            return null;
        ArrayList groupIds = new ArrayList(actor.getGroupActorIds());
        groupIds.add(actorId);
        pooledTaskInstanceList = ManagedJbpmContext.instance().getGroupTaskList(groupIds);
        return pooledTaskInstanceList;
    }

    public List<ProcessModel> getDashboardModelList()
    {
        if (dashboardModelList != null)
            return dashboardModelList;
        List<TaskInstance> taskInstanceList = getPooledTaskInstanceList();
        if (CollectionUtil.isEmpty(taskInstanceList))
            return dashboardModelList;
        Collections.sort(taskInstanceList, new TaskInstanceSort());
        dashboardModelList = new ArrayList<ProcessModel>();
        //
        List<Long> processIds = entityManager.createQuery("select p.processId from Page p where p.processId is not null").setMaxResults(10000).getResultList();
        processIds.addAll(entityManager.createQuery("select c.processInstanceId from Course c where c.processInstanceId is not null").setMaxResults(10000).getResultList());
        //
        for (TaskInstance taskInstance : taskInstanceList)
        {
            Object variable = taskInstance.getVariable(Constants.FLOW_OBJECT_NAME);
            if (variable != null && variable.getClass().isAssignableFrom(byte[].class))
            {
                ProcessModel pm = (ProcessModel) ByteUtil.convertByteToObject((byte[]) variable);
                //
                pm.setTaskId(taskInstance.getId());
                pm.setProcessId(taskInstance.getProcessInstance().getId());
                if(processIds.contains(pm.getProcessId()))
                    dashboardModelList.add(pm);
            }
        }
        return dashboardModelList;
    }

    private List<ProcessModel> dashboardModelList;
    
    


    public static class TaskInstanceSort implements Comparator<TaskInstance>
    {

        @Override
        public int compare(TaskInstance o1, TaskInstance o2)
        {
            if (o1.getCreate() == null)
                return 0;
            if (o2.getCreate() == null)
                return 0;
            return o2.getCreate().compareTo(o1.getCreate());
        }

    }

}
