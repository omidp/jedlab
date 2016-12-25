package com.jedlab.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.jboss.seam.Component;
import org.jboss.seam.core.Expressions;
import org.omidbiz.core.axon.Axon;
import org.omidbiz.core.axon.filters.RecursionControlFilter;
import org.omidbiz.core.axon.hibernate.AxonBuilder;

import com.jedlab.model.EntityModel;

/**
 * @author Omid Pourhadi
 *
 *         omidpourhadi [AT] gmail [DOT] com
 */
@Provider
@Consumes("application/json")
@Produces("application/json")
public class JsonReaderProvider implements MessageBodyReader<Object>
{

    @Context
    HttpServletRequest request;
    
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
//        return type.isAnnotationPresent(AxonSerializer.class);
        return true;
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
    {
        StringWriter w = new StringWriter();
        IOUtils.copy(entityStream, w, "UTF-8");
        String content = java.net.URLDecoder.decode(w.toString(), "UTF-8");
        final Axon axon = (Axon) Expressions.instance().createValueExpression("#{axon}").getValue();
        Object object = axon.toObject(content, type, null);
        if (request != null && "PUT".equals(request.getMethod()))
        {
            if(object instanceof EntityModel)
            {
                EntityModel em = (EntityModel) object;
                EntityManager entityManager =  (EntityManager) Component.getInstance("entityManager");
                Object updateModel = entityManager.find(type, em.getId());
                return axon.toObject(content, type, updateModel);
            }
        }
        IOUtils.closeQuietly(w);
        IOUtils.closeQuietly(entityStream);
        return object;
    }

}
