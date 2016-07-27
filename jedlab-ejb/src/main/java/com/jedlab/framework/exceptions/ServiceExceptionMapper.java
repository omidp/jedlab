package com.jedlab.framework.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ServiceExceptionMapper implements ExceptionMapper<ServiceException>
{

    private static Logger log = LoggerFactory.getLogger(ServiceExceptionMapper.class);

    @Override
    public Response toResponse(ServiceException se)
    {
        log.info("EXCEPTION IS : " + se.getMessage());
        return CloudException.toResponse(se.getCode(), se.getMessage());
    }

}
