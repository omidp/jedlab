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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.CssCompressor;

/**
 *
 */
@Mojo(name = "cssCompressor")
public class StyleCompressor extends AbstractMojo
{

    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File basedir;

    @Parameter(defaultValue = "${project.basedir}/src/main/webapp", readonly = false)
    private File sourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}", readonly = false)
    private File outputDirectory;

    public void execute() throws MojoExecutionException
    {
        ArrayList<File> jsFiles = new ArrayList<File>(FileUtils.listFiles(sourceDirectory, new String[] { "css" }, true));
        for (File file : jsFiles)
        {
            InputStream is = null;
            FileWriter sw = null;
            try
            {
                is = new FileInputStream(file);
                CssCompressor cc = new CssCompressor(new InputStreamReader(is, "UTF-8"));
                sw = new FileWriter(new File(outputDirectory + "/" + file.getName()));
                cc.compress(sw, 5000);
                sw.flush();
                sw.close();
                is.close();
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
