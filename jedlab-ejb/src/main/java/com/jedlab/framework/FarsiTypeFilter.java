package com.jedlab.framework;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.web.AbstractFilter;

public class FarsiTypeFilter extends AbstractFilter
{

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException
    {
        if (request instanceof HttpServletRequest)
        {
            request.setCharacterEncoding("UTF-8");
            FarsiRequestWrapper frw = new FarsiRequestWrapper((HttpServletRequest) request);
            response.setCharacterEncoding("UTF-8");
            filterChain.doFilter(frw, response);
        }
        else
        {
            filterChain.doFilter(request, response);
        }
    }

}
