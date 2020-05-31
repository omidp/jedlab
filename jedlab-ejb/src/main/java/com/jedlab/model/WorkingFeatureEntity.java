package com.jedlab.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "working_features", schema = "public")
public class WorkingFeatureEntity extends BasePO
{

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WorkFeatureStatus status;

    @Column(name = "title")
    private String title;

    public WorkFeatureStatus getStatus()
    {
        return status;
    }

    public void setStatus(WorkFeatureStatus status)
    {
        this.status = status;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public enum WorkFeatureStatus
    {
        PROS, CONS
    }

}
