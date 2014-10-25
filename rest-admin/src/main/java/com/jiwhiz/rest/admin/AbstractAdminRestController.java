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
package com.jiwhiz.rest.admin;

import static com.jiwhiz.rest.UtilConstants.API_ROOT;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.account.UserAccountService;

/**
 * Super class for all admin related rest controller classes.
 * 
 * @author Yuan Ji
 */
@Slf4j
@RequestMapping( value = API_ROOT, produces = "application/hal+json" )
public abstract class AbstractAdminRestController {
    
    protected final UserAccountService userAccountService;
    
    @Inject
    public AbstractAdminRestController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }
    
    protected UserAccount getCurrentAuthenticatedAdmin() {
        UserAccount currentUser = this.userAccountService.getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            //ERROR! Should not happen.
            log.error("Fatal Error! Unauthorized data access!");
            throw new AccessDeniedException("User not logged in or not ADMIN.");
        }
        return currentUser;
    }

}
