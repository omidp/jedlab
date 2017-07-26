package com.jedlab.payment;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.navigation.Pages;
import org.jboss.seam.security.Identity;

import com.jedlab.Env;
import com.jedlab.framework.CryptoUtil;

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
        String redirectUrl = url + "thankyou.seam?p=donate";
        Identity identity = Identity.instance();
        if(identity.isLoggedIn())
        {
            redirectUrl = url + String.format("/instructor/dashboard.seam?u=%s", CryptoUtil.encodeBase64(identity.getCredentials().getUsername()));
        }
        paymentVO = new PaymentVO(amount*10, Env.getMerchantId(), RandomStringUtils.randomAlphanumeric(12), redirectUrl);
    }

}
