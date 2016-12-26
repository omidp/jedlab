package com.jedlab.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.mail.internet.MimeUtility;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.Identity;

import com.jedlab.action.Constants;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;
import com.jedlab.model.MemberCourse;

@Path("/chapters")
@Name("chapterResource")
@Scope(ScopeType.EVENT)
public class ChapterResource implements Serializable
{

    @In
    EntityManager entityManager;

    @Context
    private UriInfo uriInfo;

    @Context
    HttpServletRequest request;

    @Path("/download/{chapterId}/{userId}")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadChapterById(@PathParam("chapterId") Long chapterId, @PathParam("userId") Long userId)
    {
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        if (uid == null)
            uid = userId;
        if (uid == null)
            return Response.status(Status.BAD_REQUEST).build();
        InputStream is = null;
        try
        {
            MemberCourse mc = (MemberCourse) entityManager
                    .createQuery(
                            "select mc from MemberCourse mc LEFT JOIN mc.chapter chap where mc.member.id = :memId AND chap.id = :chapterId")
                    .setParameter("memId", uid).setParameter("chapterId", chapterId).setMaxResults(1).getSingleResult();

            Chapter chapter = mc.getChapter();
            Course course = mc.getCourse();
            if (course.isFree() && !mc.isCanDownload())
                return Response.ok(StatusMessage.getBundleMessage("NOT_PAID", "NOT_PAID")).build();
            if (!course.isFree() && !mc.isPaid())
                return Response.ok(StatusMessage.getBundleMessage("NOT_PAID", "NOT_PAID")).build();
            File file = new File(chapter.getUrl());
            if (file.exists() == false)
                Response.serverError().build();
            is = new FileInputStream(file);
            StreamingOutput output = new StreamBuilder(is);
            ResponseBuilder rb = Response.ok(output);
            String name = course.getName().concat("_").concat(chapter.getName()).concat(".mp4");
            if (isInternetExplorer(request))
                rb.header("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(name, "utf-8") + "\"");
            else if(isChrome(request) || isFirefox(request))
                rb.header("Content-Disposition", "attachment; filename=\"" + MimeUtility.encodeWord(name, "UTF-8", "Q") + "\"");
            else
                rb.header("Content-Disposition", "attachment; filename=\"" + "video.mp4" + "\"");
            return rb.header("Content-Length", file.length()).header("Content-Type", "video/mp4").build();
        }
        catch (NoResultException | FileNotFoundException | UnsupportedEncodingException e)
        {
            return Response.status(Status.BAD_REQUEST).build();
        }
        finally
        {
            // IOUtils.closeQuietly(is);
        }

    }

    @Path("/auth/{chapterId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findChapterById(@PathParam("chapterId") Long chapterId)
    {
        return Response.ok().build();

    }

    private boolean isInternetExplorer(HttpServletRequest request)
    {
        String userAgent = request.getHeader("user-agent");
        return (userAgent.indexOf("MSIE") > -1);
    }

    private boolean isFirefox(HttpServletRequest request)
    {
        String userAgent = request.getHeader("user-agent");
        return userAgent.indexOf("Firefox") != -1;
    }

    private boolean isChrome(HttpServletRequest request)
    {
        String userAgent = request.getHeader("user-agent");
        return userAgent.indexOf("Chrome") != -1;
    }

    public static class StreamBuilder implements StreamingOutput
    {
        private final InputStream is;

        public StreamBuilder(InputStream is)
        {
            this.is = is;
        }

        @Override
        public void write(OutputStream output) throws IOException, WebApplicationException
        {
            // buffer size set to 10MB
            // int bufferSize = 1024 * 1024 * 10;
            // buffer size set to 3MB
            int bufferSize = 1024 * 30;
            int buffer = 1;
            int rs = is.read();
            while (rs != -1)
            {
                output.write(rs);
                rs = is.read();
                buffer++;
                // flush the output stream every 10MB
                if (buffer == bufferSize)
                {
                    buffer = 1;
                    output.flush();
                }
            }
            output.flush();
        }

    }
}
