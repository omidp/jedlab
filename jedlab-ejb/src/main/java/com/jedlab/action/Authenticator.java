package com.jedlab.action;

import javax.ejb.Local;

@Local
public interface Authenticator
{

    boolean authenticate();

    public boolean isCaptchaRequired();

}