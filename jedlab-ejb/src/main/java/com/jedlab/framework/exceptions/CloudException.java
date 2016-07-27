package com.jedlab.framework.exceptions;

import javax.ws.rs.core.Response;

import org.omidbiz.core.axon.AxonBuilder;

public class CloudException
{

    
    public static Response toResponse(int code, String msg)
    {
        StringBuilder sb = buildError(code, msg);
        return Response.status(Response.Status.BAD_REQUEST).entity(sb.toString()).build();
    }

    
    private static StringBuilder buildError(int code, String msg)
    {
        ErrorMessage errorMessage = new ErrorMessage(code, msg);
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"errors\" : [");
        String jsonError = new AxonBuilder().useWithPrettyWriter().create().toJson(errorMessage);
        sb.append(jsonError);
        sb.append("]}");
        return sb;
    }
    
}
