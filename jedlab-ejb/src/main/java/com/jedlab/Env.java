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
        return prop.getProperty("JAIL_HOME");
    }
    
    
    public static String getGistHome()
    {
        return prop.getProperty("GIST_HOME");
    }
    
}
