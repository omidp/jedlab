package com.jedlab.framework;

import java.io.UnsupportedEncodingException;

import org.jboss.seam.util.Base64;

/**
 * @author Omid Pourhadi
 *
 */
public class CryptoUtil
{

    
    public static String decodeBase64(String input)
    {
        return new String(Base64.decode(input));
    }
    
    public static String encodeBase64(String input)
    {
        try
        {
            return new String(Base64.encodeBytes(input.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException e)
        {
        }
        return "";
    }
    
}
