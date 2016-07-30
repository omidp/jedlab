package com.jedlab.model;

import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.jboss.seam.international.StatusMessage;

@Table(name = "course")
@Entity
@Where(clause = " active = true ")
public class Course extends BasePO
{

    @Column(name = "name")
    private String name;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "image", length = 2147483647)
    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "teacher")
    private String teacher;

    @Column(name = "description")
    private String description;

    @Column(name = "lang")
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private Level level;

    @Column(name = "is_active", columnDefinition="boolean DEFUALT true")
    private boolean active;

    public enum Language
    {
        ENGLISH(StatusMessage.getBundleMessage("English", "")), PERSIAN(StatusMessage.getBundleMessage("Persian", ""));

        private String label;

        private Language(String label)
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

    public enum Level
    {
        BEGINNER(StatusMessage.getBundleMessage("Beginner", "")), INTERMEDIATE(StatusMessage.getBundleMessage("Intermediate", "")), ADVANCE(
                StatusMessage.getBundleMessage("Advance", ""));

        private String label;

        private Level(String label)
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public byte[] getImage()
    {
        return image;
    }

    public void setImage(byte[] image)
    {
        this.image = image;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public String getTeacher()
    {
        return teacher;
    }

    public void setTeacher(String teacher)
    {
        this.teacher = teacher;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Language getLanguage()
    {
        return language;
    }

    public void setLanguage(Language language)
    {
        this.language = language;
    }

    public Level getLevel()
    {
        return level;
    }

    public void setLevel(Level level)
    {
        this.level = level;
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
