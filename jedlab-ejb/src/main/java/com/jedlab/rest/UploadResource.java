package com.jedlab.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.json.JSONObject;

import com.jedlab.Env;
import com.jedlab.model.Podcast;

@Path("/uploads")
@Name("uploadResource")
@Scope(ScopeType.EVENT)
public class UploadResource implements Serializable
{

    private static final String REPOSITORY = Env.getRepositoryLocation();

    @Context
    HttpServletRequest request;

    @In
    EntityManager entityManager;

    /**
     * @param urljson
     * @return
     * @throws IOException
     *             <p>
     *             { "podcastId" : 10, "url" : "" }
     *             </p>
     */
    @Path("/url")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadByUrl(String urljson) throws IOException
    {
        JSONObject json = new JSONObject(urljson);
        String url = json.getString("url");
        Long podcastId = json.getLong("podcastId");        
        //
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        String filePath = REPOSITORY + podcastId;
        File file = new File(filePath);
        if (file.exists() == false)
            file.mkdirs();
        FileOutputStream dest = null;
        InputStream src = null;
        try
        {
            dest = new FileOutputStream(new File(filePath + Env.FILE_SEPARATOR + fileName));
            src = new JdkHttpConnection().sendGetRequest(url);
            final ReadableByteChannel inputChannel = Channels.newChannel(src);
            final WritableByteChannel outputChannel = Channels.newChannel(dest);
            fastCopy(inputChannel, outputChannel);
        }
        finally
        {
            IOUtils.closeQuietly(src);
            IOUtils.closeQuietly(dest);
        }
        return Response.ok().build();
    }

    @POST
    @Path("/file/{podcastId}")
    @Consumes("multipart/form-data")
    public Response uploadFile(@PathParam("podcastId") Long podcastId, MultipartFormDataInput input) throws IOException
    {
        
        String fileName = "";

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("uploadedFile");

        for (InputPart inputPart : inputParts)
        {
            FileOutputStream dest = null;
            InputStream src = null;
            try
            {
                MultivaluedMap<String, String> header = inputPart.getHeaders();
                fileName = getFileName(header);
                String filePath = REPOSITORY + podcastId;
                File file = new File(filePath);
                if (file.exists() == false)
                    file.mkdirs();
                src = inputPart.getBody(InputStream.class, null);
                dest = new FileOutputStream(new File(filePath + Env.FILE_SEPARATOR + fileName));
                final ReadableByteChannel inputChannel = Channels.newChannel(src);
                final WritableByteChannel outputChannel = Channels.newChannel(dest);
                fastCopy(inputChannel, outputChannel);
            }
            finally
            {
                IOUtils.closeQuietly(src);
                IOUtils.closeQuietly(dest);
            }

        }

        return Response.ok(fileName + " upload successfully").build();
    }

    private String getFileName(MultivaluedMap<String, String> header)
    {
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition)
        {
            if ((filename.trim().startsWith("filename")))
            {
                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }

    public static void fastCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException
    {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);

        while (src.read(buffer) != -1)
        {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }

        buffer.flip();

        while (buffer.hasRemaining())
        {
            dest.write(buffer);
        }
    }

}
