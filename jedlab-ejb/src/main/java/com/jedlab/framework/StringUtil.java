package com.jedlab.framework;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class StringUtil
{

    public static boolean isNotEmpty(String input)
    {
        return (input != null && input.trim().length() > 0);
    }

    public static boolean isEmpty(String input)
    {
        if (input == null)
        {
            return true;
        }
        return input.trim().length() == 0;
    }

    public static String cut(String input, int length)
    {
        if (isEmpty(input))
        {
            return null;
        }
        if (input != null && input.length() > length)
        {
            return input.substring(0, length).concat(" ...");
        }
        return input;
    }

    public static String stringCutter(String input, int howmany)
    {
        return ((input != null && input.length() > howmany) ? input.substring(0, howmany) : input);
    }

    public static int countDuplicateWordsOccurence(String input1, String input2)
    {
        int occurence = 0;
        for (int i = 0; i < input1.length(); i++)
        {
            char at1 = input1.charAt(i);
            for (int j = 0; j < input2.length(); j++)
            {
                char at2 = input2.charAt(j);
                if (at1 == at2)
                {
                    if (i == j) occurence++;
                }
            }
        }
        return occurence;
    }

    public static boolean isNotNull(Object input)
    {
        String val = input == null ? null : String.valueOf(input);
        return isNotEmpty(val);
    }

    private static final Pattern SCRIPT_PATTERN = Pattern.compile("(?i)<(/?script[^>]*)>");

    public static String escapeJavascript(String input)
    {
        return SCRIPT_PATTERN.matcher(input).replaceAll("&lt;$1&gt;");
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static
    {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String formatViewCount(long value)
    {
        // Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatViewCount(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatViewCount(-value);
        if (value < 1000) return Long.toString(value); // deal with easy case

        Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); // the number part of the
                                                  // output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

}
