package com.jedlab.dao.home;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.HibernateEntityHome;
import org.jboss.seam.log.Log;

import com.jedlab.JedLab;
import com.jedlab.model.CompanyEntity;
import com.jedlab.model.ContractEntity;
import com.jedlab.model.PositionEntity;
import com.jedlab.model.SalaryEntity;
import com.jedlab.model.WorkingExprienceEntity;

@Name("workingExpHome")
@Scope(ScopeType.CONVERSATION)
public class WorkingExpHome extends HibernateEntityHome<WorkingExprienceEntity>
{

    @Logger
    Log log;

    @In(create = true)
    JedLab jedLab;

    PositionEntity position;

    ContractEntity contract;

    SalaryEntity salary;
    
    String companySlug;
    
    CompanyEntity company;

    public String getCompanySlug()
    {
        return companySlug;
    }

    public void setCompanySlug(String companySlug)
    {
        this.companySlug = companySlug;
    }

    public Long getWorkExpId()
    {
        return (Long) getId();
    }

    public void setWorkExpId(Long id)
    {
        setId(id);
    }

    public void load()
    {
        WorkingExprienceEntity weEntity = getInstance();
        salary = weEntity.getSalary();
        position = weEntity.getPosition();
        contract = weEntity.getContract();
        company = (CompanyEntity) getSession().createCriteria(CompanyEntity.class).add(Restrictions.eq("companySlug", getCompanySlug())).uniqueResult();
        if (isIdDefined())
        {
        }

    }

    public void wire()
    {
        getInstance().setMemebr(jedLab.getCurrentUser());
        getInstance().setCompany(getCompany());
    }

    @Override
    public String persist()
    {
        wire();
        return super.persist();
    }

    @Override
    public String update()
    {
        wire();
        return super.update();
    }

    @Override
    protected WorkingExprienceEntity loadInstance()
    {
        Criteria criteria = getSession().createCriteria(WorkingExprienceEntity.class, "we");
        criteria.createCriteria("we.position", "p", Criteria.LEFT_JOIN);
        criteria.createCriteria("we.contract", "c", Criteria.LEFT_JOIN);
        criteria.createCriteria("we.salary", "s", Criteria.LEFT_JOIN);
        criteria.add(Restrictions.eq("id", getId()));
        return (WorkingExprienceEntity) criteria.uniqueResult();
    }

    public PositionEntity getPosition()
    {
        if (position == null)
            position = new PositionEntity();
        return position;
    }

    public void setPosition(PositionEntity position)
    {
        this.position = position;
    }

    public ContractEntity getContract()
    {
        if (contract == null)
            contract = new ContractEntity();
        return contract;
    }

    public void setContract(ContractEntity contract)
    {
        this.contract = contract;
    }

    public SalaryEntity getSalary()
    {
        if (salary == null)
            salary = new SalaryEntity();
        return salary;
    }

    public void setSalary(SalaryEntity salary)
    {
        this.salary = salary;
    }

    public CompanyEntity getCompany()
    {
        return company;
    }

    public void setCompany(CompanyEntity company)
    {
        this.company = company;
    }
    
    
    

}
