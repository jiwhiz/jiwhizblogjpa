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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.beans.BeanUtils;

import com.jiwhiz.domain.account.UserAccount;

/**
 * @author Yuan Ji
 */
@ToString
public class ProfileForm {
    
    @Getter @Setter
    private String displayName;
    
    @Getter @Setter
    private String email;
    
    @Getter @Setter
    private String imageUrl;
    
    @Getter @Setter
    private String webSite;

    public ProfileForm() {
        
    }
    
    public ProfileForm(UserAccount account) {
        BeanUtils.copyProperties(account, this);
    }
}
