package com.jedlab.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "salary", schema = "public")
public class SalaryEntity extends BasePO
{

    @Column(name = "amount")
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name="type")
    private SalaryType type;
    
    public enum SalaryType
    {
        MONTHLY, HOURLY, DAILY,YEARLY, CONTRACTOR, HAJMI; 
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public SalaryType getType()
    {
        return type;
    }

    public void setType(SalaryType type)
    {
        this.type = type;
    }
    
    
    


}
