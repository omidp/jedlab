package com.jedlab.compiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.problems.CompilationProblem;
import org.apache.commons.jci.readers.FileResourceReader;
import org.apache.commons.jci.stores.FileResourceStore;
import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityController;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;

import com.jedlab.Env;
import com.jedlab.compiler.JavaCommandLine.ProcessVO;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.StringUtil;
import com.jedlab.model.Question;
import com.jedlab.model.TestCase;

@Name("ideAction")
@Scope(ScopeType.CONVERSATION)
public class IdeAction extends EntityController
{

    @Logger
    Log log;

    private String code;

    private Question question;

    private List<Problem> problems = new ArrayList<>();

    @Create
    public void init()
    {
        code = "//*******************************************************************\r\n" + "// NOTE: please Change the ClassName\r\n"
                + "//*******************************************************************\r\n\r\n"
                + "import java.lang.Math; // headers MUST be above the first class\r\n\r\n"
                + "// one class needs to have a main() method\r\n" + "public class ClassName\r\n" + "{\r\n\r\n"
                + "  public static void main(String[] args)\r\n" + "  {\r\n" + "      \r\n" + "  }\r\n" + "\r\n" + "}";
        problems = new ArrayList<>();
    }

    public void load()
    {
        try
        {
            question = (Question) getEntityManager()
                    .createQuery("select q from Question q LEFT OUTER JOIN q.testcases tc where q.id = :qid").setParameter("qid", 1L)
                    .getSingleResult();
        }
        catch (NoResultException e)
        {
            throw new EntityNotFoundException();
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
            JavaFile javaFile = new JavaFile(getCode()).make();
            compilation(javaFile);
        }
        catch (CompilerException e)
        {
            problems.add(new Problem(e.getMessage()));
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
            if (CollectionUtil.isNotEmpty(runtimeCompiler.getCompileErrors()))
            {
                for (CompilationProblem p : runtimeCompiler.getCompileErrors())
                {
                    problems.add(new Problem(p.getMessage()));
                }
            }
        }
        catch (CompilerException ce)
        {
            throw new CompilerException(ce);
        }

    }

    public String execute()
    {
        
        try
        {
            JavaFile javaFile = new JavaFile(getCode()).make();
            compilation(javaFile);
            //
            List<TestCase> testcases = question.getTestcases();
            int i=0;
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
            if(CollectionUtil.isNotEmpty(problems))
            {
                return null;
            }
        }
        catch (CompilerException e)
        {
            problems.add(new Problem(e.getMessage()));
            return null;
        }
        if(CollectionUtil.isNotEmpty(problems))
        {
            return null;
        }

        return "executed";
    }

    public static class JavaRuntimeCompiler
    {
        private String fileName;
        private String sourceDir;
        List<CompilationProblem> compileErrors;

        public JavaRuntimeCompiler(String fileName, String sourceDir)
        {
            this.fileName = fileName;
            this.sourceDir = sourceDir;
        }

        public JavaRuntimeCompiler compile()
        {
            JavaCompiler compiler = new JavaCompilerFactory().createCompiler("eclipse");
            CompilationResult result = compiler.compile(new String[] { String.format("%s.java", fileName) }, new FileResourceReader(
                    new File(sourceDir)), new FileResourceStore(new File(sourceDir)));
            if (result.getErrors().length > 0)
            {
                compileErrors = new ArrayList<>();
                compileErrors = Arrays.asList(result.getErrors());
            }
            return this;
        }

        public List<CompilationProblem> getCompileErrors()
        {
            return compileErrors;
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
            try
            {
                String fname = RandomStringUtils.randomAlphabetic(10);
                // TODO: add user id as folder name
                String sourceDir = Env.getJailHome();
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
                if (f.exists() == false)
                    f.createNewFile();
                Files.write(f.toPath(), code.getBytes("UTF-8"));
                this.fileName = fname;
                this.directory = sourceDir;
            }
            catch (Exception e)
            {
                throw new CompilerException(e);
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
