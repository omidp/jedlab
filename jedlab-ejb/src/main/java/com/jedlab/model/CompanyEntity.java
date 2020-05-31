package com.jedlab.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.io.IOUtils;
import org.hibernate.annotations.Type;

import com.jedlab.Env;
import com.jedlab.framework.StringUtil;

@Entity
@Table(name = "company", schema = "public")
public class CompanyEntity extends BasePO
{

    @Column(name = "founded_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date foundedDate;

    @Column(name = "logo_path")
    private String logoPath;

    @Column(name = "cover_path")
    private String coverPath;

    @Column(name = "description")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Column(name = "full_description")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String fullDescription;

    @Column(name = "website")
    private String website;

    @Column(name = "name")
    private String name;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "founded")
    private String founded;

    @Column(name = "company_slug")
    private String companySlug;

    @Column(name = "tel")
    private String tel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private LocationEntity location;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private CompanySize companySize;

    @Column(name = "view_count", nullable = false, columnDefinition = " bigint DEFAULT 0 ")
    private Long viewCount = 0L;

    public String getLogoPath()
    {
        return logoPath;
    }

    public void setLogoPath(String logoPath)
    {
        this.logoPath = logoPath;
    }

    public String getCoverPath()
    {
        return coverPath;
    }

    public void setCoverPath(String coverPath)
    {
        this.coverPath = coverPath;
    }

    public String getFullDescription()
    {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription)
    {
        this.fullDescription = fullDescription;
    }

    public String getNameEn()
    {
        return nameEn;
    }

    public void setNameEn(String nameEn)
    {
        this.nameEn = nameEn;
    }

    public String getFounded()
    {
        return founded;
    }

    public void setFounded(String founded)
    {
        this.founded = founded;
    }

    public String getCompanySlug()
    {
        return companySlug;
    }

    public void setCompanySlug(String companySlug)
    {
        this.companySlug = companySlug;
    }

    public String getTel()
    {
        return tel;
    }

    public void setTel(String tel)
    {
        this.tel = tel;
    }

    public CompanySize getCompanySize()
    {
        return companySize;
    }

    public void setCompanySize(CompanySize companySize)
    {
        this.companySize = companySize;
    }

    public Date getFoundedDate()
    {
        return foundedDate;
    }

    public void setFoundedDate(Date foundedDate)
    {
        this.foundedDate = foundedDate;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public LocationEntity getLocation()
    {
        return location;
    }

    public void setLocation(LocationEntity location)
    {
        this.location = location;
    }

    public Long getViewCount()
    {
        return viewCount;
    }

    public void setViewCount(Long viewCount)
    {
        this.viewCount = viewCount;
    }

    @Transient
    public boolean isCoverImageExists()
    {
        return StringUtil.isNotEmpty(getCoverPath());
    }

    @Transient
    public boolean isLogoImageExists()
    {
        return StringUtil.isNotEmpty(getLogoPath());
    }

    @Transient
    public byte[] getLogoData()
    {
        if (isLogoImageExists())
        {
            try (FileInputStream fis = new FileInputStream(new File(getLogoPath())))
            {
                return IOUtils.toByteArray(fis);
            }
            catch (FileNotFoundException e)
            {

            }
            catch (IOException e)
            {

            }
        }
        return null;
    }

    @Transient
    public byte[] getCoverData()
    {
        if (isCoverImageExists())
        {
            try (FileInputStream fis = new FileInputStream(new File(getCoverPath())))
            {
                return IOUtils.toByteArray(fis);
            }
            catch (FileNotFoundException e)
            {

            }
            catch (IOException e)
            {

            }
        }
        return null;
    }

    @Transient
    public String getViewCountFormatted()
    {
        if(getViewCount() == null)
            return "0";
        return StringUtil.formatViewCount(getViewCount());
    }

}
