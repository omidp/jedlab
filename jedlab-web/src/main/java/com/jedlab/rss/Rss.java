package com.jedlab.rss;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;
import org.jboss.seam.text.SeamTextLexer;
import org.jboss.seam.text.SeamTextParser;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import com.jedlab.model.Course;

@Name("rss")
@Scope(ScopeType.CONVERSATION)
public class Rss
{

    private Feed feed;

    @In
    EntityManager entityManager;
    
    @Logger
    Log log;

    public Feed getFeed()
    {
        return feed;
    }

    @Create
    public void create()
    {
        feed = new Feed();
        List<Entry> entries = new ArrayList<Entry>();
        //
        List<Course> resultList = entityManager.createQuery("select c from Course c").setMaxResults(1000).getResultList();
        for (Course course : resultList)
        {
            Entry entry = new Entry();
            entry.setAuthor("JEDLab");
            entry.setLink(String.format("http://jedlab.ir/course/%s", course.getId()));
            //PubDate under <feed> level is not supported
            //entry.setPublished(course.getCreatedDate());
            SeamTextParser stp = new SeamTextParser(new SeamTextLexer(new StringReader(course.getDescription())));
            try
            {
                stp.startRule();
            }
            catch (RecognitionException | TokenStreamException e)
            {
                log.info("PARSER EXCEPTION : " + e.getMessage());
            }
            entry.setSummary(stp.toString());
            entry.setTitle(course.getName());
            entry.setUid(UUID.randomUUID().toString());
            entry.setUpdated(course.getCreatedDate());
            entries.add(entry);
        }
        Collections.sort(entries, new Comparator<Entry>() {

            @Override
            public int compare(Entry o1, Entry o2)
            {
                return o2.getUpdated().compareTo(o1.getUpdated());
            }
        });
        feed.setEntries(entries);
        feed.setLink("http://jedlab.ir/rss");
        feed.setSubtitle(StatusMessage.getBundleMessage("Java_Video", "Java_Video"));
        feed.setTitle(StatusMessage.getBundleMessage("JEDLab", ""));
        feed.setUid(UUID.randomUUID().toString());
        feed.setUpdated(new Date());
    }

}
