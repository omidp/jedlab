package com.jedlab.model;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.omidbiz.core.axon.internal.IgnoreElement;

import com.jedlab.framework.StringUtil;
import com.jedlab.model.enums.Gender;
import com.jedlab.model.enums.Privacy;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "member", uniqueConstraints = { @UniqueConstraint(columnNames = "user_name"),
        @UniqueConstraint(columnNames = "email_address"), @UniqueConstraint(columnNames = "activation_code") })
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@Name("user")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class Member extends BasePO
{
    
    public static final String INSTRUCTOR_DISC = "I";
    public static final String STUDENT_DISC = "S";

    @Column(name = "user_name")
    private String username;

    @Column(name = "discriminator", insertable=false, updatable=false,nullable=false)
    private String discriminator;

    @Column(name = "user_pass")
    private String password;

    @Column(name = "email_address")
    @Email(message = "#{messages['Invalid_Email']}")
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

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "age")
    private Integer age;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "image", length = 2147483647)
    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "privacy")
    @Enumerated(EnumType.STRING)
    private Privacy privacy;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "bio")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String bio;

    public String getDiscriminator()
    {
        return discriminator;
    }

    public void setDiscriminator(String discriminator)
    {
        this.discriminator = discriminator;
    }

    public String getMobileNo()
    {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo)
    {
        this.mobileNo = mobileNo;
    }

    public String getBio()
    {
        return bio;
    }

    public void setBio(String bio)
    {
        this.bio = bio;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public Integer getAge()
    {
        return age;
    }

    public void setAge(Integer age)
    {
        this.age = age;
    }

    @Transient
    public boolean getHasImage()
    {
        return getImage() != null && getImage().length > 0;
    }

    public byte[] getImage()
    {
        return image;
    }

    public void setImage(byte[] image)
    {
        this.image = image;
    }

    public Gender getGender()
    {
        return gender;
    }

    public void setGender(Gender gender)
    {
        this.gender = gender;
    }

    public Privacy getPrivacy()
    {
        return privacy;
    }

    public void setPrivacy(Privacy privacy)
    {
        this.privacy = privacy;
    }

    @Transient
    public String getFullname()
    {
        if (StringUtil.isNotEmpty(getFirstName()) && StringUtil.isNotEmpty(getLastName()))
            return getFirstName() + " " + getLastName();
        else if (StringUtil.isNotEmpty(getFirstName()))
            return getFirstName();
        else if (StringUtil.isNotEmpty(getLastName()))
            return getLastName();
        else
            return "";
    }

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

    @Transient
    public String getUsernameOrEmail()
    {
        if (StringUtil.isNotEmpty(getUsername()))
            return getUsername();
        return getEmail();
    }
    
    @IgnoreElement
    @Transient
    public String getBase64Image()
    {
        if (getImage() == null)
            return null;
        byte[] encodeBase64 = Base64.encodeBase64(getImage());
        try
        {
            return new String(encodeBase64, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new UnsupportedOperationException("image can not be found to convert to base64");
        }
    }
    
    @PrePersist
    public void prePrersist()
    {
        setEmail(getEmail().toLowerCase());
    }
    
    @PreUpdate
    public void preUpdate()
    {
        setEmail(getEmail().toLowerCase());
    }


}
