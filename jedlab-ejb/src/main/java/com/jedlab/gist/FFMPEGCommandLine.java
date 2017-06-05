package com.jedlab.gist;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import com.jedlab.Env;

public class FFMPEGCommandLine
{

    public static final String[] options = { "-y", "-i", "${FILEPATH}", "-c:v", "-preset", "slow", "-crf", "22", "-pix_fmt", "yuv420p",
            "-c:a", "aac", "-b:a", "128k", "${OUTPUT}" };

    private final File file;

    public FFMPEGCommandLine(File file)
    {
        this.file = file;
    }

    public void run()
    {
        ByteArrayOutputStream out = null;
        OutputStream os = null;
        try
        {
            if (file.exists() == false)
            {
                throw new IllegalAccessException("File not found");
            }
            String fileMp4 = file.getAbsolutePath();
            fileMp4 = fileMp4 + ".mp4";
            //
            CommandLine cmdLine = new CommandLine("ffmpeg");
            Map map = new HashMap();
            map.put("FILEPATH", file.getAbsolutePath());
            map.put("OUTPUT", fileMp4);
            for (int i = 0; i < options.length; i++)
            {
                String cmdArg = options[i];
                cmdLine.addArgument(cmdArg, true);
            }
            cmdLine.setSubstitutionMap(map);
            DefaultExecutor executor = new DefaultExecutor();
            ExecuteWatchdog watchdog = new ExecuteWatchdog(2 * 1000);
            FFMPEGResultHandler resultHandler = new FFMPEGResultHandler(watchdog);
            out = new ByteArrayOutputStream();
            os = new FileOutputStream(new File(Env.USER_HOME + File.separator + "video_convert_error.txt"));
            PumpStreamHandler psh = new PumpStreamHandler(out, os);
            executor.setStreamHandler(psh);
            executor.execute(cmdLine, resultHandler);
            // resultHandler.waitFor();
            if (resultHandler.hasResult())
            {
            }
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
    }

    public static class FFMPEGResultHandler extends DefaultExecuteResultHandler
    {

        private static Logger log = org.slf4j.LoggerFactory.getLogger(FFMPEGResultHandler.class);

        private ExecuteWatchdog watchdog;

        public FFMPEGResultHandler(ExecuteWatchdog watchdog)
        {
            this.watchdog = watchdog;
        }

        public void onProcessComplete(int exitValue)
        {
            super.onProcessComplete(exitValue);
            log.debug("The compress process completed");
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
