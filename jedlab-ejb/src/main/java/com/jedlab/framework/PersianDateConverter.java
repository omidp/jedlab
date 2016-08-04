package com.jedlab.framework;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("persianDateConverter")
@BypassInterceptors
@Converter
public class PersianDateConverter implements javax.faces.convert.Converter
{

    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String value)
    {
        return (value == null) ? null : com.omidbiz.persianutils.PersianDateConverter.getInstance().SolarToGregorian(value);
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object value)
    {
        String format = new SimpleDateFormat("yyyy/MM/dd").format((Date)value);
        return (value == null) ? "" : com.omidbiz.persianutils.PersianDateConverter.getInstance().GregorianToSolar(format);
    }

}
