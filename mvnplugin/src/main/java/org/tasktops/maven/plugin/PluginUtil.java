package org.tasktops.maven.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * @author Omid Pourhadi
 *
 */
public class PluginUtil
{

    public static boolean shouldInclude(File file, List<String> includes) throws IOException
    {
        if (includes != null && includes.size() > 0)
        {
            for (String inclusion : includes)
            {
                if (inclusion.equals(file.getName()))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean shouldExclude(File file, List<String> excludes)
    {
        if (excludes != null && excludes.size() > 0)
        {
            for (String exclusion : excludes)
            {
                if (exclusion.equals(file.getName()))
                {
                    return true;
                }
            }
        }
        return false;
    }

}
