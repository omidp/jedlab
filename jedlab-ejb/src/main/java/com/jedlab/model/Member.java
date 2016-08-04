package com.jedlab.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "member", uniqueConstraints = { @UniqueConstraint(columnNames = "user_name"),
        @UniqueConstraint(columnNames = "email_address") , @UniqueConstraint(columnNames = "activation_code")})
@Name("user")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class Member extends BasePO
{

    @Column(name = "user_name")
    private String username;

    @Column(name = "user_pass")
    private String password;

    @Column(name = "email_address")
    @Email(message="#{messages['Invalid_Email']}")
    @NotNull
    private String email;

    @Column(name = "join_date", updatable = false, insertable = false, columnDefinition = " timestamp without time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    private Date joinDate;

    @Column(name = "is_active", columnDefinition = " boolean DEFAULT true")
    private boolean active;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "recover_passwd_code")
    private String recoverPasswordCode;

    public String getActivationCode()
    {
        return activationCode;
    }

    public void setActivationCode(String activationCode)
    {
        this.activationCode = activationCode;
    }

    public String getRecoverPasswordCode()
    {
        return recoverPasswordCode;
    }

    public void setRecoverPasswordCode(String recoverPasswordCode)
    {
        this.recoverPasswordCode = recoverPasswordCode;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Date getJoinDate()
    {
        return joinDate;
    }

    public void setJoinDate(Date joinDate)
    {
        this.joinDate = joinDate;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

}
