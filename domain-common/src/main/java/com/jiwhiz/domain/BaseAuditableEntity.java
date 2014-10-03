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
package com.jiwhiz.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Super class for auditable entity classes.
 * 
 * @author Yuan Ji
 */
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
@SuppressWarnings("serial")
public abstract class BaseAuditableEntity extends BaseEntity {

    @CreatedBy
    @Column( name="created_by" )
    private String createdBy;
    
    @CreatedDate
    @Column( name="created_time" )
    private Date createdTime;
    
    @LastModifiedBy
    @Column( name="last_modified_by" )
    private String lastModifiedBy;
    
    @LastModifiedDate
    @Column( name="last_modified_time" )
    private Date lastModifiedTime;

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }
    
}
