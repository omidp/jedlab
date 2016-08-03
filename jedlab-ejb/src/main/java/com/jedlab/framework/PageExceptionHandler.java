package com.jedlab.framework;

import javax.faces.application.FacesMessage;

import org.jboss.seam.annotations.ApplicationException;
import org.jboss.seam.annotations.exception.Redirect;
import org.jboss.seam.exception.ParametricExceptionHandler;
import org.jboss.seam.international.StatusMessage.Severity;



/**
 * @author Omid Pourhadi
 *
 * omidpourhadi [AT] gmail [DOT] com
 */
@ApplicationException(end=true)
@Redirect(includePageParameters = true)
public class PageExceptionHandler extends ParametricExceptionHandler 
{

    
    private Severity severity = Severity.ERROR;
    
    public PageExceptionHandler(String message)
    {
        super(message);
    }

    public PageExceptionHandler(String message, Severity severity)
    {
        super(message);
        this.severity = severity;
    }
    
    @Override
    protected javax.faces.application.FacesMessage.Severity getMessageSeverity()
    {
        switch (severity)
        {
        case ERROR:
           return FacesMessage.SEVERITY_ERROR;
        case FATAL:
           return FacesMessage.SEVERITY_FATAL;
        case INFO:
           return FacesMessage.SEVERITY_INFO;
        case WARN:
           return FacesMessage.SEVERITY_WARN;
        default:
           return null;
        }
    }
    
    
}
