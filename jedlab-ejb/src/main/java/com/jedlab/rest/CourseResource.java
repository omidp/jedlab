package com.jedlab.rest;

import java.net.URI;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.framework.StringUtil;

@Path("/c")
@Name("courseResource")
@Scope(ScopeType.EVENT)
public class CourseResource
{

    @In
    EntityManager entityManager;

    @Context
    private UriInfo uriInfo;

    @Context
    HttpServletRequest request;

    @Path("{courseName}")
    @GET
    public Response findCourseByName(@PathParam("courseName") String courseName)
    {
        URI ru = uriInfo.getRequestUri();
        String uri = String.format("%s://%s:%s%s/", ru.getScheme(), ru.getHost(), ru.getPort(), request.getContextPath());
        if (StringUtil.isEmpty(courseName))
        {
            uri += "error.seam";
        }
        else
        {
            try
            {
                Long courseId = (Long) entityManager.createQuery("select c.id from Course c where c.name = :cname").setMaxResults(1)
                        .setParameter("cname", courseName).getSingleResult();
                uri += String.format("course/%s", courseId);
            }
            catch (NoResultException e)
            {
                uri += "error.seam";
            }
        }
        return Response.seeOther(UriBuilder.fromUri(uri).build()).build();

    }
}
