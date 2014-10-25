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
package com.jiwhiz.rest;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.jiwhiz.config.TestRestServiceWebConfig;
import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.account.UserAccountService;
import com.jiwhiz.domain.account.UserRoleType;
import com.jiwhiz.domain.post.BlogPost;
import com.jiwhiz.domain.post.CommentPost;
import com.jiwhiz.domain.post.CommentStatusType;

/**
 * @author Yuan Ji
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestRestServiceWebConfig.class })
@WebAppConfiguration
public abstract class AbstractRestControllerTest {
    protected MockMvc mockMvc;
    
    @Inject
    protected WebApplicationContext webApplicationContext;

    @Inject
    protected UserAccountService userAccountServiceMock;

    @Inject
    public void setup() {
        Mockito.reset(userAccountServiceMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    protected static final String USER_ID = "user123";
    protected static final String USER_USERNAME = "user123";
    protected static final String USER_DISPLAY_NAME = "John Doe";
    
    protected static final String ADMIN_USER_ID = "user124";
    protected static final String ADMIN_USER_USERNAME = "user124";
    protected static final String AUTHOR_USER_ID = "user125";
    protected static final String AUTHOR_USER_USERNAME = "user125";
    
    protected UserAccount getTestLoggedInUser() {
        UserAccount user = new UserAccount();
        user.setId(USER_ID);
        user.getRoles().add(UserRoleType.ROLE_USER);
        user.setDisplayName(USER_DISPLAY_NAME);
        return user;
    }

    protected UserAccount getTestLoggedInUserWithAdminRole() {
        UserAccount user = new UserAccount();
        user.setId(ADMIN_USER_ID);
        user.getRoles().add(UserRoleType.ROLE_USER);
        user.getRoles().add(UserRoleType.ROLE_ADMIN);
        user.setDisplayName(USER_DISPLAY_NAME);
        return user;
    }

    protected UserAccount getTestLoggedInUserWithAuthorRole() {
        UserAccount user = new UserAccount();
        user.setId(AUTHOR_USER_ID);
        user.getRoles().add(UserRoleType.ROLE_USER);
        user.getRoles().add(UserRoleType.ROLE_AUTHOR);
        user.setDisplayName(USER_DISPLAY_NAME);
        return user;
    }

    protected static final String USERS_1_USER_ID = "user001";
    protected static final String USERS_1_DISPLAY_NAME = "John Doe";
    protected static final String USERS_2_USER_ID = "user002";
    protected static final String USERS_2_DISPLAY_NAME = "Jane Doe";
    
    protected UserAccount getAuthor1() {
        UserAccount user = new UserAccount();
        user.setId(USERS_1_USER_ID);
        user.getRoles().add(UserRoleType.ROLE_USER);
        user.getRoles().add(UserRoleType.ROLE_AUTHOR);
        user.setDisplayName(USERS_1_DISPLAY_NAME);
        return user;
    }
    
    protected UserAccount getAuthor2() {
        UserAccount user = new UserAccount();
        user.setId(USERS_2_USER_ID);
        user.getRoles().add(UserRoleType.ROLE_USER);
        user.getRoles().add(UserRoleType.ROLE_AUTHOR);
        user.setDisplayName(USERS_2_DISPLAY_NAME);
        return user;
    }

    protected List<UserAccount> getTestUserAccountList() {
        return Arrays.asList(getAuthor1(), getAuthor2());
    }

    protected static final String BLOG_ID = "blog123";
    protected static final String BLOG_TITLE = "My First Post";
    
    protected BlogPost getTestSinglePublishedBlogPost() {
        BlogPost blog = new BlogPost();
        blog.setId(BLOG_ID);
        blog.setAuthor(getTestLoggedInUserWithAuthorRole());
        blog.setTitle(BLOG_TITLE);
        blog.setPublished(true);
        return blog;
    }
    
    protected static final String BLOGS_1_ID = "blog001";
    protected static final String BLOGS_1_TITLE = "Test Blog One";
    protected static final String BLOGS_2_ID = "blog002";
    protected static final String BLOGS_2_TITLE = "Test Blog Two";
    
    protected BlogPost getBlog1() {
        BlogPost blog1 = new BlogPost();
        blog1.setId(BLOGS_1_ID);
        blog1.setTitle(BLOGS_1_TITLE);
        blog1.setAuthor(getAuthor1());
        blog1.setPublished(true);
        return blog1;
    }
    
    protected BlogPost getBlog2() {
        BlogPost blog2 = new BlogPost();
        blog2.setId(BLOGS_2_ID);
        blog2.setTitle(BLOGS_2_TITLE);
        blog2.setAuthor(getAuthor2());
        blog2.setPublished(true);
        return blog2;
    }
    
    protected List<BlogPost> getTestPublishedBlogPostList() {
        return Arrays.asList(getBlog1(), getBlog2());
    }

    protected static final String COMMENT_ID = "comm123";
    protected static final String COMMENT_CONTENT = "Test comment...";

    protected CommentPost getTestApprovedCommentPost() {
        CommentPost comment = new CommentPost();
        comment.setId(COMMENT_ID);
        comment.setAuthor(getTestLoggedInUserWithAuthorRole());
        comment.setBlogPost(getTestSinglePublishedBlogPost());
        comment.setContent(COMMENT_CONTENT);
        comment.setStatus(CommentStatusType.APPROVED);
        return comment;
    }
    
    protected static final String COMMENTS_1_ID = "comm001";
    protected static final String COMMENTS_1_CONTENT = "My comment...";
    protected static final String COMMENTS_2_ID = "comm002";
    protected static final String COMMENTS_2_CONTENT = "Another comment...";
    
    protected List<CommentPost> getTestApprovedCommentPostList() {
        CommentPost comment1 = new CommentPost();
        comment1.setId(COMMENTS_1_ID);
        comment1.setAuthor(getAuthor1());
        comment1.setBlogPost(getTestSinglePublishedBlogPost());
        comment1.setContent(COMMENTS_1_CONTENT);
        comment1.setStatus(CommentStatusType.APPROVED);
        
        CommentPost comment2 = new CommentPost();
        comment2.setId(COMMENTS_2_ID);
        comment2.setAuthor(getAuthor2());
        comment2.setBlogPost(getTestSinglePublishedBlogPost());
        comment2.setContent(COMMENTS_2_CONTENT);
        comment2.setStatus(CommentStatusType.APPROVED);
        
        return Arrays.asList(comment1, comment2);
    }

    protected List<CommentPost> getTestUserCommentPostList() {
        CommentPost comment1 = new CommentPost();
        comment1.setId(COMMENTS_1_ID);
        comment1.setAuthor(getTestLoggedInUserWithAuthorRole());
        comment1.setBlogPost(getTestSinglePublishedBlogPost());
        comment1.setContent(COMMENTS_1_CONTENT);
        comment1.setStatus(CommentStatusType.APPROVED);
        
        CommentPost comment2 = new CommentPost();
        comment2.setId(COMMENTS_2_ID);
        comment2.setAuthor(getTestLoggedInUserWithAuthorRole());
        comment2.setBlogPost(getTestSinglePublishedBlogPost());
        comment2.setContent(COMMENTS_2_CONTENT);
        comment2.setStatus(CommentStatusType.PENDING);
        
        return Arrays.asList(comment1, comment2);
    }
}
