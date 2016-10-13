package com.jedlab.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.jboss.seam.contexts.Contexts;
import org.ocpsoft.prettytime.PrettyTime;

import com.jedlab.action.Constants;
import com.jedlab.framework.CollectionUtil;

@Table(name = "test_case")
@Entity
public class TestCase extends BasePO
{

    @Column(name = "input_params", columnDefinition = "character varying[]")
    @Type(type = "com.jedlab.model.type.StringArrayUserType")
    private String[] inputParams;

    @Column(name = "result")
    private String result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    public String[] getInputParams()
    {
        return inputParams;
    }

    public void setInputParams(String[] inputParams)
    {
        this.inputParams = inputParams;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public Question getQuestion()
    {
        return question;
    }

    public void setQuestion(Question question)
    {
        this.question = question;
    }

}
