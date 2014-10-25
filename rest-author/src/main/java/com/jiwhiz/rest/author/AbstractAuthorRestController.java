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

import static com.jiwhiz.rest.UtilConstants.API_ROOT;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.account.UserAccountService;
import com.jiwhiz.domain.post.BlogPost;
import com.jiwhiz.domain.post.BlogPostRepository;
import com.jiwhiz.rest.ResourceNotFoundException;

/**
 * @author Yuan Ji
 */
@RequestMapping( value = API_ROOT, produces = "application/hal+json" )
@Slf4j
public class AbstractAuthorRestController {

    protected final UserAccountService userAccountService;
    protected final BlogPostRepository blogPostRepository;
    
    @Inject
    public AbstractAuthorRestController(
            UserAccountService userAccountService, 
            BlogPostRepository blogPostRepository) {
        this.userAccountService = userAccountService;
        this.blogPostRepository = blogPostRepository;
    }

    protected UserAccount getCurrentAuthenticatedAuthor() {
        UserAccount currentUser = this.userAccountService.getCurrentUser();
        if (currentUser == null || !currentUser.isAuthor()) {
            //ERROR! Should not happen.
            log.error("Fatal Error! Unauthorized data access!");
            throw new AccessDeniedException("User not logged in or not AUTHOR.");
        }
        return currentUser;
    }
    
    protected BlogPost getBlogByIdAndCheckAuthor(String blogId) throws ResourceNotFoundException {
        UserAccount currentUser = getCurrentAuthenticatedAuthor();
        BlogPost blogPost = blogPostRepository.findOne(blogId);
        if (blogPost == null) {
            throw new ResourceNotFoundException("No such blog for id "+blogId);
        }
        if (!blogPost.getAuthor().equals(currentUser)) {
            throw new AccessDeniedException("User cannot access other author's blog.");
        }
        return blogPost;
    }
}
