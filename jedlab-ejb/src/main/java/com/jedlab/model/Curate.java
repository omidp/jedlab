package com.jedlab.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "curates")
@Entity
public class Curate extends BasePO
{

    @Column(name = "url")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_block_id", nullable = false)
    private PageBlock pageBlock;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public PageBlock getPageBlock()
    {
        return pageBlock;
    }

    public void setPageBlock(PageBlock pageBlock)
    {
        this.pageBlock = pageBlock;
    }

}
