package com.jedlab.payment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesManager;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.framework.EntityController;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;

import com.jedlab.Env;
import com.jedlab.action.Constants;
import com.jedlab.framework.CollectionUtil;
import com.jedlab.framework.ErrorPageExceptionHandler;
import com.jedlab.framework.StringUtil;
import com.jedlab.framework.TxManager;
import com.jedlab.model.Chapter;
import com.jedlab.model.Course;
import com.jedlab.model.Invoice;
import com.jedlab.model.Member;
import com.jedlab.model.MemberCourse;

import foo.PaymentIFBinding;
import foo.PaymentIFBindingSoap;

@Name("paidInvoice")
@Scope(ScopeType.CONVERSATION)
public class Paid extends EntityController
{

    @Logger
    Log log;
    
    @In(create = true)
    private Renderer renderer;

    @RequestParameter(value = "c")
    private Long courseId;

    private Course course;

    @RequestParameter(value = "State")
    private String state;

    @RequestParameter(value = "ResNum")
    private String resNum;

    @RequestParameter(value = "RefNum")
    private String refNum;

    private String errorMessage;

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    @Transactional
    public void load()
    {
        if (StringUtil.isNotEmpty(state) && StringUtil.isNotEmpty(refNum) && StringUtil.isNotEmpty(resNum))
        {
            TxManager.beginTransaction();
            TxManager.joinTransaction(getEntityManager());
            processPayment();
        }
        else
        {
            // user canceled payment
            if (this.courseId != null)
            {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("courseId", this.courseId);
                FacesManager.instance().redirect("/course.seam", params, false, false);
            }
            else
            {
                FacesManager.instance().redirect("/courseList.seam", new HashMap<String, Object>(), false, false);
            }
        }
    }

    private void processPayment()
    {
        this.errorMessage = StatusMessage.getBundleMessage("Payment_Error", "");
        Long userId = (Long) getSessionContext().get(Constants.CURRENT_USER_ID);
        if ("OK".equals(state))
        {
            // everything is ok
            try
            {
                Long uid = userId;
                Invoice invoice = (Invoice) getEntityManager().createQuery("select i from Invoice i where i.resNo = :resNo")
                        .setParameter("resNo", resNum).setMaxResults(1).getSingleResult();
                if (invoice == null)
                {
                    log.info("purchase canceled because invoice not found with state " + state + " for user_id : " + uid
                            + " and course_id : " + String.valueOf(courseId));
                    throw new ErrorPageExceptionHandler(this.errorMessage);
                }
                if (uid == null)
                    uid = invoice.getMember().getId();
                if (this.courseId == null || uid == null)
                {
                    log.info("purchase canceled because course not found with state " + state + " for user_id : " + uid
                            + " and course_id : " + String.valueOf(courseId));
                    throw new ErrorPageExceptionHandler(this.errorMessage);
                }
                this.course = (Course) getEntityManager().createQuery("select c from Course c where c.id = :courseId")
                        .setParameter("courseId", this.courseId).setMaxResults(1).getSingleResult();
                log.info("processing payment with state " + state + " for user_id : " + uid
                        + " and course_id : " + String.valueOf(courseId));
                //
                SSLUtilities.trustAllHostnames();
                SSLUtilities.trustAllHttpsCertificates();
                PaymentIFBindingSoap pay = new PaymentIFBinding().getPaymentIFBindingSoap();
                double amt = pay.verifyTransaction(refNum, Env.getMerchantId());
                double invoiceAmt = invoice.getPaymentAmount().doubleValue() * 10;
                log.info("invoice amount : " + invoiceAmt);
                log.info("bank amount : " + amt);                
                if (invoiceAmt == amt)
                {
                    // payment is ok
                    invoice.setPaid(true);
                    invoice.setRefNo(refNum);
                    invoice.setDescription(this.course.getName());
                    getEntityManager().flush();
                    if (this.course.isFree())
                    {
                        getEntityManager()
                                .createQuery(
                                        "update MemberCourse mc set mc.canDownload = true where mc.member.id = :memId AND mc.course.id = :courseId")
                                .setParameter("memId", uid).setParameter("courseId", this.courseId).executeUpdate();
                    }
                    else
                    {
                        List<Chapter> chapters = getEntityManager()
                                .createQuery(
                                        "select c from Chapter c  where c.course.id = :courseId AND c.id  NOT IN (select mc.chapter.id from MemberCourse mc where mc.course.id = c.course.id AND mc.member.id = :memId)")
                                .setParameter("courseId", courseId).setParameter("memId", uid).getResultList();
                        if (CollectionUtil.isNotEmpty(chapters))
                        {
                            for (Chapter chapter : chapters)
                            {
                                MemberCourse mc = new MemberCourse();
                                mc.setCanDownload(true);
                                mc.setPaid(true);
                                mc.setChapter(chapter);
                                mc.setCourse(course);
                                //
                                Member m = new Member();
                                m.setId(uid);
                                mc.setMember(m);
                                getEntityManager().persist(mc);
                            }
                        }
                        log.info("purchase successfull with state " + state + " for user_id : " + uid
                                + " and course_id : " + String.valueOf(courseId));     
                    }
                    getEntityManager().flush();
                    this.errorMessage = StatusMessage.getBundleMessage("Payment_Successful", "");
                    StatusMessages.instance().addFromResourceBundle("Payment_Successful");
                    Events.instance().raiseAsynchronousEvent("sendPaymentEmail", this.course, invoice, invoice.getMember());
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("courseId", this.courseId);
                    FacesManager.instance().redirect("/course.seam", params, false, false);
                }
                else
                {
                    log.info("purchase canceled with state " + state + " for user_id : " + uid + " and course_id : "
                            + String.valueOf(courseId) + " with error code : " + amt);
                    processError((int) amt);
                    pay.reverseTransaction(refNum, Env.getMerchantId(), Env.getMerchantId(), Env.getMerchantPass());
                }
            }
            catch (NoResultException e)
            {
                log.info("purchase canceled because invoice not found with state " + state + " for user_id : " + userId
                        + " and course_id : " + String.valueOf(courseId));
            }
        }
        else
        {
            log.info("purchase canceled with state " + state + " for user_id : " + userId + " and course_id : " + String.valueOf(courseId));
        }
    }

    @Observer("sendPaymentEmail")
    public void sendPaymentEmail(Course course, Invoice invoice, Member member)
    {
        getConversationContext().set("username", member.getUsername());
        StringBuilder content = new StringBuilder();
        String thankYou = interpolate(StatusMessage.getBundleMessage("Paid_Thank_You", ""), invoice.getResNo(), invoice.getPaymentAmount()
                .doubleValue());
        content.append(thankYou).append("<br />");
        if (course.isFree())
        {
            String dlMsg = interpolate(StatusMessage.getBundleMessage("Paid_Link_Email_Content", ""), course.getName());
            content.append(dlMsg).append("<br />");
            ;
        }
        String t = StatusMessage.getBundleMessage("Thank_You_Paid", "");
        content.append(t);
        getConversationContext().set("content", content.toString());
        Contexts.getConversationContext().set("subject", interpolate(StatusMessage.getBundleMessage("Receipt", ""), invoice.getResNo()));
        Contexts.getConversationContext().set("memberEmail", member.getEmail());
        renderer.render("/mailTemplates/announcement.xhtml");
    }

    private void processError(int amt)
    {
        switch (amt)
        {
        case -1:

            break;
        case -2:

            break;
        case -3:

            break;
        case -4:

            break;
        case -5:

            break;
        case -6:

            break;
        case -7:

            break;
        case -8:

            break;
        case -9:

            break;
        case -10:

            break;
        case -11:

            break;
        case -12:

            break;
        case -13:

            break;
        case -14:

            break;
        case -15:

            break;
        case -16:

            break;
        case -17:

            break;
        case -18:

            break;

        default:
            break;
        }
    }

}
