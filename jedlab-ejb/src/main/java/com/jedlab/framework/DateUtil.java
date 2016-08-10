package com.jedlab.framework;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.omidbiz.persianutils.JalaliCalendar;

public class DateUtil
{
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public static String getDuration(Date duration)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(duration);
        cal.set(Calendar.YEAR, 1990);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DATE, 1);
        return sdf.format(cal.getTime());
    }

    public static Date getDuration(String hourMin) throws ParseException
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(hourMin));
        cal.set(Calendar.YEAR, 1990);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DATE, 1);
        return cal.getTime();
    }

    public static Date addDate(Date date, int field, int amount)
    {
        Calendar cal = new JalaliCalendar();
        cal.setTime(date);
        cal.add(field, amount);
        return cal.getTime();
    }


}
