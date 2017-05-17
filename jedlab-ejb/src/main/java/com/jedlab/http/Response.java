package com.jedlab.http;

import java.io.InputStream;

public class Response
{
    private final int statusCode;
    private final int length;
    private final long date;
    private final String msg;
    private final String contentType;
    private final InputStream inputStream;
    
    
    
    public Response(int statusCode, int length, long date, String msg, String contentType, InputStream inputStream)
    {
        this.statusCode = statusCode;
        this.length = length;
        this.date = date;
        this.msg = msg;
        this.contentType = contentType;
        this.inputStream = inputStream;
    }

    
    public InputStream content()
    {
        return inputStream;
    }
    
    public int statusCode() {
        return statusCode;
    }

    public int length() {
        return length;
    }


    public String contentType() {
        return contentType;
    }


    public long date() {
        return date;
    }


    public String msg() {
        return msg;
    }

}
