package com.jedlab.framework;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil
{
    
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);
    
    public static final Pattern UNICODECHARACTER = Pattern.compile("\\w+", Pattern.UNICODE_CHARACTER_CLASS);
    
    public static final Pattern ENGLISHCHARACTER = Pattern.compile("^[a-zA-Z0-9]*$", Pattern.CASE_INSENSITIVE);
    
    public static final Pattern ONLYDIGITS = Pattern.compile("\\d+", Pattern.CASE_INSENSITIVE);

    public static boolean find(String input, String find)
    {
        Pattern p = Pattern.compile(find, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(input);
        return m.find();
    }

    public static int findOccurrence(String input, String find)
    {
        Pattern p = Pattern.compile(find, Pattern.CASE_INSENSITIVE);
        int cnt = 0;
        Matcher matcher = p.matcher(input);
        while (matcher.find())
        {
            cnt++;
        }
        return cnt;
    }
    

}
