package com.jedlab.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Type;

import com.jedlab.framework.StringUtil;
import com.jedlab.model.enums.Gender;
import com.jedlab.model.enums.Privacy;

@Entity
@PrimaryKeyJoinColumn(name = "member_id")
@Table(name = "instructor", uniqueConstraints = { @UniqueConstraint(columnNames = "mobile_no")})
public class Instructor extends Member
{

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "bio")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String bio;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "image", length = 2147483647)
    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

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

}
