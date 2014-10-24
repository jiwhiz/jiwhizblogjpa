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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jiwhiz.domain.BaseAuditableEntity;
import com.jiwhiz.domain.account.UserAccount;

/**
 * Abstract super class for domain entities related to post, like BlogPost, CommentPost.
 * 
 * @author Yuan Ji
 *
 */
@ToString(callSuper=true)
@MappedSuperclass
@SuppressWarnings("serial")
public abstract class AbstractPost extends BaseAuditableEntity {

    @Getter @Setter
    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="author_id")
    private UserAccount author;

    @Getter @Setter
    @Column( name="content" )
    private String content;

    public AbstractPost() {
    }
    
    public AbstractPost(UserAccount author, String content) {
        this.author = author;
        this.content = content;
    }

}
