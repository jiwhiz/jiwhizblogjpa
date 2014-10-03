/* 
 * Copyright 2013-2014 JIWHIZ Consulting Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiwhiz.domain.post;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jiwhiz.domain.account.UserAccount;

/**
 * Domain Entity for blog post.
 * 
 * @author Yuan Ji
 * 
 */
@Entity
@Table( name="BLOG_POST" )
@SuppressWarnings("serial")
public class BlogPost extends AbstractPost {
    
    @Column( name="title" )
    private String title; 

    @Column( name="published" )
    private Boolean published;
    
    @Column( name="published_time" )
    private Date publishedTime;
    
    @Column( name="tag_string" )
    private String tagString;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean isPublished() {
        return published;
    }
    
    public void setPublished(Boolean published){
        this.published = published;
    }

    public Date getPublishedTime() {
        return publishedTime;
    }

    public void setPublishedTime(Date publishedTime) {
        this.publishedTime = publishedTime;
    }

    public String getTagString() {
        return tagString;
    }

    public void setTagString(String tagString) {
        this.tagString = tagString;
    }

    public BlogPost() {
    }
    
    public BlogPost(UserAccount author, String title, String content, String tagString) {
        super(author, content);
        this.title = title;
        parseAndSetTagString(tagString);
    }

    /**
     * Parse user input tag string and trim the key words, remove duplicate and store as sorted tags.
     * 
     * @param tagString
     */
    public void parseAndSetTagString(String tagString) {
        Set<String> tagSet = new TreeSet<String>();
        
        for (String tag : tagString.split(",")) {
            String newTag = tag.trim();
            if (newTag.length() > 0) {
                tagSet.add(newTag);
            }
        }
        
        Iterator<String> iter = tagSet.iterator();
        StringBuilder sb = new StringBuilder();
        while (iter.hasNext()) {
            sb.append(iter.next());
            if (iter.hasNext()) {
                sb.append(",");
            }
        }
        this.tagString = sb.toString();
    }

    @JsonIgnore
    public String[] getTags() {
        return tagString.split(",");
    }
    
    public String toString() {
        return String.format("BlogPost {title='%s'}", getTitle());
    }
}
