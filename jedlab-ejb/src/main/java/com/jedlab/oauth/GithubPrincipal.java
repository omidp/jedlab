package com.jedlab.oauth;

import org.jboss.seam.security.SimplePrincipal;

public class GithubPrincipal extends SimplePrincipal
{

    public GithubPrincipal(String name)
    {
        super(name);
    }

}
