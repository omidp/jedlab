package com.jedlab.story;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.ServletLifecycle;

@Name("markdownProcessor")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Startup
public class HtmlMarkdownProcessor
{

    public static final String MARKDOWN = "markdown_md";

    private ServletContext context = null;

    @Create
    public void create()
    {
        context = ServletLifecycle.getServletContext();
        this.context.setAttribute(MARKDOWN, buildMd(context));
    }

    private HtmlMarkdownHolder buildMd(ServletContext context2)
    {
        List<Extension> extensions = Arrays.asList(TablesExtension.create(), StrikethroughExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).nodeRendererFactory(new HtmlNodeRendererFactory() {
            public NodeRenderer create(HtmlNodeRendererContext context)
            {
                return new RTLTextBlockNodeRenderer(context);
            }
        }).build();
        return new HtmlMarkdownHolder(parser, renderer);
    }

    public static class HtmlMarkdownHolder
    {
        private Parser parser;
        private HtmlRenderer renderer;

        public HtmlMarkdownHolder(Parser parser, HtmlRenderer renderer)
        {
            this.parser = parser;
            this.renderer = renderer;
        }

        public Parser getParser()
        {
            return parser;
        }

        public HtmlRenderer getRenderer()
        {
            return renderer;
        }

    }

    
    class RTLTextBlockNodeRenderer implements NodeRenderer
    {

        private final HtmlWriter html;
        private final HtmlNodeRendererContext htmlContext;
        private final Pattern ENGLISH_CHARACTER_PATTERN = Pattern.compile("^\\w");

        RTLTextBlockNodeRenderer(HtmlNodeRendererContext context)
        {
            this.html = context.getWriter();
            this.htmlContext = context;
        }

        @Override
        public Set<Class<? extends Node>> getNodeTypes()
        {
            // Return the node types we want to use this renderer for.
            return Collections.<Class<? extends Node>> singleton(Text.class);
        }

        @Override
        public void render(Node node)
        {
            Text content = (Text) node;

            String literal = content.getLiteral();
            Map<String, String> attributes = new LinkedHashMap<>();
//            if (new BigInteger(1, literal.getBytes()).intValue() < 0x80)
            if(ENGLISH_CHARACTER_PATTERN.matcher(literal).find())
                attributes.put("class", "ltr-dir");
            else
                attributes.put("class", "rtl-dir");
            html.tag("span", attributes);
            html.text(literal);
            html.tag("/span");
        }
    }

}
