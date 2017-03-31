package com.jedlab.model.enums;

import org.jboss.seam.international.StatusMessage;

public enum Gender
{
    MALE(StatusMessage.getBundleMessage("Male", "")), FEMALE(StatusMessage.getBundleMessage("Female", ""));

    private String label;

    private Gender(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

}
