package com.jedlab.rest;

import java.io.InputStream;
import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import org.apache.commons.io.IOUtils;
import org.apache.tika.metadata.Metadata;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage;

import com.jedlab.dao.home.PageHome;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.exceptions.ServiceException;
import com.jedlab.http.HttpMethodRequest;
import com.jedlab.http.Request;
import com.jedlab.model.Curate;
import com.jedlab.model.Page;
import com.jedlab.model.PageBlock;
import com.jedlab.tika.parser.HtmlContentParser;
import com.jedlab.tika.parser.HtmlContentParser.ContentParser;

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

    @In(create = true)
    PageHome pageHome;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPage(Page page)
    {
        if (page == null || StringUtil.isEmpty(page.getTitle()))
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
        if (page == null || page.getId() == null)
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
        if (id == null || StringUtil.isEmpty(pageBlock.getTitle()))
            return Response.status(Status.BAD_REQUEST).build();
        pageHome.updatePageBlockTitle(id, pageBlock.getTitle());
        return Response.ok().build();
    }
    
    @Path("/blocks/{id}")
    @DELETE
    public Response deleteBlock(@PathParam("id") Long id)
    {
        PageBlock pb =  pageHome.findBlockById(id);
        if(pb.getPage().isOwner() == false)
            return Response.noContent().build();
        pageHome.deletePageBlock(pb);
        return Response.noContent().build();
    }

    @Path("/curates")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCurate(Curate curate)
    {
        if (curate == null 
                || curate.getPageBlock() == null 
                || curate.getPageBlock().getId() == null
                || curate.getUrl() == null)
            throw new ServiceException(100, StatusMessage.getBundleMessage("Invalid_Url", ""));
        boolean isValid = pageHome.urlIsValid(curate.getUrl());
        if (!isValid)
            throw new ServiceException(100, StatusMessage.getBundleMessage("Invalid_Url", ""));
        boolean exists =   pageHome.curateExists(curate.getUrl());
        if(exists)
            throw new ServiceException(100, StatusMessage.getBundleMessage("Url_Exists", ""));
        com.jedlab.http.Response resp = new Request(curate.getUrl(), HttpMethodRequest.GET).execute();
        if ((resp.statusCode() == 200 || resp.statusCode() == 301) == false)
            throw new ServiceException(100, StatusMessage.getBundleMessage("Invalid_Url", ""));
        InputStream is = resp.content();
        try
        {
            String content = IOUtils.toString(is);
            IOUtils.closeQuietly(is);
            ContentParser cp = HtmlContentParser.instance();
            cp.parse(content);
            Metadata metadata = cp.metaData();
            String desc = metadata.get("description");
            String title = metadata.get("title");
            String keywords = metadata.get("keywords");
            curate.setDescription(desc);
            if(resp.statusCode() == 301)
                curate.setTitle(curate.getUrl());
            else
                curate.setTitle(title);
            curate.setKeywords(keywords);
            //
            String[] names = metadata.names();
            if (names != null)
            {
                for (int i = 0; i < names.length; i++)
                {
                    String name = names[i];
                    if ("shortcut icon".equals(name) || "og:image".equals(name) || "icon".equals(name))
                    {
                        curate.setImageUrl(metadata.get(name));
                        break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new ServiceException(100, StatusMessage.getBundleMessage("Invalid_Url", ""));
        }
        Curate c = pageHome.createCurate(curate);
        return Response.ok(c).build();
    }
    
    @DELETE
    @Path("/curates/{id}")
    public Response deleteCurate(@PathParam("id") Long curateId)
    {
        Curate c = pageHome.findCurateById(curateId);
        if(c.getPage().isOwner() == false)
            return Response.noContent().build();
        pageHome.deleteCurate(c);
        return Response.noContent().build();
    }

}
