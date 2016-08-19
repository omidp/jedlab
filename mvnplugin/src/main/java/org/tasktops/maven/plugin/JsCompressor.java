package org.tasktops.maven.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * @author omidp
 * 
 */
@Mojo(name = "jsCompressor")
public class JsCompressor extends AbstractMojo
{

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File basedir;

    @Parameter(defaultValue = "${project.basedir}/src/main/webapp", readonly = false)
    private File sourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}", readonly = false)
    private File outputDirectory;
    
    @Parameter(readonly = false)
    private List<String> excludes;

    public void execute() throws MojoExecutionException
    {
        ArrayList<File> jsFiles = new ArrayList<File>(FileUtils.listFiles(sourceDirectory, new String[] { "js" }, true));
        for (File file : jsFiles)
        {
            if(excludes != null && excludes.contains(file.getName()))
                continue;
            InputStream is = null;
            FileWriter sw = null;
            try
            {
                is = new FileInputStream(file);
                JavaScriptCompressor js = new JavaScriptCompressor(new InputStreamReader(is, "UTF-8"), new ErrorReporter4Mojo(getLog(),
                        true));
                sw = new FileWriter(new File(outputDirectory + "/" + file.getName()));
                js.compress(sw, 5000, false, false, true, false);
                sw.flush();
            }
            catch (EvaluatorException | IOException e)
            {
                throw new MojoExecutionException(e.getMessage());
            }
            finally
            {
                IOUtils.closeQuietly(is);
                IOUtils.closeQuietly(sw);
            }
        }
    }
}
