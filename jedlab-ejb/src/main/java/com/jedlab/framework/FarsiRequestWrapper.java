package com.jedlab.framework;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.omidbiz.persianutils.PersianCharacterUnifier;

public class FarsiRequestWrapper extends HttpServletRequestWrapper
{

    HttpServletRequest request;

    public FarsiRequestWrapper(HttpServletRequest request)
    {
        super(request);
        this.request = request;
    }

    @Override
    public String getCharacterEncoding()
    {
        if (request != null)
            return request.getCharacterEncoding();
        else
            return "UTF-8";
    }

    @Override
    public String getParameter(String name)
    {

        String parameter = super.getParameter(name);
        String pchar = PersianCharacterUnifier.getInstance().unify(parameter);
        return pchar;
    }

}
