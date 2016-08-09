package com.jedlab;

import java.net.URL;

import org.tasktops.jsf.PureJSF;

public class JsComponentResource extends ComponentResource
{

    private static final String RESOURCE_PATH = "/ui.scripts.js";

    @Override
    public URL getURL()
    {
        return PureJSF.getResource(RESOURCE_PATH);
    }

    @Override
    public String getContentType()
    {
        return "text/javascript";
    }
}
