package com.jedlab.framework;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import com.omidbiz.persianutils.JalaliCalendar;

public class DateUtil
{

    public static String getDuration(Date duration)
    {
        Calendar helper = getCalendarInstance();
        helper.setTime(duration);
        Calendar cal = getCalendarInstance();
        cal.set(Calendar.HOUR, helper.get(Calendar.HOUR));
        cal.set(Calendar.MINUTE, helper.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, helper.get(Calendar.SECOND));
        //
        Calendar endCal = getCalendarInstance();
        long millis = cal.getTimeInMillis() - endCal.getTimeInMillis();
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public static Date getDuration(String hourMin) throws ParseException
    {
        StringTokenizer tokenizer = new StringTokenizer(hourMin, ":");
        if (tokenizer.countTokens() < 2)
            throw new IllegalArgumentException("format must be mm:ss");
        String min = tokenizer.nextToken();
        String sec = tokenizer.nextToken();
        Calendar cal = getCalendarInstance();
        cal.set(Calendar.MINUTE, Integer.parseInt(min));
        cal.set(Calendar.SECOND, Integer.parseInt(sec));
        return cal.getTime();
    }

    private static Calendar getCalendarInstance()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1990);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Date addDate(Date date, int field, int amount)
    {
        Calendar cal = new JalaliCalendar();
        cal.setTime(date);
        cal.add(field, amount);
        return cal.getTime();
    }

}
