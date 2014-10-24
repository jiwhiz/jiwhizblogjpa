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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jiwhiz.domain.BaseEntity;

/**
 * Domain Entity for user social connection.
 * 
 * @author Yuan Ji
 * 
 */
@ToString(callSuper=true)
@SuppressWarnings("serial")
@Entity
@Table( name="USER_SOCIAL_CXN" )
public class UserSocialConnection extends BaseEntity {
    
    @Getter @Setter
    @Column( name="user_id" )
    private String userId;
    
    @Getter @Setter
    @Column( name="provider_id" )
    private String providerId;
    
    @Getter @Setter
    @Column( name="provider_user_id" )
    private String providerUserId;
    
    @Getter @Setter
    @Column( name="rank" )
    private int rank;
    
    @Getter @Setter
    @Column( name="display_name" )
    private String displayName;
    
    @Getter @Setter
    @Column( name="profile_url" )
    private String profileUrl;
    
    @Getter @Setter
    @Column( name="image_url" )
    private String imageUrl;
    
    @Getter @Setter
    @JsonIgnore
    @Column( name="access_token" )
    private String accessToken;
    
    @Getter @Setter
    @JsonIgnore
    @Column( name="secret" )
    private String secret;
    
    @Getter @Setter
    @JsonIgnore
    @Column( name="refresh_token" )
    private String refreshToken;
    
    @Getter @Setter
    @JsonIgnore
    @Column( name="expire_time" )
    private Long expireTime;

    public UserSocialConnection() {
        super();
    }

    public UserSocialConnection(String userId, String providerId, String providerUserId, int rank,
            String displayName, String profileUrl, String imageUrl, String accessToken, String secret,
            String refreshToken, Long expireTime) {
        super();
        this.userId = userId;
        this.providerId = providerId;
        this.providerUserId = providerUserId;
        this.rank = rank;
        this.displayName = displayName;
        this.profileUrl = profileUrl;
        this.imageUrl = imageUrl;
        this.accessToken = accessToken;
        this.secret = secret;
        this.refreshToken = refreshToken;
        this.expireTime = expireTime;
    }
}
