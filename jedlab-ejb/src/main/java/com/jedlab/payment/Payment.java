package com.jedlab.payment;

import java.math.BigDecimal;

import javax.faces.context.FacesContext;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityController;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.util.RandomStringUtils;

import com.jedlab.Env;
import com.jedlab.action.Constants;
import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TxManager;
import com.jedlab.model.Course;
import com.jedlab.model.Invoice;
import com.jedlab.model.Member;

@Name("payment")
@Scope(ScopeType.CONVERSATION)
public class Payment extends EntityController
{

    @RequestParameter
    private Long courseId;

    private PaymentVO paymentVO;

    private Invoice invoice;

    private Course course;

    private Boolean discountOk;

    private String discountCode;

    public Course getCourse()
    {
        if (course == null)
            course = new Course();
        return course;
    }

    public Boolean getDiscountOk()
    {
        return discountOk;
    }

    public void setDiscountOk(Boolean discountOk)
    {
        this.discountOk = discountOk;
    }

    public String getDiscountCode()
    {
        return discountCode;
    }

    public void setDiscountCode(String discountCode)
    {
        this.discountCode = discountCode;
    }

    public Invoice getInvoice()
    {
        if (invoice == null)
            invoice = new Invoice();
        return invoice;
    }

    public boolean isPaid()
    {
        return getInvoice().isPaid();
    }

    public PaymentVO getPaymentVO()
    {
        return paymentVO;
    }

    public Long getCourseId()
    {
        return courseId;
    }

    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }

    @Transactional
    public void load()
    {
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        Long uid = (Long) getSessionContext().get(Constants.CURRENT_USER_ID);
        try
        {
            this.course = (Course) getEntityManager().createQuery("select c from Course c where c.id = :courseId")
                    .setParameter("courseId", getCourseId()).setMaxResults(1).getSingleResult();
        }
        catch (NoResultException e)
        {
            throw new ErrorPageExceptionHandler("course not found");
        }
        this.invoice = findInvoice(uid, course);
        if (invoice.isPaid() == false)
        {
            invoice.setResNo(RandomStringUtils.randomNumeric(15));
            if (invoice.getId() == null)
            {
                // insert
                invoice.setCourse(course);
                Member m = new Member();
                m.setId(uid);
                invoice.setMember(m);
                if (course.isFree())
                    invoice.setPaymentAmount(new BigDecimal(course.getDownloadPrice()));
                else
                    invoice.setPaymentAmount(course.getPrice());
                int amt = invoice.getPaymentAmount().intValue();
                this.discountOk = (Boolean) getConversationContext().get("discountOk");
                if (this.discountOk != null && this.discountOk)
                {
                    amt = amt / 2;
                    invoice.setUsedDiscount(Boolean.TRUE);
                }
                else
                {
                    invoice.setUsedDiscount(Boolean.FALSE);
                }
                invoice.setPaymentAmount(new BigDecimal(amt));
                getEntityManager().persist(invoice);
            }

            if (invoice.getId() != null)
            {
                // update
                int amt = invoice.getPaymentAmount().intValue();
                if (course.isFree())
                    amt = course.getDownloadPrice().intValue();
                else
                    amt = course.getPrice().intValue();
                this.discountOk = (Boolean) getConversationContext().get("discountOk");
                if (this.discountOk != null && this.discountOk)
                {
                    amt = amt / 2;
                    getEntityManager().createQuery("update Invoice i set i.resNo = :resNo, i.paymentAmount = :amt, i.usedDiscount = true where i.id = :invoiceId")
                            .setParameter("resNo", invoice.getResNo())
                            .setParameter("amt", new BigDecimal(amt))
                            .setParameter("invoiceId", invoice.getId())
                            .executeUpdate();
                    invoice.setPaymentAmount(new BigDecimal(amt));
                    
                }
                else
                {
                    getEntityManager().createQuery("update Invoice i set i.resNo = :resNo, i.paymentAmount = :amt, i.usedDiscount = false where i.id = :invoiceId")
                            .setParameter("resNo", invoice.getResNo())
                            .setParameter("amt", new BigDecimal(amt))
                            .setParameter("invoiceId", invoice.getId()).executeUpdate();
                    invoice.setPaymentAmount(new BigDecimal(amt));
                }
            }
            getEntityManager().flush();

            FacesContext facesContext = FacesContext.getCurrentInstance();
            String viewId = Pages.getCurrentViewId();
            String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext, Pages.getCurrentViewId());
            url = Pages.instance().encodeScheme(viewId, facesContext, url);
            url = url.substring(0, url.lastIndexOf("/") + 1);
            String redirectUrl = url + "paid.seam" + "?c=" + getCourseId();
            //Toman to RIAL
            int bankAmt = invoice.getPaymentAmount().intValue() * 10;
            paymentVO = new PaymentVO(bankAmt, Env.getMerchantId(), invoice.getResNo(), redirectUrl);
        }
    }

    public String checkDiscount()
    {
        if (StringUtil.isNotEmpty(getDiscountCode()))
            this.discountOk = getDiscountCode().trim().equals(this.course.getDiscountCode());
        else
            this.discountOk = false;
        getConversationContext().set("discountOk", discountOk);
        return "checked";
    }

    private Invoice findInvoice(Long uid, Course course)
    {
        try
        {
            Invoice invoice = (Invoice) getEntityManager()
                    .createQuery("select i from Invoice i where i.member.id = :memId AND i.course.id = :courseId")
                    .setParameter("memId", uid).setParameter("courseId", course.getId()).setMaxResults(1).getSingleResult();
            return invoice;
        }
        catch (NoResultException e)
        {
        }
        return new Invoice();
    }

    public boolean isPostback()
    {
        return getFacesContext().getRenderKit().getResponseStateManager().isPostback(getFacesContext());
    }

}
