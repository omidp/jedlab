package com.jedlab.model.enums;

import org.jboss.seam.international.StatusMessage;

public enum Privacy
{
    EVERYONE(StatusMessage.getBundleMessage("Everyone", "")), OnlyMe(StatusMessage.getBundleMessage("OnlyMe", ""));
    private String label;

    private Privacy(String label)
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
