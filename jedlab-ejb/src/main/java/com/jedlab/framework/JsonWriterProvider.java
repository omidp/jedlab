package com.jedlab.framework;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.omidbiz.core.axon.Axon;
import org.omidbiz.core.axon.filters.RecursionControlFilter;
import org.omidbiz.core.axon.hibernate.AxonBuilder;

import com.jedlab.model.Course;

/**
 * @author Omid Pourhadi
 *
 * omidpourhadi [AT] gmail [DOT] com
 */
@Provider
@Consumes("application/json")
@Produces("application/json")
public class JsonWriterProvider implements MessageBodyWriter<Object>
{

    public static final Axon axon = new AxonBuilder().addFilter(new RecursionControlFilter()).create();
    
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
//        return type.isAnnotationPresent(AxonSerializer.class);
        return true;
    }

    @Override
    public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {        
//        String json = axon.toJson(t);
//        return json.getBytes().length;
        return -1;
    }

    @Override
    public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
    {
        String json = axon.toJson(t);
        IOUtils.write(json.getBytes("UTF-8"), entityStream);
        IOUtils.closeQuietly(entityStream);
    }
    
    

}
