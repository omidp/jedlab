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

import org.hibernate.annotations.Type;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.international.StatusMessage;

import com.jedlab.framework.StringUtil;

@Entity
@PrimaryKeyJoinColumn(name = "member_id")
@Table(name = "student")
public class Student extends Member
{

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

    public enum Gender
    {
        MALE(StatusMessage.getBundleMessage("Male", "")), FEMALE(StatusMessage.getBundleMessage("Female", ""));

        private String label;

        private Gender(String label)
        {
            this.label = label;
        }

        public String getLabel()
        {
            return label;
        }

        public void setLabel(String label)
        {
            this.label = label;
        }

    }

    public enum Privacy
    {
        EVERYONE(StatusMessage.getBundleMessage("Everyone", "")), OnlyMe(StatusMessage.getBundleMessage("OnlyMe", ""));
        private String label;

        private Privacy(String label)
        {
            this.label = label;
        }

        public String getLabel()
        {
            return label;
        }

        public void setLabel(String label)
        {
            this.label = label;
        }

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

}
