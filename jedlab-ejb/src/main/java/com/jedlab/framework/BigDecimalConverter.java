package com.jedlab.framework;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("bigDecimalConverter")
@BypassInterceptors
@Converter
public class BigDecimalConverter implements javax.faces.convert.Converter
{

    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String value)
    {
        return (value == null) ? null : new BigDecimal(value);
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object value)
    {

        return (value == null) ? "" : String.valueOf(new BigDecimal(String.valueOf(value)).intValue());
    }

}
