package com.jedlab.gist;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import com.jedlab.Env;

public class PygmentsCommandLine
{

    private File file;

    public PygmentsCommandLine(File file)
    {
        this.file = file;
    }

    public String run()
    {
        ByteArrayOutputStream out = null;
        OutputStream os = null;
        try
        {
            CommandLine cmd = new CommandLine("pygmentize");
            cmd.addArgument("-f");
            cmd.addArgument("html");
            cmd.addArgument("-O");
            // cmd.addArgument("full,style=emacs,linenos=1");
            cmd.addArgument("style=emacs,linenos=1");
            cmd.addArgument("-l");
            cmd.addArgument("java");
            cmd.addArgument(file.getAbsolutePath());
            //
            DefaultExecutor executor = new DefaultExecutor();
            ExecuteWatchdog watchdog = new ExecuteWatchdog(2 * 1000);
            PygmentResultHandler handler = new PygmentResultHandler(watchdog, file);
            out = new ByteArrayOutputStream();
            os = new FileOutputStream(new File(Env.USER_HOME + File.separator + "pygment_error.txt"));
            PumpStreamHandler psh = new PumpStreamHandler(out, os);
            executor.setStreamHandler(psh);

            executor.execute(cmd, handler);
            handler.waitFor();
            if (handler.hasResult())
            {                
                return new String(out.toByteArray(), "UTF-8");
            }
            else
                return null;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(os);
        }
        return null;
    }
    
    
    public static class PygmentResultHandler extends DefaultExecuteResultHandler
    {

        private static Logger log = org.slf4j.LoggerFactory.getLogger(PygmentResultHandler.class); 
        
        private ExecuteWatchdog watchdog;
        private File file;

        public PygmentResultHandler(ExecuteWatchdog watchdog, File file)
        {
            this.watchdog = watchdog;
            this.file = file;
        }

        public void onProcessComplete(int exitValue)
        {
            super.onProcessComplete(exitValue);
            log.debug("The compress process completed");
        }

        public File getFile()
        {
            return file;
        }

        public void onProcessFailed(ExecuteException e)
        {
            super.onProcessFailed(e);
            if (watchdog != null && watchdog.killedProcess())
            {
                log.debug("The compress process timed out");
            }
            else
            {
                log.debug("The compress process failed to do : " + e.getMessage());
            }
        }
    }

}
