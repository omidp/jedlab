package com.jedlab;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

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
            int indexOf = agent.indexOf("MSIE");
            if(indexOf > 0)
            {
                //ie
                request.getRequestDispatcher("noie.html").forward(request, response);
                return;
            }
            if(agent.matches("/Trident.*rv\\:11\\./"))
            {
                //ie
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
