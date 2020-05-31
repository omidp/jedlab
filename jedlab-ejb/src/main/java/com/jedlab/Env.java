package com.jedlab;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.jedlab.action.Constants;
import com.jedlab.framework.StringUtil;

/**
 * Immutable Environment Configuration
 * 
 * @author omid pourhadi : omidpourhadi AT gmail DOT com
 * @version 1.0
 */
public final class Env
{

    public static final String NL = System.getProperty("line.separator");
    public static final String FILE_SEPARATOR = File.separator;
    public static final String JBOSS7_DATA_HOME = System.getProperty("jboss.server.data.dir");
    public static final String OS = System.getProperty("os.name");
    public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    public static final String USER_HOME = System.getProperty("user.home");

    private static Properties prop = new Properties();

    private Env()
    {

    }

    static
    {
        reload();
    }

    /**
     * <b>Must</b> be caled from Seam Synchronized component
     */
    public static synchronized void reload()
    {
        InputStream stream = null;
        try
        {
            stream = Env.class.getClassLoader().getResourceAsStream(Constants.CONFIG_FILE);
            if(stream == null)
                stream = Env.class.getResourceAsStream(Constants.CONFIG_FILE);
            if(stream == null)
                stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(Constants.CONFIG_FILE);
            prop.load(stream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            IOUtils.closeQuietly(stream);
        }
    }

    public static String getUserHome()
    {
        return System.getenv("user.home");
    }
    
    public static String getJailHome()
    {
        if(isDevMode())
            return prop.getProperty("DEV_JAIL_HOME");
        else
            return prop.getProperty("PROD_JAIL_HOME");
    }
    
    public static boolean isDevMode()
    {
        return Boolean.valueOf(prop.getProperty("devmode"));
    }
    
    
    public static String getGistHome()
    {
        return prop.getProperty("GIST_HOME");
    }
    
    public static String getVideoLocation()
    {
        return prop.getProperty("VID_LOC");
    }
    
    public static String getMerchantId()
    {
        return prop.getProperty("MERCHANTID");
    }
    
    public static String getMerchantPass()
    {
        return prop.getProperty("MPASS");
    }
    
    public static String getGithubKey()
    {
        return prop.getProperty("GITHUB_KEY");
    }
    
    public static String getGithubSecret()
    {
        return prop.getProperty("GITHUB_SECRET");
    }
    
    public static String getGithubCallback()
    {
        return prop.getProperty("GITHUB_CALLBACK");
    }
    
//    public static String getMdServer()
//    {
//        return prop.getProperty("MD_SERVER");
//    }
//    
    public static String getGoogleKey()
    {
        return prop.getProperty("GOOGLE_KEY");
    }
    
    public static String getGoogleSecret()
    {
        return prop.getProperty("GOOGLE_SECRET");
    }
    
    public static String getGoogleCallback()
    {
        return prop.getProperty("GOOGLE_CALLBACK");
    }
    
    public static String getStoryLocation()
    {
        return prop.getProperty("STORY_LOC");
    }
    
    public static String getCompanyImageLocation()
    {
        return prop.getProperty("COMPANY_IMG_LOC");
    }
    
    public static String getRepositoryLocation()
    {
        return prop.getProperty("REPOSITORY_LOC");
    }
    
}
