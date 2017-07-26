package com.jedlab.validators;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class NationalCodeValidator implements Validator
{

    @Override
    public void validate(FacesContext fc, UIComponent component, Object object) throws ValidatorException
    {
        UIInput input = (UIInput) component;
        String value = (String) input.getValue();
        FacesMessage fcMsg = new FacesMessage();
        fcMsg.setSummary("Invalid code");
        if (value != null)
        {
            if (!isLengthTen(value))
            {
                throw new ValidatorException(fcMsg);
            }
            if (isRepeatedDigits(value))
            {
                throw new ValidatorException(fcMsg);
            }

            List<Integer> codes = tokenizeCodeToArray(value);
            int n = calculateN(codes);
            int res = n % 11;
            int resultCalculated = 0;
            switch (res)
            {
                case 1:
                    resultCalculated = res;
                    break;

                default:
                    resultCalculated = 11 - res;
                    break;
            }

            if (resultCalculated != codes.get(9))
            {
                throw new ValidatorException(fcMsg);
            }

        }

    }

    private List<Integer> tokenizeCodeToArray(String value)
    {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < value.length(); i++)
        {
            result.add(Character.getNumericValue(value.charAt(i)));
        }
        return result;
    }

    private int calculateN(List<Integer> codes)
    {
        int n = 0;
        for (int i = 0; i < 10 - 1; i++)
        {
            n += codes.get(i) * (10 - i);
        }
        return n;
    }

    private boolean isRepeatedDigits(String value)
    {
        Integer intVal = Integer.parseInt(value);
        return (intVal % 1111111111 == 0);
    }

    private boolean isLengthTen(String value)
    {
        return (value.length() == 10);
    }
}
