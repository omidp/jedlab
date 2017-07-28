package com.jedlab.framework;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.validation.ConstraintViolation;

import org.jboss.seam.annotations.ApplicationException;
import org.jboss.seam.annotations.exception.Redirect;
import org.jboss.seam.exception.ParametricExceptionHandler;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessages;

@Redirect(viewId = "/error.xhtml", includePageParameters = true)
@ApplicationException(rollback = true)
public class FrameworkExceptionHandler extends ParametricExceptionHandler
{

    public FrameworkExceptionHandler()
    {
        super("‬");
    }

    public FrameworkExceptionHandler(String message)
    {
        super(message);
    }

    public FrameworkExceptionHandler(Map<String, Object> parameters)
    {
        super("‬", parameters);
    }

    public FrameworkExceptionHandler(String message, Map<String, Object> parameters)
    {
        super(message, parameters);
    }
    
    public FrameworkExceptionHandler(List<String> messageList, Map<String, Object> parameters)
    {
        super(StatusMessage.getBundleMessage("Number_Of_Error_Messages", "") + (CollectionUtil.isNotEmpty(messageList) ? messageList.size() : "0"), parameters);
        StatusMessages statusMessages = StatusMessages.instance();
        for (String msg : messageList)
        {
            statusMessages.add(msg);
        }
    }
    
    public FrameworkExceptionHandler(List<String> messageList)
    {
        super(StatusMessage.getBundleMessage("Number_Of_Error_Messages", "") + (CollectionUtil.isNotEmpty(messageList) ? messageList.size() : "0"));
        StatusMessages statusMessages = StatusMessages.instance();
        for (String msg : messageList)
        {
            statusMessages.add(msg);
        }
    }

    public FrameworkExceptionHandler(Set<ConstraintViolation<Object>> constraintViolations)
    {
        super(StatusMessage.getBundleMessage("Number_Of_Error_Messages", "") + (CollectionUtil.isNotEmpty(constraintViolations) ? constraintViolations.size() : "0"));
        StatusMessages statusMessages = StatusMessages.instance();
        statusMessages.add(constraintViolations);
    }
    
    @Override
    protected Severity getMessageSeverity()
    {
        return FacesMessage.SEVERITY_ERROR;
    }

}
