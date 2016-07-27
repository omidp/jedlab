package com.jedlab.framework.exceptions;

public class ServiceException extends RuntimeException
{

    public static final int SOMETHNG_BAD_HAPPENED = 0;

    private int code;

    public ServiceException(int code)
    {
        this.code = code;
    }

    public ServiceException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public ServiceException(int code, String message, Throwable cause)
    {
        super(message, cause);
        this.code = code;
    }

    public ServiceException(String message, Throwable cause)
    {
        super(message, cause);
        this.code = SOMETHNG_BAD_HAPPENED;
    }

    public ServiceException(int code, String message)
    {
        super(message);
        this.code = code;
    }

    public ServiceException(String message)
    {
        super(message);
        this.code = SOMETHNG_BAD_HAPPENED;
    }

    public ServiceException(int code, Throwable cause)
    {
        super(cause);
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

}