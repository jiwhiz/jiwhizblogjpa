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
package com.jiwhiz.rest.site;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.ResourceSupport;

import com.jiwhiz.domain.account.UserAccount;

/**
 * @author Yuan Ji
 */
public class UserProfileResource extends ResourceSupport {
    public static final String LINK_NAME_COMMENTS = "comments";
    
    @Getter @Setter
    private String userId;
    
    @Getter @Setter
    private String displayName;

    @Getter @Setter
    private String imageUrl;

    @Getter @Setter
    private String webSite;

    @Getter @Setter
    private boolean admin;

    @Getter @Setter
    private boolean author;
    
    @Getter @Setter
    private boolean accountLocked;
    
    @Getter @Setter
    private boolean trustedAccount;

    public UserProfileResource() {
    }
    
    public UserProfileResource(UserAccount account) {
        BeanUtils.copyProperties(account, this);
    }

}
