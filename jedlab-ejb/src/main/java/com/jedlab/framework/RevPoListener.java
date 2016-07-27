package com.jedlab.framework;

import org.hibernate.envers.RevisionListener;
import org.jboss.seam.Component;
import org.jboss.seam.security.Credentials;

public class RevPoListener implements RevisionListener
{

	@Override
	public void newRevision(Object revisionEntity)
	{
		RevisionPO rev = (RevisionPO) revisionEntity;
		Credentials identity = (Credentials) Component.getInstance("org.jboss.seam.security.credentials");
		if(identity != null)
		    rev.setUsername(identity.getUsername());
		rev.setIpAddress(WebContext.instance().getClientIP());
	}

}
