package com.jedlab.action;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.web.AbstractFilter;

@Scope(APPLICATION)
@Name("cacheExpirePageAction")
@BypassInterceptors
@Install(precedence = Install.BUILT_IN, value = true)
@Filter(around="org.jboss.seam.web.rewriteFilter")
public class CacheExpirePageAction extends AbstractFilter
{

    private static final Pattern pattern = Pattern.compile("images|resource|css|js|image|img|resources|javascript");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse)
        {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            String requestURI = req.getRequestURI();
            boolean find = pattern.matcher(requestURI).find();
            if (!find)
            {
                resp.setHeader("Expires", "0");
                resp.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
                resp.setHeader("Pragma", "no-cache");
            }
//            else
//            {
//                resp.setHeader("Expires", 1 month later);
//            }
        }
        chain.doFilter(request, response);
    }

}
