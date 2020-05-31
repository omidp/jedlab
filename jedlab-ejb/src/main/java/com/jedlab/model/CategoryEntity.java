package com.jedlab.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "category", schema = "public")
public class CategoryEntity extends BasePO
{

    @Column(name="name")
    private String name;
    
}
