package com.jedlab;

import java.io.Serializable;

public class CourseItem implements Serializable
{

    private Long id;
    private String label;
    private String value;
    private String desc;

    public CourseItem()
    {
    }

    public CourseItem(Long id, String label, String value, String desc)
    {
        this.id = id;
        this.label = label;
        this.value = value;
        this.desc = desc;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

}
