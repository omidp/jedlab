package com.jedlab.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "location", schema = "public")
public class LocationEntity extends BasePO
{

    @Column(name="name")
    private String name;
    
}
