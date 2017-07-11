package com.jedlab.payment;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.navigation.Pages;

import com.jedlab.Env;

@Scope(ScopeType.CONVERSATION)
@Name("donate")
public class Donate implements Serializable
{

    private PaymentVO paymentVO;

    public PaymentVO getPaymentVO()
    {
        return paymentVO;
    }

    public void pay(Integer amount)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String viewId = Pages.getCurrentViewId();
        String url = facesContext.getApplication().getViewHandler().getActionURL(facesContext, Pages.getCurrentViewId());
        url = Pages.instance().encodeScheme(viewId, facesContext, url);
        url = url.substring(0, url.lastIndexOf("/") + 1);
        String redirectUrl = url + "home.seam";
        paymentVO = new PaymentVO(amount*10, Env.getMerchantId(), RandomStringUtils.randomAlphanumeric(12), redirectUrl);
    }

}
