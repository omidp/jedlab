package com.jedlab.rest;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.dao.query.CourseQuery;
import com.jedlab.dao.query.MemberCourseQuery;
import com.jedlab.framework.StringUtil;
import com.jedlab.model.Course;

@Path("/c")
@Name("courseResource")
@Scope(ScopeType.EVENT)
public class CourseResource implements Serializable
{

    @In
    EntityManager entityManager;

    @Context
    private UriInfo uriInfo;

    @Context
    HttpServletRequest request;

    @In(create = true)
    CourseQuery courseQuery;
    
    @In(create=true)
    MemberCourseQuery memberCourseQuery;

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

    @Path("/auth/list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Wrapped
    public Response list(@QueryParam("start") @DefaultValue("0") int start, @QueryParam("show") @DefaultValue("25") int show)
    {
        if ((start < 0) || (show < 0))
        {
            return Response.status(BAD_REQUEST).build();
        }
        final List<Course> result = getEntityList(start, show);
        TypeUtil typeUtil = new TypeUtil(result, Course.class);
        return Response.ok(new GenericEntity(result, typeUtil.getType())).build();
    }

    public List<Course> getEntityList(int start, int show)
    {
        memberCourseQuery.setFirstResult(start);
        if (show > 0) // set 0 for unlimited
        {
            memberCourseQuery.setMaxResults(show);
        }
        return memberCourseQuery.getResultList();
    }
}
