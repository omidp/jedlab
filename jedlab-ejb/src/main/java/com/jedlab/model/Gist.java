package com.jedlab.model;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.NGramFilterFactory;
import org.apache.solr.analysis.PersianNormalizationFilterFactory;
import org.apache.solr.analysis.SnowballPorterFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.AnalyzerDefs;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.hibernate.validator.constraints.Length;
import org.jboss.seam.contexts.Contexts;
import org.ocpsoft.prettytime.PrettyTime;

import com.jedlab.action.Constants;

@Entity
@Table(name = "gist", schema = "public")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Indexed
@AnalyzerDefs({
        @AnalyzerDef(name = "en", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = { @Parameter(name = "language", value = "English") }) }),
        @AnalyzerDef(name = "fa", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = { @TokenFilterDef(factory = PersianNormalizationFilterFactory.class) }),
        @AnalyzerDef(name = "ngrams", tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class), filters = {
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = NGramFilterFactory.class, params = { @Parameter(name = "minGramSize", value = "3"),
                        @Parameter(name = "maxGramSize", value = "3") }) }) })
public class Gist extends BasePO
{

    @Column(name = "file_name", nullable = false)
    @NotNull
    private String fileName;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "content", nullable = false)
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    @Length(min = 2)
    private String content;

    @Column(name = "orig_content", nullable = false)
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    @Length(min = 2)
    @Fields({ @Field(name = "title:en", analyzer = @Analyzer(definition = "en")),
            @Field(name = "title:ngrams", analyzer = @Analyzer(definition = "ngrams")) })
    private String origContent;

    @Column(name = "short_content", nullable = false)
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    @Length(min = 2)
    private String shortContent;

    @Column(name = "is_private")
    private boolean privateGist;

    @Column(name = "created_date", updatable = false, insertable = false, columnDefinition = " timestamp with time zone DEFAULT now()")
    @Temporal(TemporalType.TIMESTAMP)
    @org.hibernate.annotations.Generated(org.hibernate.annotations.GenerationTime.INSERT)
    @OrderBy
    private Date createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "description")
    @Fields({ @Field(name = "description:en", analyzer = @Analyzer(definition = "en")),
            @Field(name = "description:fa", analyzer = @Analyzer(definition = "fa")) })
    private String description;

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public boolean isPrivateGist()
    {
        return privateGist;
    }

    public void setPrivateGist(boolean privateGist)
    {
        this.privateGist = privateGist;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

    public Member getMember()
    {
        return member;
    }

    public void setMember(Member member)
    {
        this.member = member;
    }

    @Transient
    public String getSocialDate()
    {
        PrettyTime p = new PrettyTime(new Locale("fa", "IR"));
        return p.format(getCreatedDate());
    }

    public String getShortContent()
    {
        return shortContent;
    }

    public void setShortContent(String shortContent)
    {
        this.shortContent = shortContent;
    }

    public String getOrigContent()
    {
        return origContent;
    }

    public void setOrigContent(String origContent)
    {
        this.origContent = origContent;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    @Transient
    public boolean isOwner()
    {
        Object currentLogginId = Contexts.getSessionContext().get(Constants.CURRENT_USER_ID);
        if (member == null || currentLogginId == null)
            return false;
        return member.getId().longValue() == ((Long) currentLogginId).longValue();
    }

    @PrePersist
    public void prePersist()
    {
        setUuid(UUID.randomUUID().toString());
    }

}
