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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jiwhiz.domain.BaseEntity;

/**
 * Domain Entity for user social connection.
 * 
 * @author Yuan Ji
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table( name="USER_SOCIAL_CXN" )
public class UserSocialConnection extends BaseEntity {
    
    @Column( name="user_id" )
    private String userId;
    
    @Column( name="provider_id" )
    private String providerId;
    
    @Column( name="provider_user_id" )
    private String providerUserId;
    
    @Column( name="rank" )
    private int rank;
    
    @Column( name="display_name" )
    private String displayName;
    
    @Column( name="profile_url" )
    private String profileUrl;
    
    @Column( name="image_url" )
    private String imageUrl;
    
    @JsonIgnore
    @Column( name="access_token" )
    private String accessToken;
    
    @JsonIgnore
    @Column( name="secret" )
    private String secret;
    
    @JsonIgnore
    @Column( name="refresh_token" )
    private String refreshToken;
    
    
    @JsonIgnore
    @Column( name="expire_time" )
    private Long expireTime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

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

    public String toString() {
        return String.format(
                "UserSocialConnection { userId : '%s', providerId : '%s', providerUserId : '%s', displayName : '%s'",
                userId, providerId, providerUserId, displayName);
    }

}
