package com.jedlab;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import com.jedlab.model.Course.Language;
import com.jedlab.model.Course.Level;

@Name("jedLab")
@Scope(ScopeType.CONVERSATION)
public class JedLab implements Serializable
{

    @Factory("courseLevels")
    public Level[] courseLevels()
    {
        return Level.values();
    }
    
    @Factory("courseLangs")
    public Language[] courseLangs()
    {
        return Language.values();
    }
    
}
