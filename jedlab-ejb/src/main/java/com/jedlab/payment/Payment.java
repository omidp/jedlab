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

    private Invoice paidInvoice;

    public Invoice getPaidInvoice()
    {
        if (paidInvoice == null)
            paidInvoice = new Invoice();
        return paidInvoice;
    }

    public boolean isPaid()
    {
        return getPaidInvoice().isPaid();
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
        Course course = (Course) getEntityManager().createQuery("select c from Course c where c.id = :courseId")
                .setParameter("courseId", getCourseId()).setMaxResults(1).getSingleResult();
        Invoice invoice = findInvoice(uid, course);
        // paid invoice is handled in UI
        if (invoice.isPaid())
            this.paidInvoice = invoice;
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
                getEntityManager().persist(invoice);
            }

            if (invoice.getId() != null)
            {
                // update
                getEntityManager().createQuery("update Invoice i set i.resNo = :resNo where i.id = :invoiceId")
                .setParameter("resNo", invoice.getResNo()).setParameter("invoiceId", invoice.getId())
                        .executeUpdate();
            }
            getEntityManager().flush();

            FacesContext facesContext = FacesContext.getCurrentInstance();
            String viewId = Pages.getCurrentViewId();
            String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext, Pages.getCurrentViewId());
            url = Pages.instance().encodeScheme(viewId, facesContext, url);
            url = url.substring(0, url.lastIndexOf("/") + 1);
            String redirectUrl = url + "paid.seam" + "?c=" + getCourseId();
            paymentVO = new PaymentVO(invoice.getPaymentAmount().intValue() * 10, Env.getMerchantId(), invoice.getResNo(), redirectUrl);
        }
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

}
