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
package com.jiwhiz.domain.post.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.post.BlogPost;
import com.jiwhiz.domain.post.CommentPost;
import com.jiwhiz.domain.post.CommentPostRepository;
import com.jiwhiz.domain.post.CommentStatusType;

/**
 * @author Yuan Ji
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CommentPostServiceImplTest {
    @Mock
    CommentPostRepository commentPostRepositoryMock;
        
    CommentPostServiceImpl serviceToTest;
    
    @Before
    public void setup() {
        serviceToTest = new CommentPostServiceImpl(commentPostRepositoryMock);
    }
    
    @Test
    public void postComment_ShouldCreateNewComment() {
        UserAccount testUser = new UserAccount();
        testUser.setTrustedAccount(false);
        BlogPost testBlog = new BlogPost();
        
        when(commentPostRepositoryMock.save(isA(CommentPost.class))).thenAnswer( new Answer<CommentPost>() {
            @Override
            public CommentPost answer(InvocationOnMock invocation) throws Throwable {
                CommentPost comment = (CommentPost)invocation.getArguments()[0];
                return comment;
            }
        });
        
        CommentPost comment = serviceToTest.postComment(testUser,  testBlog,  "Test test test...");
        assertEquals("Status should be PENDING if user is not trusted.", CommentStatusType.PENDING, comment.getStatus());
    }
    
    @Test
    public void postComment_ShouldApproveComment_IfUserIsTrusted() {
        UserAccount testUser = new UserAccount();
        testUser.setTrustedAccount(true);
        BlogPost testBlog = new BlogPost();
        
        when(commentPostRepositoryMock.save(isA(CommentPost.class))).thenAnswer( new Answer<CommentPost>() {
            @Override
            public CommentPost answer(InvocationOnMock invocation) throws Throwable {
                CommentPost comment = (CommentPost)invocation.getArguments()[0];
                return comment;
            }
        });
        
        CommentPost comment = serviceToTest.postComment(testUser,  testBlog,  "Test test test...");
        assertEquals("Status should be APPROVED if user is trusted.", CommentStatusType.APPROVED, comment.getStatus());
    }
    
}
