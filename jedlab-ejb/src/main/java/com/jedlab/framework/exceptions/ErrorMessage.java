package com.jedlab.framework.exceptions;

import java.io.Serializable;

import com.jedlab.framework.AxonSerializer;

/**
 * @author : Omid Pourhadi omidpourhadi [AT] gmail [DOT] com
 * @version 0.2
 */
@AxonSerializer
public class ErrorMessage implements Serializable
{
    private int code;
    private String msg;
    private String property;
    private String channelName;

    public ErrorMessage()
    {
    }

    public ErrorMessage(String msg, String property)
    {
        this.msg = msg;
        this.property = property;
    }

    public ErrorMessage(int code, String msg)
    {
        this.code = code;
        this.msg = msg;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public String getProperty()
    {
        return property;
    }

    public void setProperty(String property)
    {
        this.property = property;
    }

    public String getChannelName()
    {
        return channelName;
    }

    public void setChannelName(String channelName)
    {
        this.channelName = channelName;
    }

}
