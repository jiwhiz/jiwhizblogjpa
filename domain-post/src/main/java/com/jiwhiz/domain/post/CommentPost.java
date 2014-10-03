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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jiwhiz.domain.account.UserAccount;

/**
 * Domain Entity for blog comment.
 * 
 * @author Yuan Ji
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table( name="COMMENT_POST" )
public class CommentPost extends AbstractPost {
    
    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="blog_post_id")
    private BlogPost blogPost;
    
    @Enumerated(EnumType.STRING)
    @Column( name="status" )
    private CommentStatusType status;
    
    public BlogPost getBlogPost() {
        return blogPost;
    }

    public void setBlogPost(BlogPost blogPost) {
        this.blogPost = blogPost;
    }

    public CommentStatusType getStatus() {
        return status;
    }

    public void setStatus(CommentStatusType status) {
        this.status = status;
    }

    public CommentPost() {
    }
    
    public CommentPost(UserAccount author, BlogPost blogPost, String content) {
        super(author, content);
        this.blogPost = blogPost;
        this.status = CommentStatusType.PENDING;
    }

    public CommentPost update(String newContent) {
        setContent(newContent);
        return this;
    }
    
    public CommentPost approve() {
        assert status == CommentStatusType.PENDING;
        this.status = CommentStatusType.APPROVED;
        return this;
    }
    
    public CommentPost disapprove() {
        assert status == CommentStatusType.APPROVED;
        this.status = CommentStatusType.PENDING;
        return this;
    }
    
    public CommentPost markSpam() {
        assert status == CommentStatusType.PENDING;
        this.status = CommentStatusType.SPAM;
        return this;
    }
    
    public CommentPost unmarkSpam() {
        assert status == CommentStatusType.SPAM;
        this.status = CommentStatusType.PENDING;
        return this;
    }
}
