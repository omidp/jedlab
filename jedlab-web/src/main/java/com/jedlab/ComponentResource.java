package com.jedlab;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * @author Omid Pourhadi
 *
 *         omidpourhadi [AT] gmail [DOT] com
 */
public abstract class ComponentResource extends HttpServlet
{

    private long startupTime;

    @Override
    public void init()
    {
        this.startupTime = System.currentTimeMillis();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        URL resourceUrl = getURL();
        long latestTimestamp = -1;
        long timestamp = getFileTimestamp(resourceUrl);
        if (timestamp > latestTimestamp)
        {
            latestTimestamp = timestamp;
        }
        long lastModified = (latestTimestamp > this.startupTime ? latestTimestamp : this.startupTime);

        if (request.getDateHeader("If-Modified-Since") != -1)
        {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }
        if (lastModified != -1)
        {
            lastModified -= lastModified % 1000;
            long requestModifiedSince = request.getDateHeader("If-Modified-Since");
            if (lastModified <= requestModifiedSince)
            {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        }
        response.setDateHeader("Last-Modified", lastModified != -1 ? lastModified : this.startupTime);
        response.setHeader("Cache-Control", "must-revalidate");
        InputStream in = null;
        OutputStream outputStream = null;
        try
        {
            in = resourceUrl.openStream();
            response.setContentType(getContentType());
            outputStream = selectOutputStream(request, response);
            if (in != null)
            {
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);
                while (read != -1)
                {
                    outputStream.write(buffer, 0, read);
                    read = in.read(buffer);
                }
                outputStream.flush();

            }
            else
            {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        finally
        {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(outputStream);
        }
    }

    protected long getFileTimestamp(URL url)
    {

        try
        {
            URLConnection resource = url.openConnection();
            long lastModifiedTime = resource.getLastModified();
            // if (logger.isDebugEnabled()) {
            // logger.debug("Last-modified timestamp of " + resource + " is " +
            // lastModifiedTime);
            // }
            return lastModifiedTime;
        }
        catch (IOException ex)
        {
            // logger.warn("Couldn't retrieve last-modified timestamp of [" +
            // resource +
            // "] - using ResourceServlet startup time");
            return -1;
        }
    }

    public abstract URL getURL();

    public abstract String getContentType();

    protected OutputStream selectOutputStream(HttpServletRequest request, HttpServletResponse response) throws IOException
    {

        String acceptEncoding = request.getHeader("Accept-Encoding");
        String mimeType = response.getContentType();

        if (isGzipEnabled() && acceptEncoding != null && acceptEncoding.length() > 0 && acceptEncoding.indexOf("gzip") > -1
                && isCompressedMimeType(mimeType))
        {
            return new GZIPResponseStream(response);
        }
        else
        {
            return response.getOutputStream();
        }
    }

    protected boolean isCompressedMimeType(String mimeType)
    {
        return mimeType.matches("text/.+");
    }

    protected boolean isGzipEnabled()
    {
        return true;
    }

    /*
     * Copyright 2004-2008 the original author or authors.
     * 
     * Licensed under the Apache License, Version 2.0 (the "License"); you may
     * not use this file except in compliance with the License. You may obtain a
     * copy of the License at
     * 
     * http://www.apache.org/licenses/LICENSE-2.0
     * 
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
     * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
     * License for the specific language governing permissions and limitations
     * under the License.
     * 
     * @See org/springframework/js/resource/ResourceServlet.java
     */
    private class GZIPResponseStream extends ServletOutputStream
    {

        private ByteArrayOutputStream byteStream = null;

        private GZIPOutputStream gzipStream = null;

        private boolean closed = false;

        private HttpServletResponse response = null;

        private ServletOutputStream servletStream = null;

        public GZIPResponseStream(HttpServletResponse response) throws IOException
        {
            super();
            closed = false;
            this.response = response;
            this.servletStream = response.getOutputStream();
            byteStream = new ByteArrayOutputStream();
            gzipStream = new GZIPOutputStream(byteStream);
        }

        @Override
        public void close() throws IOException
        {
            if (closed)
            {
                throw new IOException("This output stream has already been closed");
            }
            gzipStream.finish();

            byte[] bytes = byteStream.toByteArray();

            response.setContentLength(bytes.length);
            response.addHeader("Content-Encoding", "gzip");
            servletStream.write(bytes);
            servletStream.flush();
            servletStream.close();
            closed = true;
        }

        @Override
        public void flush() throws IOException
        {
            if (closed)
            {
                throw new IOException("Cannot flush a closed output stream");
            }
            gzipStream.flush();
        }

        @Override
        public void write(int b) throws IOException
        {
            if (closed)
            {
                throw new IOException("Cannot write to a closed output stream");
            }
            gzipStream.write((byte) b);
        }

        @Override
        public void write(byte b[]) throws IOException
        {
            write(b, 0, b.length);
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException
        {
            if (closed)
            {
                throw new IOException("Cannot write to a closed output stream");
            }
            gzipStream.write(b, off, len);
        }

        @SuppressWarnings("unused")
        public boolean closed()
        {
            return (this.closed);
        }

        @SuppressWarnings("unused")
        public void reset()
        {
            // noop
        }
    }

}
