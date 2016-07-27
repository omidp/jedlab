package com.jedlab.framework;

import org.jboss.seam.annotations.ApplicationException;
import org.jboss.seam.annotations.exception.Redirect;
import org.jboss.seam.exception.ParametricExceptionHandler;



/**
 * @author Omid Pourhadi
 *
 * omidpourhadi [AT] gmail [DOT] com
 */
@ApplicationException(end=true)
@Redirect(includePageParameters = true, viewId="/error.xhtml")
public class ErrorPageExceptionHandler extends ParametricExceptionHandler 
{

    public ErrorPageExceptionHandler(String message)
    {
        super(message);
    }

    
    
}
