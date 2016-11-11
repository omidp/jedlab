package com.jedlab.dao.home;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.util.RandomStringUtils;

import com.jedlab.Env;
import com.jedlab.JedLab;
import com.jedlab.action.Constants;
import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.WebUtil;
import com.jedlab.gist.PygmentsCommandLine;
import com.jedlab.model.Gist;

@Name("gistHome")
@Scope(ScopeType.CONVERSATION)
public class GistHome extends EntityHome<Gist>
{

    @Logger
    Log log;

    @In(create = true)
    JedLab jedLab;

    public Long getGistId()
    {
        return (Long) getId();
    }

    public void setGistId(Long gistId)
    {
        setId(gistId);
    }

    public void load()
    {
        if (isIdDefined())
        {
            Gist gist = getInstance();
            if (gist.isPrivateGist())
            {
                Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
                if (gist.getMember().getId() != uid)
                    throw new AuthorizationException(StatusMessage.getBundleMessage("Permission_Deny", ""));
            }
        }
    }

    public void wire()
    {
        Long uid = (Long) Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        String gistHome = Env.getGistHome();
        if(StringUtil.isEmpty(getInstance().getFileName()))
            getInstance().setFileName(RandomStringUtils.randomAlphabetic(15));
        String gistPath = gistHome + String.valueOf(uid) + Env.FILE_SEPARATOR + getInstance().getFileName();
        File gistFile = new File(gistPath);
        if (gistFile.getParentFile().exists() == false)
            gistFile.getParentFile().mkdirs();
        String origContent = getInstance().getOrigContent();
        if (gistFile.exists() == false)
        {
            try (OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(gistFile), Charset.forName("UTF-8")))
            {
                os.write(origContent);
                os.flush();
                os.close();
            }
            catch (IOException e)
            {
                log.info(e);
            }
        }
        //
        PygmentsCommandLine cmd = new PygmentsCommandLine(gistFile);
        String gist = cmd.run();
        if (StringUtil.isEmpty(gist))
        {
            throw new ErrorPageExceptionHandler("empty content");
        }
        getInstance().setContent(gist);
        //
        String lines[] = origContent.split("\\r?\\n");
        String tempContent = "";
        if (lines.length <= 10)
        {
            tempContent = origContent;
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <= 10; i++)
            {
                sb.append(lines[i]).append("\r\n");
            }
            tempContent = sb.toString();
        }
        
        try (OutputStreamWriter os = new OutputStreamWriter(new FileOutputStream(gistFile), Charset.forName("UTF-8")))
        {
            os.write(tempContent);
            os.flush();
            os.close();
            cmd = new PygmentsCommandLine(gistFile);
            gist = cmd.run();
            if (StringUtil.isEmpty(gist))
            {
                throw new ErrorPageExceptionHandler("empty content");
            }
            getInstance().setShortContent(gist);
        }
        catch (IOException e)
        {
            log.info(e);
        }
        
        
        //
        getInstance().setMember(jedLab.getCurrentUser());
    }

    @Override
    public String persist()
    {
        wire();
        return super.persist();
    }

    @Override
    public String update()
    {
        wire();
        return super.update();
    }
    
    @Transactional
    public void deleteById()
    {
        String gistId = WebUtil.getParameterValue("gistId");
        if(StringUtil.isNotEmpty(gistId))
        {
            Gist instance = getEntityManager().find(Gist.class, Long.parseLong(gistId));
            getEntityManager().remove(instance);
            getEntityManager().flush();
        }
    }

}
