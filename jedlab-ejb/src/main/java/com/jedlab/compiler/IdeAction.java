package com.jedlab.compiler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.framework.EntityController;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;

import com.jedlab.Env;
import com.jedlab.action.Constants;
import com.jedlab.compiler.JavaCommandLine.CompilerResultHandler;
import com.jedlab.compiler.JavaCommandLine.ProcessVO;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TxManager;
import com.jedlab.model.Member;
import com.jedlab.model.MemberQuestion;
import com.jedlab.model.MemberQuestion.QuestionStatus;
import com.jedlab.model.Question;
import com.jedlab.model.TestCase;

@Name("ideAction")
@Scope(ScopeType.CONVERSATION)
public class IdeAction extends EntityController
{

    @Logger
    Log log;

    private static final String sourceDir = Env.getJailHome() + File.separator;

    private String code;

    private Question question;

    private boolean solved;

    private List<Problem> problems = new ArrayList<>();

    private Long questionId;

    public Long getQuestionId()
    {
        return questionId;
    }

    public void setQuestionId(Long questionId)
    {
        this.questionId = questionId;
    }

    public boolean isSolved()
    {
        return solved;
    }

    public IdeAction()
    {

    }

    @Create
    public void init()
    {
        code = "//*******************************************************************\r\n"
                + "// NOTE: please Change the ClassName\r\n"
                + "//*******************************************************************\r\n\r\n"
                + "import java.lang.Math; // headers MUST be above the first class\r\n\r\n"
                + "// a class needs to have a main() method\r\n"
                // + String.format("public class ClassName%s\r\n",
                // RandomStringUtils.randomAlphabetic(3)) + "{\r\n\r\n"
                + "public class ClassName\r\n" + "{\r\n\r\n" + "  public static void main(String[] args)\r\n" + "  {\r\n" + "      \r\n"
                + "  }\r\n" + "\r\n" + "}";
    }

    public void load()
    {
        Long uid = (Long) getSessionContext().get(Constants.CURRENT_USER_ID);
        try
        {
            question = (Question) getEntityManager()
                    .createQuery("select q from Question q LEFT OUTER JOIN q.testcases tc where q.id = :qid")
                    .setParameter("qid", getQuestionId()).getSingleResult();
        }
        catch (NoResultException e)
        {
            throw new EntityNotFoundException();
        }
        try
        {
            Long cnt = (Long) getEntityManager()
                    .createQuery("select count(mq) from MemberQuestion mq where mq.member.id = :memId AND mq.question.id =:qid")
                    .setParameter("memId", uid).setParameter("qid", getQuestionId()).getSingleResult();
            solved = cnt > 0;
        }
        catch (NoResultException e)
        {
        }
    }

    public List<Problem> getProblems()
    {
        return problems;
    }

    public Question getQuestion()
    {
        return question;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    @SuppressWarnings("unchecked")
    public String compile() throws IOException
    {
        try
        {
            problems = new ArrayList<>();
            JavaFile javaFile = new JavaFile(getCode()).make();
            compilation(javaFile);
        }
        catch (CompilerException e)
        {
            problems.add(new Problem(e.getMessage()));
        }
        if (CollectionUtil.isEmpty(problems))
        {
            getStatusMessages().addFromResourceBundle("Compile_Successful");
        }
        return null;
    }

    private void compilation(JavaFile javaFile)
    {
        try
        {

            // FileUtils.deleteDirectory();
            //
            JavaRuntimeCompiler runtimeCompiler = new JavaRuntimeCompiler(javaFile.getFileName(), javaFile.getDirectory()).compile();
            ProcessVO processVO = runtimeCompiler.getProcessVO();
            if (processVO != null)
            {
                if (StringUtil.isNotEmpty(processVO.getOutput()))
                    problems.add(new Problem(processVO.getOutput()));
                if (processVO.getCause() != null && processVO.getCause().getExitValue() == 1)
                    problems.add(new Problem(StatusMessage.getBundleMessage("Compile_Error", "Compile_Error")));
            }
        }
        catch (CompilerException ce)
        {
            throw new CompilerException(ce);
        }

    }

    @Transactional
    public String execute()
    {

        try
        {
            problems = new ArrayList<>();
            JavaFile javaFile = new JavaFile(getCode()).make();
            compilation(javaFile);
            //
            List<TestCase> testcases = question.getTestcases();
            int i = 0;
            for (TestCase testCase : testcases)
            {
                i++;
                JavaCommandLine jcl = new JavaCommandLine(javaFile.getDirectory(), javaFile.getFileName(), Arrays.asList(testCase
                        .getInputParams()));
                ProcessVO processVO = jcl.run();
                //
                String res = testCase.getResult();
                if (res.equals(processVO.getOutput()) == false)
                {
                    String failMsg = StatusMessage.getBundleMessage("Testcase_Fail", "");
                    problems.add(new Problem(interpolate(failMsg, i)));
                }
                ExecuteException cause = processVO.getCause();
                if (cause != null)
                {
                    log.info(cause);
                    String failMsg = StatusMessage.getBundleMessage("Testcase_Fail", "");
                    problems.add(new Problem(interpolate(failMsg, i)));
                    problems.add(new Problem(cause.getMessage()));
                }
            }
            if (CollectionUtil.isNotEmpty(problems))
            {
                return null;
            }
        }
        catch (CompilerException e)
        {
            problems.add(new Problem(e.getMessage()));
            return null;
        }
        if (CollectionUtil.isNotEmpty(problems))
        {
            return null;
        }
        //
        if(solved == false)
        {
            TxManager.beginTransaction();
            TxManager.joinTransaction(getEntityManager());
            MemberQuestion mq = new MemberQuestion();
            Member m = new Member();
            m.setId((Long) getSessionContext().get(Constants.CURRENT_USER_ID));
            mq.setMember(m);
            mq.setQuestion(question);
            mq.setStatus(QuestionStatus.RESOLVED);
            getEntityManager().persist(mq);
            getEntityManager().flush();
            getStatusMessages().addFromResourceBundle("Problem_Solved");
        }
        return "executed";
    }

    public static class JavaRuntimeCompiler
    {
        private String fileName;
        private String sourceDir;
        private ProcessVO processVO;

        public JavaRuntimeCompiler(String fileName, String sourceDir)
        {
            this.fileName = fileName;
            this.sourceDir = sourceDir;
        }

        public JavaRuntimeCompiler compile()
        {
            ByteArrayOutputStream out = null;
            OutputStream os = null;
            try
            {
                CommandLine cmdLine = null;
                if (Env.isDevMode())
                {
                    cmdLine = new CommandLine("javac");
                    cmdLine.addArgument("-cp");
                    cmdLine.addArgument(sourceDir);
                    cmdLine.addArgument(String.format("%s.java", fileName));
                }
                else
                {
                    cmdLine = new CommandLine("chroot");
                    cmdLine.addArgument(Env.getJailHome());
                    cmdLine.addArgument("/java/jdk8/bin/javac");
                    cmdLine.addArgument(String.format("%s.java", fileName));
                }
                //
                DefaultExecutor executor = new DefaultExecutor();
                executor.setWorkingDirectory(new File(sourceDir));
                ExecuteWatchdog watchdog = new ExecuteWatchdog(2 * 1000);
                CompilerResultHandler resultHandler = new CompilerResultHandler(watchdog);
                out = new ByteArrayOutputStream();
                os = new FileOutputStream(new File(Env.USER_HOME + File.separator + "compiler_error.txt"));
                PumpStreamHandler psh = new PumpStreamHandler(out, os);
                executor.setStreamHandler(psh);

                executor.execute(cmdLine, resultHandler);
                resultHandler.waitFor();
                ExecuteException ee = resultHandler.getException();
                processVO = new ProcessVO(new String(out.toByteArray(), "UTF-8").replaceAll("\n", "").replaceAll("\r", "").trim(), ee);
            }
            catch (IOException e)
            {
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            finally
            {
                IOUtils.closeQuietly(out);
                IOUtils.closeQuietly(os);
            }
            return this;
        }

        public ProcessVO getProcessVO()
        {
            return processVO;
        }

    }

    public static class JavaFile
    {
        /**
         * fileName without .java
         */
        private String fileName;
        private String directory;
        private String code;

        public JavaFile(String code)
        {
            this.code = code;
        }

        public String getFileName()
        {
            return fileName;
        }

        public String getDirectory()
        {
            return directory;
        }

        public JavaFile make()
        {
            OutputStream os = null;
            OutputStreamWriter osw = null;
            try
            {
                String fname = RandomStringUtils.randomAlphabetic(10);
                // TODO: add user id as folder name

                File dir = new File(sourceDir);
                if (dir.exists() == false)
                    dir.mkdirs();
                Pattern pattern = Pattern
                        .compile("\\s*(public|private)\\s+class\\s+(\\w+)\\s+((extends\\s+\\w+)|(implements\\s+\\w+( ,\\w+)*))?\\s*\\{");
                Matcher m = pattern.matcher(code);
                if (m.find())
                {
                    // public/private
                    String modifier = m.group(1);
                    if ("public".equals(modifier) == false)
                        throw new CompilerException(StatusMessage.getBundleMessage("Public_Class_Name_Error", ""));
                    fname = m.group(2);
                    if (Character.isLowerCase(fname.charAt(0)))
                        throw new CompilerException(StatusMessage.getBundleMessage("Class_Name_Error", ""));
                }
                else
                {
                    throw new CompilerException(StatusMessage.getBundleMessage("Class_Name_Not_Found", ""));
                }
                String fileName = sourceDir + String.format("%s.java", fname);
                File f = new File(fileName);
                os = new FileOutputStream(f);
                osw = new OutputStreamWriter(os, "UTF-8");
                osw.write(code);
                osw.flush();
                os.flush();
                this.fileName = fname;
                this.directory = sourceDir;
            }
            catch (Exception e)
            {
                throw new CompilerException(e);
            }
            finally
            {
                IOUtils.closeQuietly(os);
                IOUtils.closeQuietly(osw);
            }
            return this;
        }

    }

    public static class Problem
    {
        private String message;

        public Problem(String message)
        {
            this.message = message;
        }

        public String getMessage()
        {
            return message;
        }

    }

}
