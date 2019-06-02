package com.jedlab.payment;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.navigation.Pages;

import com.jedlab.Env;
import com.jedlab.framework.RegexUtil;
import com.jedlab.framework.TxManager;
import com.jedlab.model.DonateEntity;

import foo.PaymentIFBinding;
import foo.PaymentIFBindingSoap;

@Scope(ScopeType.CONVERSATION)
@Name("donate")
public class Donate implements Serializable
{

    @RequestParameter(value = "State")
    private String state;

    @RequestParameter(value = "ResNum")
    private String resNum;

    @RequestParameter(value = "RefNum")
    private String refNum;

    @In
    EntityManager entityManager;

    private PaymentVO paymentVO;

    private BigDecimal totalDonateAmount;

    private DonateEntity instance;

    public DonateEntity getInstance()
    {
        if (instance == null)
            instance = new DonateEntity();
        return instance;
    }

    public PaymentVO getPaymentVO()
    {
        return paymentVO;
    }

    public BigDecimal getTotalDonateAmount()
    {
        if (totalDonateAmount == null)
        {
            totalDonateAmount = entityManager.createQuery("select sum(de.amount) from DonateEntity de where de.paid = true", BigDecimal.class).getSingleResult();
        }
        return totalDonateAmount;
    }

    public void pay()
    {
        if(getInstance().getAmount() == null || RegexUtil.ONLYDIGITS.matcher(String.valueOf(getInstance().getAmount())).matches() == false)
        {
            StatusMessages.instance().addFromResourceBundle(Severity.ERROR, "Only_Digit");
            return;
        }
        if(getInstance().getAmount().intValue() < 50000)
        {            
            StatusMessages.instance().addFromResourceBundle(Severity.ERROR, "Less_Than_Amt");
            return;
        }
        TxManager.beginTransaction();
        TxManager.joinTransaction(entityManager);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String viewId = Pages.getCurrentViewId();
        String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext, Pages.getCurrentViewId());
        url = Pages.instance().encodeScheme(viewId, facesContext, url);
        url = url.substring(0, url.lastIndexOf("/") + 1);
        String redirectUrl = url + "thankyou.seam?p=donate";
        String randomRes = RandomStringUtils.randomAlphanumeric(12);
        getInstance().setResNo(randomRes);
        entityManager.persist(getInstance());
        paymentVO = new PaymentVO(getInstance().getAmount().intValue() * 10, Env.getMerchantId(), randomRes, redirectUrl);
    }

    public void processPayment()
    {
        if ("OK".equals(state))
        {
            SSLUtilities.trustAllHostnames();
            SSLUtilities.trustAllHttpsCertificates();
            PaymentIFBindingSoap pay = new PaymentIFBinding().getPaymentIFBindingSoap();
            double amt = pay.verifyTransaction(refNum, Env.getMerchantId());
            TxManager.beginTransaction();
            TxManager.joinTransaction(entityManager);
            entityManager.createQuery("update DonateEntity de set de.paid = true where de.resNo = :no").setParameter("no", resNum).executeUpdate();
        }
    }

}
