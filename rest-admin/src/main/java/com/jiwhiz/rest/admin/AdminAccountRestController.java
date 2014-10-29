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

import static com.jiwhiz.rest.admin.AdminApiUrls.URL_ADMIN;

import javax.inject.Inject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.account.UserAccountService;
import com.wordnik.swagger.annotations.Api;

/**
 * RESTful API for AdminAccountResource.
 * 
 * @author Yuan Ji
 */
@RestController
@Api(value="Admin Account", 
     description="Entry point for admin management", position = 30)
public class AdminAccountRestController extends AbstractAdminRestController {

    private final AdminAccountResourceAssembler adminAccountResourceAssembler;
    
    @Inject
    public AdminAccountRestController(
            UserAccountService userAccountService,
            AdminAccountResourceAssembler adminAccountResourceAssembler) {
        super(userAccountService);
        this.adminAccountResourceAssembler = adminAccountResourceAssembler;
    }

    /**
     * Get admin info, which contains role, links to other api.
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = URL_ADMIN)
    @Transactional(readOnly=true)
    public HttpEntity<AdminAccountResource> getAdminAccount() {
        UserAccount currentUser = getCurrentAuthenticatedAdmin();
        AdminAccountResource resource = adminAccountResourceAssembler.toResource(currentUser);
        
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

}
