package com.jedlab.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;

import com.jedlab.Env;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.model.TestCase;

public class JavaCommandLine
{

    private static final String USER_HOME = System.getProperty("user.home");

    private String sourceDir;
    private String fileName;
    private List<String> programArgs;

    public JavaCommandLine(String sourceDir, String fileName, List<String> programArgs)
    {
        this.sourceDir = sourceDir;
        this.fileName = fileName;
        this.programArgs = programArgs;
    }

    public ProcessVO run() throws CompilerException
    {
        ByteArrayOutputStream out = null;
        OutputStream os = null;
        try
        {
             CommandLine cmdLine = chrootCmd();
            //CommandLine cmdLine = defaultCmd();
            DefaultExecutor executor = new DefaultExecutor();
            executor.setWorkingDirectory(new File(sourceDir));
            ExecuteWatchdog watchdog = new ExecuteWatchdog(2 * 1000);
            CompilerResultHandler resultHandler = new CompilerResultHandler(watchdog);
            out = new ByteArrayOutputStream();
            os = new FileOutputStream(new File(USER_HOME + File.separator + "compiler_error.txt"));
            PumpStreamHandler psh = new PumpStreamHandler(out, os);
            executor.setStreamHandler(psh);

            executor.execute(cmdLine, resultHandler);
            resultHandler.waitFor();
            // if (resultHandler.hasResult())
            // {
            //
            // }
            ExecuteException ee = resultHandler.getException();
            return new ProcessVO(new String(out.toByteArray(), "UTF-8").replaceAll("\n", "").replaceAll("\r", "").trim(), ee);
        }
        catch (Exception e)
        {
            throw new CompilerException(e);
        }
        finally
        {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(os);
        }

    }

    private CommandLine chrootCmd()
    {
        CommandLine cmdLine = new CommandLine("chroot");
//        cmdLine.addArgument("/root/jail");
        cmdLine.addArgument(Env.getJailHome());
        cmdLine.addArgument("/java/jdk8/bin/java");
//        cmdLine.addArgument("-cp");
//        cmdLine.addArgument(sourceDir);
        cmdLine.addArgument(fileName);
        if(CollectionUtil.isNotEmpty(programArgs))
        {
            for (String arg : programArgs)
            {
                cmdLine.addArgument(arg);
            }
        }
        return cmdLine;
    }

    private CommandLine defaultCmd()
    {
        CommandLine cmdLine = new CommandLine("java");
        cmdLine.addArgument("-cp");
        cmdLine.addArgument(sourceDir);
        cmdLine.addArgument(fileName);
        if(CollectionUtil.isNotEmpty(programArgs))
        {
            for (String arg : programArgs)
            {
                cmdLine.addArgument(arg);
            }
        }
        return cmdLine;
    }

    public static class ProcessVO
    {
        private String output;
        private ExecuteException cause;

        public ProcessVO(String output, ExecuteException cause)
        {
            this.output = output;
            this.cause = cause;
        }

        public String getOutput()
        {
            return output;
        }

        public ExecuteException getCause()
        {
            return cause;
        }

    }

    public static class CompilerResultHandler extends DefaultExecuteResultHandler
    {

        private static final Logger log = Logger.getLogger(CompilerResultHandler.class.getName());

        private ExecuteWatchdog watchdog;
        private ExecuteException cause;

        public CompilerResultHandler(ExecuteWatchdog watchdog)
        {
            this.watchdog = watchdog;
        }

        public void onProcessComplete(int exitValue)
        {
            super.onProcessComplete(exitValue);
        }

        public void onProcessFailed(ExecuteException e)
        {
            super.onProcessFailed(e);
            if (watchdog != null && watchdog.killedProcess())
            {
                log.fine("The compress process timed out");
            }
            else
            {
                log.fine("The compress process failed to do : " + e.getMessage());
            }
            this.cause = e;
        }

        public ExecuteException getCause()
        {
            return cause;
        }

    }

}
