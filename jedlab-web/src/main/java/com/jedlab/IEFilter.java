package com.jedlab;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.jedlab.framework.StringUtil;

public class IEFilter implements Filter
{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        if (request instanceof HttpServletRequest)
        {
            String agent = ((HttpServletRequest) request).getHeader("User-Agent");
            if (StringUtil.isEmpty(agent))
            {
                request.getRequestDispatcher("noie.html").forward(request, response);
                return;
            }
            int indexOf = agent.indexOf("MSIE");
            if (indexOf > 0)
            {
                // ie
                request.getRequestDispatcher("noie.html").forward(request, response);
                return;
            }
            if (agent.indexOf("Trident") > 0)
            {
                // ie
                request.getRequestDispatcher("noie.html").forward(request, response);
                return;
            }
        }

        chain.doFilter(request, response);

    }

    @Override
    public void destroy()
    {

    }

}
