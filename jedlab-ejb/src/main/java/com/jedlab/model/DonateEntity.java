package com.jedlab.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Table(name = "donates")
@Entity
public class DonateEntity extends BasePO
{

    @Column(name = "amt", nullable = false)
    private BigDecimal amount;

    @Column(name = "card_no")
    private String cardNo;

    @Column(name = "created_date", updatable = false, insertable = false, columnDefinition = " timestamp without time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    @OrderBy(value = "DESC")
    private Date createdDate;

    @Column(name = "is_paid")
    private boolean paid;

    @Column(name = "res_num")
    private String resNo;

    public String getResNo()
    {
        return resNo;
    }

    public void setResNo(String resNo)
    {
        this.resNo = resNo;
    }

    public boolean isPaid()
    {
        return paid;
    }

    public void setPaid(boolean paid)
    {
        this.paid = paid;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public String getCardNo()
    {
        return cardNo;
    }

    public void setCardNo(String cardNo)
    {
        this.cardNo = cardNo;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

    @PrePersist
    public void prePersist()
    {
        setCreatedDate(new Date());
    }

}
