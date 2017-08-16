package com.jedlab.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.international.StatusMessage;
import org.omidbiz.core.axon.internal.IgnoreElement;

import com.jedlab.action.Constants;
import com.jedlab.framework.StringUtil;

@NamedQuery(name = Course.FIND_WITH_INSTRUCTOR_BY_ID, query = "select c from Course c join fetch c.instructor i where c.id = :courseId")
@Entity
@Table(name = "course", schema = "public")
// @Where(clause = " is_active = 'true' ")
public class Course extends BasePO
{

    public static final String FIND_WITH_INSTRUCTOR_BY_ID = "course.findWithInstructor";

    @Column(name = "name")
    private String name;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "image", length = 2147483647)
    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    @Column(name = "price", nullable = false)
    @NotNull
    private BigDecimal price;

    @Column(name = "description")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Column(name = "requirement")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String requirement;

    @Column(name = "experience")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String experience;

    @Column(name = "process_id")
    private Long processInstanceId;

    @Column(name = "resources")
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Basic(fetch = FetchType.LAZY)
    private String resources;

    @Column(name = "lang")
    @Enumerated(EnumType.STRING)
    private Language language;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private Level level;

    @Column(name = "is_active", columnDefinition = "boolean DEFAULT true")
    private boolean active;

    @Column(name = "created_date", updatable = false, insertable = false, columnDefinition = " timestamp without time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    @OrderBy(value = "DESC")
    private Date createdDate;

    @Column(name = "is_sticky", columnDefinition = "boolean DEFAULT false")
    private boolean sticky;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    private List<Chapter> chapters = new ArrayList<>(0);

    @ManyToMany(targetEntity = Tag.class, cascade = CascadeType.ALL)
    @JoinTable(name = "course_tags", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<Tag>();

    @Column(name = "view_count", columnDefinition = " bigint DEFAULT 0")
    private Long viewCount;

    @Column(name = "download_price", columnDefinition = " bigint DEFAULT 0")
    private Integer downloadPrice;

    @Column(name = "discount_code")
    private String discountCode;

    @Transient
    private Long chapterCount;

    @Transient
    private boolean registered;

    @Transient
    private long registeredUserCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;

    @Column(name = "published")
    private boolean published;

    public String getDiscountCode()
    {
        return discountCode;
    }

    public void setDiscountCode(String discountCode)
    {
        this.discountCode = discountCode;
    }

    @Transient
    public boolean isOwner()
    {
        Object currentLogginId = Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        if (instructor == null || currentLogginId == null)
            return false;
        return instructor.getId().longValue() == ((Long) currentLogginId).longValue();
    }

    public Long getProcessInstanceId()
    {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId)
    {
        this.processInstanceId = processInstanceId;
    }

    public boolean isPublished()
    {
        return published;
    }

    public void setPublished(boolean published)
    {
        this.published = published;
    }

    public Instructor getInstructor()
    {
        return instructor;
    }

    public void setInstructor(Instructor instructor)
    {
        this.instructor = instructor;
    }

    public Integer getDownloadPrice()
    {
        return downloadPrice;
    }

    public void setDownloadPrice(Integer downloadPrice)
    {
        this.downloadPrice = downloadPrice;
    }

    public long getRegisteredUserCount()
    {
        return registeredUserCount;
    }

    public void setRegisteredUserCount(long registeredUserCount)
    {
        this.registeredUserCount = registeredUserCount;
    }

    public boolean isSticky()
    {
        return sticky;
    }

    public void setSticky(boolean sticky)
    {
        this.sticky = sticky;
    }

    public Long getViewCount()
    {
        return viewCount;
    }

    public void setViewCount(Long viewCount)
    {
        this.viewCount = viewCount;
    }

    @IgnoreElement
    public Set<Tag> getTags()
    {
        return tags;
    }

    public void setTags(Set<Tag> tags)
    {
        this.tags = tags;
    }

    public Long getChapterCount()
    {
        return chapterCount;
    }

    public void setChapterCount(Long chapterCount)
    {
        this.chapterCount = chapterCount;
    }

    public boolean isRegistered()
    {
        return registered;
    }

    public void setRegistered(boolean registered)
    {
        this.registered = registered;
    }

    public String getResources()
    {
        return resources;
    }

    public void setResources(String resources)
    {
        this.resources = resources;
    }

    public String getRequirement()
    {
        return requirement;
    }

    public void setRequirement(String requirement)
    {
        this.requirement = requirement;
    }

    public String getExperience()
    {
        return experience;
    }

    public void setExperience(String experience)
    {
        this.experience = experience;
    }

    @IgnoreElement
    public List<Chapter> getChapters()
    {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters)
    {
        this.chapters = chapters;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

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

    @IgnoreElement
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

    @Transient
    @IgnoreElement
    public boolean getHasImage()
    {
        return getImage() != null && getImage().length > 0;
    }

    @Transient
    public boolean isFree()
    {
        return getPrice() == null || getPrice().compareTo(BigDecimal.ZERO) == 0;
    }

    @Transient
    public boolean isFreeForDownload()
    {
        return getDownloadPrice() == null || getDownloadPrice().intValue() == 0;
    }

    @Transient
    public boolean isHasChapter()
    {
        return chapterCount != null && chapterCount.longValue() > 0;
    }

    @PrePersist
    public void prePersist()
    {
        setViewCount(new Long(0));
        setLanguage(Language.PERSIAN);
    }

    @Transient
    public boolean isEditable()
    {
        return processInstanceId == null && !active;
    }

    @Transient
    public String getViewCountFormatted()
    {
        if (getViewCount() == null)
            return "0";
        return StringUtil.formatViewCount(getViewCount());
    }
    
    @Transient
    public boolean isHasDiscount()
    {
        return StringUtil.isNotEmpty(getDiscountCode());
    }

}
