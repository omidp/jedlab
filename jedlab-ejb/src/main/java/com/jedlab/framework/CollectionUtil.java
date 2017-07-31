package com.jedlab.framework;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CollectionUtil
{

    public static boolean isNotEmpty(Collection<?> input)
    {
        return input != null && input.size() > 0;
    }

    public static boolean isEmpty(Collection<?> input)
    {
        return isNotEmpty(input) == false;
    }
    public static String commaSeparated(Long[] numbers)
    {
        if (numbers == null)
            return null;
        List<Long> arrList = Arrays.asList(numbers);
        return commaSeparated(arrList);
    }

    public static String commaSeparated(List<Long> numbers)
    {
        if (numbers == null)
            return null;
        String res = "";
        if (numbers != null && numbers.size() > 0)
        {
            for (int i = 0; i < numbers.size(); i++)
            {
                if (i > 0)
                    res += ",";
                res += numbers.get(i);
            }
        }
        return res;
    }
}
