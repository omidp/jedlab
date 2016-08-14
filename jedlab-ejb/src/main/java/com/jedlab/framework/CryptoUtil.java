package com.jedlab.framework;

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
    
}
