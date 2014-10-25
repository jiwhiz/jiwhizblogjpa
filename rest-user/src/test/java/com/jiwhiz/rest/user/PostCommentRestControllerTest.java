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

import static com.jiwhiz.rest.UtilConstants.API_ROOT;
import static com.jiwhiz.rest.user.UserApiUrls.URL_USER_BLOGS_BLOG_COMMENTS;
import static com.jiwhiz.rest.user.UserApiUrls.URL_USER_COMMENTS;
import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.jiwhiz.config.TestUtils;
import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.post.BlogPost;
import com.jiwhiz.domain.post.BlogPostRepository;
import com.jiwhiz.domain.post.CommentPost;
import com.jiwhiz.domain.post.CommentPostService;
import com.jiwhiz.rest.AbstractRestControllerTest;

/**
 * @author Yuan Ji
 *
 */
public class PostCommentRestControllerTest extends AbstractRestControllerTest {
    
    @Inject
    BlogPostRepository blogPostRepositoryMock;
    
    @Inject
    CommentPostService commentPostServiceMock;
    
    @Before
    public void setup() {
        Mockito.reset(blogPostRepositoryMock);
        Mockito.reset(commentPostServiceMock);
        super.setup();
    }
    
    @Test
    public void postComment_ShouldAddNewComment() throws Exception {
        final String NEW_COMMENT_TXT = "New comment test.";
        UserAccount user = getTestLoggedInUserWithAuthorRole();
        BlogPost blog = getTestSinglePublishedBlogPost();
        CommentPost comment = new CommentPost(user, blog, NEW_COMMENT_TXT);
        comment.setId("newCommId");
        
        when(userAccountServiceMock.getCurrentUser()).thenReturn(user);
        when(blogPostRepositoryMock.findOne(eq(BLOG_ID))).thenReturn(blog);
        when(commentPostServiceMock.postComment(eq(user), eq(blog), anyString())).thenReturn(comment);
        
        CommentForm newComment = new CommentForm();
        mockMvc.perform
                (request(HttpMethod.POST, API_ROOT + URL_USER_BLOGS_BLOG_COMMENTS, BLOG_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(newComment))
                )
                .andExpect(status().isCreated())
                .andExpect(
                    header().string("Location", 
                        endsWith(API_ROOT + URL_USER_COMMENTS + "/newCommId")
                    )
                )
                .andExpect(content().string(""))
                ;
    } 
}
