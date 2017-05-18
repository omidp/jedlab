package com.jedlab.tika.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.xml.sax.SAXException;

@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Name("htmlContentParser")
public class HtmlContentParser
{

    @Unwrap
    public ContentParser getParser()
    {
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext pcontext = new ParseContext();
        HtmlParser htmlparser = new HtmlParser();
        return new ContentParser(handler, metadata, pcontext, htmlparser);
    }

    public static ContentParser instance()
    {
        if (!Contexts.isApplicationContextActive())
        {
            throw new IllegalStateException("No active application context");
        }
        return (ContentParser) Component.getInstance(HtmlContentParser.class, ScopeType.APPLICATION);
    }

    public static class ContentParser
    {
        private final BodyContentHandler handler;
        private final Metadata metadata;
        private final ParseContext pcontext;
        private final HtmlParser htmlparser;

        public ContentParser(BodyContentHandler handler, Metadata metadata, ParseContext pcontext, HtmlParser htmlparser)
        {
            this.handler = handler;
            this.metadata = metadata;
            this.pcontext = pcontext;
            this.htmlparser = htmlparser;
        }

        public void parse(String content) throws UnsupportedEncodingException, IOException, SAXException, TikaException
        {
            htmlparser.parse(new ByteArrayInputStream(content.getBytes("UTF-8")), handler, metadata, pcontext);
        }

        public BodyContentHandler handler()
        {
            return handler;
        }

        public Metadata metaData()
        {
            return metadata;
        }

        public ParseContext parseContext()
        {
            return pcontext;
        }

        public HtmlParser htmlParser()
        {
            return htmlparser;
        }

    }

}
