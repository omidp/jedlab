package com.jedlab.rest;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.dao.home.PageHome;
import com.jedlab.framework.StringUtil;
import com.jedlab.model.Curate;
import com.jedlab.model.Page;
import com.jedlab.model.PageBlock;

@Path("/pages")
@Name("pageResource")
@Scope(ScopeType.EVENT)
public class PageResource implements Serializable
{

    @In
    EntityManager entityManager;

    @Context
    private UriInfo uriInfo;

    @Context
    HttpServletRequest request;

    
    @In(create=true)
    PageHome pageHome;
    
    @POST    
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPage(Page page)
    {
        if(page == null || StringUtil.isEmpty(page.getTitle()))
            return Response.status(Status.BAD_REQUEST).build();
        pageHome.setInstance(page);        
        pageHome.persist();
        return Response.ok(page).build();
    }
    
    @Path("/blocks")
    @POST    
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPageBlock(Page page)
    {
        if(page == null || page.getId() == null)
            return Response.status(Status.BAD_REQUEST).build();
        PageBlock pb = pageHome.createPageBlock(page);
        return Response.ok(pb).build();
    }
    
    @Path("/blocks/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTitle(@PathParam("id") Long id, PageBlock pageBlock)
    {
        if(id == null || StringUtil.isEmpty(pageBlock.getTitle()))
            return Response.status(Status.BAD_REQUEST).build();
        pageHome.updatePageBlockTitle(id, pageBlock.getTitle());
        return Response.ok().build();
    }
    
    
    @Path("/curates")
    @POST    
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCurate(Curate curate)
    {
        if(curate == null 
                || curate.getPageBlock() == null
                || curate.getPageBlock().getId() == null)
            return Response.status(Status.BAD_REQUEST).build();
        Curate c = pageHome.createCurate(curate);
        return Response.ok(c).build();
    }

}
