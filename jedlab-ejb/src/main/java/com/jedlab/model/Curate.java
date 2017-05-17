package com.jedlab.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.omidbiz.core.axon.internal.IgnoreElement;

import com.jedlab.framework.StringUtil;

@Table(name = "curates")
@Entity
public class Curate extends BasePO
{

    @Column(name = "url", nullable = false)
    @NotNull
    private String url;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_block_id", nullable = false)
    private PageBlock pageBlock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @Column(name = "description")
    private String description;

    @Column(name = "keywords")
    private String keywords;

    @Column(name = "image_url")
    private String imageUrl;
    
    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getKeywords()
    {
        return keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    @Transient
    public String getTitleOrUrl()
    {
        if (StringUtil.isEmpty(getTitle())) return getUrl();
        return getTitle();
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    @IgnoreElement
    public PageBlock getPageBlock()
    {
        return pageBlock;
    }

    public void setPageBlock(PageBlock pageBlock)
    {
        this.pageBlock = pageBlock;
    }

    @IgnoreElement
    public Page getPage()
    {
        return page;
    }

    public void setPage(Page page)
    {
        this.page = page;
    }

}
