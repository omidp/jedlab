package com.jedlab.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ProcessModel implements Serializable
{
    private String view;
    private ProcessModelType type;
    private Map<String, Object> variables;
    private Long processId;
    private Long taskId;

    public ProcessModel()
    {
    }

    public ProcessModel(String view, ProcessModelType type)
    {
        this.view = view;
        this.type = type;
    }

    public ProcessModelType getType()
    {
        return type;
    }

    public Map<String, Object> getVariables()
    {
        if (variables == null)
            variables = new HashMap<>();
        return variables;
    }

    public String getView()
    {
        return view;
    }

    public void setView(String view)
    {
        this.view = view;
    }

    public Long getProcessId()
    {
        return processId;
    }

    public void setProcessId(Long processId)
    {
        this.processId = processId;
    }

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }
    
    public boolean isJedlabType()
    {
        return ProcessModelType.JEDLAB.equals(getType());
    }

}