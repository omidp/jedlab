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
import com.jedlab.model.Story;
import com.jedlab.model.StoryInvoice;

@Name("storyPayment")
@Scope(ScopeType.CONVERSATION)
public class StoryPayment extends EntityController
{

    @RequestParameter
    private String uuid;

    private PaymentVO paymentVO;

    private StoryInvoice invoice;

    private Story story;

    public StoryInvoice getInvoice()
    {
        if (invoice == null)
            invoice = new StoryInvoice();
        return invoice;
    }

    public Story getStory()
    {
        if (story == null)
            story = new Story();
        return story;
    }

    public boolean isPaid()
    {
        return getInvoice().isPaid();
    }

    public PaymentVO getPaymentVO()
    {
        return paymentVO;
    }

    

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    @Transactional
    public void load()
    {
        TxManager.beginTransaction();
        TxManager.joinTransaction(getEntityManager());
        Long uid = (Long) getSessionContext().get(Constants.CURRENT_USER_ID);
        try
        {
            this.story = (Story) getEntityManager().createQuery("select s from Story s where s.uuid = :uuid")
                    .setParameter("uuid", getUuid()).setMaxResults(1).getSingleResult();
        }
        catch (NoResultException e)
        {
            throw new ErrorPageExceptionHandler("story not found");
        }
        this.invoice = findStoryInvoice(uid, story);
        if (invoice.isPaid() == false)
        {
            invoice.setResNo(RandomStringUtils.randomNumeric(15));
            if (invoice.getId() == null)
            {
                // insert
                invoice.setStory(story);
                Member m = new Member();
                m.setId(uid);
                invoice.setMember(m);
                invoice.setPaymentAmount(story.getPrice());
                getEntityManager().persist(invoice);
            }

            if (invoice.getId() != null)
            {
                // update
                int amt = invoice.getPaymentAmount().intValue();
                
                    getEntityManager().createQuery(
                            "update StoryInvoice si set si.resNo = :resNo, si.paymentAmount = :amt where si.id = :invoiceId")
                            .setParameter("resNo", invoice.getResNo()).setParameter("amt", new BigDecimal(amt))
                            .setParameter("invoiceId", invoice.getId()).executeUpdate();
                    invoice.setPaymentAmount(new BigDecimal(amt));
                
            }
            getEntityManager().flush();

            FacesContext facesContext = FacesContext.getCurrentInstance();
            String viewId = Pages.getCurrentViewId();
            String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext, Pages.getCurrentViewId());
            url = Pages.instance().encodeScheme(viewId, facesContext, url);
            url = url.substring(0, url.lastIndexOf("/") + 1);
            String redirectUrl = url + "storypaid.seam" + "?uuid=" + story.getUuid();
            // Toman to RIAL
            int bankAmt = invoice.getPaymentAmount().intValue() * 10;
            paymentVO = new PaymentVO(bankAmt, Env.getMerchantId(), invoice.getResNo(), redirectUrl);
        }
    }

    
    private StoryInvoice findStoryInvoice(Long uid, Story story)
    {
        try
        {
            StoryInvoice invoice = (StoryInvoice) getEntityManager()
                    .createQuery("select si from StoryInvoice si where si.member.id = :memId AND si.story.id = :storyId")
                    .setParameter("memId", uid).setParameter("storyId", story.getId()).setMaxResults(1).getSingleResult();
            return invoice;
        }
        catch (NoResultException e)
        {
        }
        return new StoryInvoice();
    }

    public boolean isPostback()
    {
        return getFacesContext().getRenderKit().getResponseStateManager().isPostback(getFacesContext());
    }

}
