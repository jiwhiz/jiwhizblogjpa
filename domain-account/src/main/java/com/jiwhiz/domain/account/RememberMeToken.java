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
package com.jiwhiz.domain.account;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import com.jiwhiz.domain.BaseEntity;

/**
 * Entity for RememberMe token.
 * 
 * @author Yuan Ji
 *
 */
@SuppressWarnings("serial")
@Entity
@Table( name="REMEMBERME_TOKEN" )
public class RememberMeToken extends BaseEntity {

    @Column( name="username" )
    private String username;
    
    @Column( name="series" )
    private String series;
    
    @Column( name="token_value" )
    private String tokenValue;
    
    @Column( name="date" )
    private Date date;
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public RememberMeToken() {
    }
    
    public RememberMeToken(PersistentRememberMeToken token) {
        this.series = token.getSeries();
        this.username = token.getUsername();
        this.tokenValue = token.getTokenValue();
        this.date = token.getDate();
    }
    
    @Override
    public String toString() {
        return String.format("RememberMeToken{username:'%s'; tokenValue:'%s'}", getUsername(), getTokenValue());
    }

}
