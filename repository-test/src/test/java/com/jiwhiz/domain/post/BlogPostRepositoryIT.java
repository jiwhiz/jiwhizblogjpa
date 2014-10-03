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

import java.util.List;
import java.util.UUID;

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
import com.jiwhiz.domain.account.UserRoleType;

/**
 * Integration Test for BlogPostRepository.
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
public class BlogPostRepositoryIT {
    @Autowired
    UserAccountRepository accountRepository;
    
    @Autowired
    BlogPostRepository blogPostRepository;
    
    
    @Test
    public void blogPostCRUD_ShouldWork() {
        // create account
        UserAccount account = new UserAccount();
        account.setId(UUID.randomUUID().toString());
        account.getRoles().add(UserRoleType.ROLE_ADMIN);
        account.getRoles().add(UserRoleType.ROLE_AUTHOR);
        account.setDisplayName("John");
        accountRepository.save(account);
        
        //create blog post
        BlogPost blog = new BlogPost(account, "Test Blog", "This is a test blog content", "jiwhizblog, spring, REST");
        blog.setId(UUID.randomUUID().toString());
        BlogPost savedBlog = blogPostRepository.save(blog);
        String id = savedBlog.getId();
        assertTrue(blogPostRepository.exists(id));

        // read
        BlogPost blogInDb = blogPostRepository.findOne(id);
        assertEquals("Test Blog", blogInDb.getTitle());

        // update
        savedBlog.setTitle("Changed Title");
        blogPostRepository.save(blog);
        blogInDb = blogPostRepository.findOne(id);
        assertEquals("Changed Title", blogInDb.getTitle());

        // delete
        blogPostRepository.delete(blogInDb);
        blogInDb = blogPostRepository.findOne(id);
        assertNull(blogInDb);
        assertFalse(blogPostRepository.exists(id));
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void findByPublishedIsTrue_ShouldReturnPublishedBlogs() {
        List<BlogPost> blogs = blogPostRepository.findByPublishedIsTrue();
        assertEquals(1, blogs.size());
        assertEquals("Blog 1", blogs.get(0).getTitle());
        
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void countByPublishedIsTrue_ShouldReturnPublishedBlogCount() {
        Long count = blogPostRepository.countByPublishedIsTrue();
        assertEquals(new Long(1l), count);
    }
}
