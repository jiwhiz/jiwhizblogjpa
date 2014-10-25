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
package com.jiwhiz.rest.user;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.springframework.hateoas.Resource;

import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.account.UserSocialConnection;

/**
 * @author Yuan Ji
 */
public class UserAccountResource extends Resource<UserAccount> {
    public static final String LINK_NAME_PROFILE = "profile";
    public static final String LINK_NAME_COMMENTS = "comments";
    public static final String LINK_NAME_COMMENT = "comment";
    
    @Getter @Setter
    private Map<String, UserSocialConnection> socialConnections = new HashMap<String, UserSocialConnection>();
    
    public UserAccountResource(UserAccount account) {
        super(account);
    }

}
