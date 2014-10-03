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
package com.jiwhiz.domain.post;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jiwhiz.JiwhizBlogRepositoryTestApplication;
import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.account.UserAccountRepository;

/**
 * Integration Test for CommentPostRepository.
 * 
 * @author Yuan Ji
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JiwhizBlogRepositoryTestApplication.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
public class CommentPostRepositoryIT {
    @Autowired
    UserAccountRepository userAccountRepository;
    
    @Autowired
    BlogPostRepository blogPostRepository;
    
    @Autowired
    CommentPostRepository commentPostRepository;
    
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void commentPostCRUD_ShouldWork() {
        // load account
        UserAccount author = userAccountRepository.findOne("user001");
        
        //load blog post
        BlogPost blog = blogPostRepository.findOne("blog001");
        
        //create comment
        CommentPost comment = new CommentPost(author, blog, "Good job!");
        CommentPost savedComment = commentPostRepository.save(comment);
        String id = savedComment.getId();
        assertTrue(commentPostRepository.exists(id));

        // read
        CommentPost commentInDb = commentPostRepository.findOne(id);
        assertEquals("Good job!", commentInDb.getContent());

        // update
        savedComment.setContent("Excellent!!!");
        commentPostRepository.save(savedComment);
        commentInDb = commentPostRepository.findOne(id);
        assertEquals("Excellent!!!", commentInDb.getContent());

        // delete
        commentPostRepository.delete(commentInDb);
        commentInDb = commentPostRepository.findOne(id);
        assertNull(commentInDb);
        assertFalse(commentPostRepository.exists(id));
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void countByBlogPost_ShouldReturnAllCommentCountForBlog() {
        BlogPost blog = blogPostRepository.findOne("blog001");
        int commentCount = commentPostRepository.countByBlogPost(blog);
        assertEquals(2, commentCount);
        
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void countByBlogPostAndStatus_ShouldReturnCommentCountForBlogWithSpecificStatus() {
        BlogPost blog = blogPostRepository.findOne("blog001");
        int commentCount = commentPostRepository.countByBlogPostAndStatus(blog, CommentStatusType.APPROVED);
        assertEquals(1, commentCount);
        commentCount = commentPostRepository.countByBlogPostAndStatus(blog, CommentStatusType.PENDING);
        assertEquals(1, commentCount);
        commentCount = commentPostRepository.countByBlogPostAndStatus(blog, CommentStatusType.SPAM);
        assertEquals(0, commentCount);
        
    }
}
