package com.jedlab.action;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.theme.ThemeSelector;

/**
 * @author Omid Pourhadi
 *
 * omidpourhadi [AT] gmail [DOT] com
 */
@Name("themeLocaleSelector")
@Scope(ScopeType.EVENT)
@BypassInterceptors
public class ThemeLocaleSelector implements Serializable
{

    @Observer("org.jboss.seam.localeSelected")
    public void select(String locale)
    {
        ThemeSelector themeSelector = ThemeSelector.instance();
        if(locale.contains("fa"))
        {
            themeSelector.setTheme("persianTheme");
        }
        else
        {
            themeSelector.setTheme("defaultTheme");
        }
    }

}
