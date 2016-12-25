package com.jedlab.payment;

import java.io.Serializable;

public class PaymentVO implements Serializable
{

    private Integer amount;
    private String merchantId;

    /**
     * shomare reside kharid
     */
    private String resNum;

    private String redirectUrl;

    public PaymentVO(Integer amount, String merchantId, String resNum, String redirectUrl)
    {
        this.amount = amount;
        this.merchantId = merchantId;
        this.resNum = resNum;
        this.redirectUrl = redirectUrl;
    }

    public Integer getAmount()
    {
        return amount;
    }

    public String getMerchantId()
    {
        return merchantId;
    }

    public String getResNum()
    {
        return resNum;
    }

    public String getRedirectUrl()
    {
        return redirectUrl;
    }

}
