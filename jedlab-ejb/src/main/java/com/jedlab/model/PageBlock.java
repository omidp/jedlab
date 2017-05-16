package com.jedlab.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.omidbiz.core.axon.internal.IgnoreElement;

@Table(name = "page_blocks")
@Entity
public class PageBlock extends BasePO
{

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pageBlock")
    Set<Curate> curates = new HashSet<Curate>(0);

    @IgnoreElement
    public Set<Curate> getCurates()
    {
        return curates;
    }

    public void setCurates(Set<Curate> curates)
    {
        this.curates = curates;
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

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

}
