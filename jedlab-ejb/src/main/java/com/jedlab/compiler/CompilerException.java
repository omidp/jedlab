package com.jedlab.compiler;

public class CompilerException extends RuntimeException
{

    public CompilerException(Exception e)
    {
        super(e);
    }

    public CompilerException(String message)
    {
        super(message);
    }

}
