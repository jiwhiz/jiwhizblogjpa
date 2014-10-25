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
import static com.jiwhiz.rest.admin.AdminApiUrls.URL_ADMIN;
import static com.jiwhiz.rest.admin.AdminApiUrls.URL_ADMIN_BLOGS;
import static com.jiwhiz.rest.admin.AdminApiUrls.URL_ADMIN_COMMENTS;
import static com.jiwhiz.rest.admin.AdminApiUrls.URL_ADMIN_USERS;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.hateoas.MediaTypes;

import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.account.UserAccountRepository;
import com.jiwhiz.domain.post.CommentPostRepository;
import com.jiwhiz.rest.AbstractRestControllerTest;

/**
 * @author Yuan Ji
 */
public class AdminAccountRestControllerTest extends AbstractRestControllerTest {
    @Inject
    UserAccountRepository userAccountRepositoryMock;
    
    @Inject
    CommentPostRepository commentPostRepositoryMock;
    
    @Before
    public void setup() {
        Mockito.reset(userAccountRepositoryMock);
        Mockito.reset(commentPostRepositoryMock);
        super.setup();
    }

    @Test
    public void getAdminAccount_ShouldReturnCurrentAdminAccount() throws Exception {
        UserAccount adminUser = getTestLoggedInUserWithAdminRole();
        
        when(userAccountServiceMock.getCurrentUser()).thenReturn(adminUser);
        
        mockMvc.perform(get(API_ROOT + URL_ADMIN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._links.self.href", endsWith(URL_ADMIN)))
                .andExpect(jsonPath("$._links.users.templated", is(true)))
                .andExpect(jsonPath("$._links.users.href", endsWith(URL_ADMIN_USERS+"{?page,size,sort}")))
                .andExpect(jsonPath("$._links.comments.templated", is(true)))
                .andExpect(jsonPath("$._links.comments.href", endsWith(URL_ADMIN_COMMENTS+"{?page,size,sort}")))
                .andExpect(jsonPath("$._links.blogs.templated", is(true)))
                .andExpect(jsonPath("$._links.blogs.href", endsWith(URL_ADMIN_BLOGS+"{?page,size,sort}")))
                ;
    }

    @Test
    public void getAdminAccount_ShouldReturn403IfLoggedInUserIsNotAdmin() throws Exception {
        UserAccount user = getTestLoggedInUser();
        
        when(userAccountServiceMock.getCurrentUser()).thenReturn(user);
        
        mockMvc.perform(get(API_ROOT + URL_ADMIN))
                .andExpect(status().isForbidden())
                ;
    } 
}
