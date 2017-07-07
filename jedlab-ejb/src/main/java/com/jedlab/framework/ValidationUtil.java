package com.jedlab.framework;

import javax.validation.ConstraintViolation;

import org.hibernate.validator.engine.ConstraintViolationImpl;

/**
 * @author Omid Pourhadi
 *
 */
public class ValidationUtil
{

    
    public static ConstraintViolation<Object> addInvalidValue(String property, Class beanClass, String message)
    {
        ConstraintViolation<Object> cv = new ConstraintViolationImpl<Object>(message, message, beanClass, beanClass, null, property, null,
                null, null);
        return cv;
    }
    
}
