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

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.social.security.SocialUserDetails;

import com.jiwhiz.domain.BaseAuditableEntity;

/**
 * Domain Entity for user account.
 * 
 * @author Yuan Ji
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table( name="USER_ACCOUNT" )
public class UserAccount extends BaseAuditableEntity implements SocialUserDetails {
    
    @ElementCollection(targetClass=UserRoleType.class)
    @Enumerated(EnumType.STRING) 
    @CollectionTable(name="USER_ROLE", joinColumns=@JoinColumn(name="account_id"))
    @Column(name="role")
    private Collection<UserRoleType> roles;
    
    @Column( name="email" )
    private String email;
    
    @Column( name="display_name" )
    private String displayName;
    
    @Column( name="image_url" )
    private String imageUrl;
    
    @Column( name="web_site" )
    private String webSite;
    
    @Column( name="account_locked" )
    private Boolean accountLocked = false;
    
    @Column( name="trusted_account" )
    private Boolean trustedAccount = false;

    public Collection<UserRoleType> getRoles() {
        return roles;
    }

    public void setRoles(Collection<UserRoleType> roles) {
        this.roles = roles;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getWebSite() {
        return webSite;
    }

    public void setWebSite(String webSite) {
        this.webSite = webSite;
    }

    public Boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(Boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public Boolean isTrustedAccount() {
        return trustedAccount;
    }

    public void setTrustedAccount(Boolean trustedAccount) {
        this.trustedAccount = trustedAccount;
    }

    public UserAccount() {
        this.roles = new ArrayList<>();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        // No password stored
        return null;
    }

    @Override
    public String getUsername() {
        return getUserId();
    }
    
    public boolean isAuthor(){
        for (UserRoleType role : getRoles()) {
            if (role == UserRoleType.ROLE_AUTHOR){
                return true;
            }
        }
        return false;
    }

    public boolean isAdmin(){
        for (UserRoleType role : getRoles()) {
            if (role == UserRoleType.ROLE_ADMIN){
                return true;
            }
        }        
        return false;
    }

    public void updateProfile(String displayName, String email, String webSite){
        setDisplayName(displayName);
        setEmail(email);
        setWebSite(webSite);
    }
    
    @Override
    public String toString() {
        String str = String.format("UserAccount{userId:'%s'; displayName:'%s';roles:[", getUserId(), getDisplayName());
        for (UserRoleType role : getRoles()) {
            str += role.toString() + ",";
        }
        return str + "]}";
    }

    @Override
    public String getUserId() {
        return getId();
    }

}
