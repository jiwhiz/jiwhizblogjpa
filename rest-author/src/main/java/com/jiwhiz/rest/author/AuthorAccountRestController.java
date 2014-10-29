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
package com.jiwhiz.rest.author;

import static com.jiwhiz.rest.author.AuthorApiUrls.URL_AUTHOR;

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
 * @author Yuan Ji
 */
@RestController
@Api(value="Author Account", 
     description="Entry point for author account management", position = 20)
public class AuthorAccountRestController extends AbstractAuthorRestController {

    private final AuthorAccountResourceAssembler authorAccountResourceAssembler;
    
    @Inject
    public AuthorAccountRestController(
            UserAccountService userAccountService,
            AuthorAccountResourceAssembler authorAccountResourceAssembler) {
        super(userAccountService, null);
        this.authorAccountResourceAssembler = authorAccountResourceAssembler;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_AUTHOR)
    @Transactional(readOnly=true)
    public HttpEntity<AuthorAccountResource> getCurrentAuthorAccount() {        
        UserAccount currentUser = getCurrentAuthenticatedAuthor();
        return new ResponseEntity<>(authorAccountResourceAssembler.toResource(currentUser), HttpStatus.OK);
    }

}
