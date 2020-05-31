package com.jedlab.model;

public enum CompanySize
{

    S("1-10"), M("11-100"), L("101-500"), VS("501-1000"), VL("1001-10000"), UL(" > 10000");

    private String label;

    private CompanySize(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

}
