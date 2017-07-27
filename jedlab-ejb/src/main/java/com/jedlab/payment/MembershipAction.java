package com.jedlab.payment;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityController;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.security.Identity;

import com.jedlab.Env;
import com.jedlab.JedLab;
import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.framework.TxManager;
import com.jedlab.model.Instructor;
import com.jedlab.model.Membership;

import foo.PaymentIFBinding;
import foo.PaymentIFBindingSoap;

@Scope(ScopeType.CONVERSATION)
@Name("membershipAction")
public class MembershipAction extends EntityController
{

    @RequestParameter(value = "State")
    private String state;

    @RequestParameter(value = "ResNum")
    private String resNum;

    @RequestParameter(value = "RefNum")
    private String refNum;

    private PaymentVO paymentVO;

    public PaymentVO getPaymentVO()
    {
        return paymentVO;
    }

    @Transactional
    public void pay(Integer amount)
    {
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        Long userId = JedLab.instance().getCurrentUserId();
        if (userId == null)
            throw new ErrorPageExceptionHandler("user id is null");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String y = String.valueOf(calendar.get(Calendar.YEAR));
        Membership membership = new Membership();        
        try
        {
            membership = (Membership) getEntityManager()
                    .createQuery("select m from Membership m where m.member.id = :memId and to_char(m.createdDate, 'yyyy') = :year")
                    .setParameter("year", y).setParameter("memId", userId).setMaxResults(1).getSingleResult();
        }
        catch (NoResultException e)
        {
            String resNo = RandomStringUtils.randomAlphanumeric(25);
            membership.setAmount(new BigDecimal(amount));
            Instructor m = new Instructor();
            m.setId(userId);
            membership.setMember(m);
            membership.setResNo(resNo);
            getEntityManager().persist(membership);
            getEntityManager().flush();
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String viewId = Pages.getCurrentViewId();
        String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext, Pages.getCurrentViewId());
        url = Pages.instance().encodeScheme(viewId, facesContext, url);
        url = url.substring(0, url.lastIndexOf("/") + 1);
        String redirectUrl = url + "thankyou.seam";
        paymentVO = new PaymentVO(amount * 10, Env.getMerchantId(), membership.getResNo(), redirectUrl);
    }

    @Transactional
    public void processPayment()
    {
        if ("OK".equals(state) && Identity.instance().isLoggedIn())
        {
            TxManager.beginTransaction();
            TxManager.joinTransaction(getEntityManager());
            try
            {
                Long userId = JedLab.instance().getCurrentUserId();
                Membership m = (Membership) getEntityManager().createQuery("select m from Membership m where m.resNo = :resNo and m.member.id = :memId")
                        .setParameter("memId", userId)
                        .setParameter("resNo", resNum).setMaxResults(1).getSingleResult();
                SSLUtilities.trustAllHostnames();
                SSLUtilities.trustAllHttpsCertificates();
                PaymentIFBindingSoap pay = new PaymentIFBinding().getPaymentIFBindingSoap();
                double amt = pay.verifyTransaction(refNum, Env.getMerchantId());
                m.setPaid(true);
                m.setRefNo(refNum);
                getEntityManager().createQuery("update Member m set m.approved = true where m.id = :memId").setParameter("memId", userId).executeUpdate();
                getEntityManager().flush();
            }
            catch (NoResultException e)
            {
            }
        }
    }
}
