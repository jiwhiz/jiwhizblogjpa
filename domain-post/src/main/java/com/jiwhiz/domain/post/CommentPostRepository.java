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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jiwhiz.domain.account.UserAccount;

/**
 * JPA Repository for CommentPost entity.
 * 
 * @author Yuan Ji
 *
 */
public interface CommentPostRepository extends JpaRepository<CommentPost, String> {
    
    Page<CommentPost> findByBlogPost(BlogPost blogPost, Pageable pageable);
    
    Page<CommentPost> findByStatusOrderByCreatedTimeDesc(CommentStatusType status, Pageable pageable);
    
    int countByBlogPostAndStatus(BlogPost blogPost, CommentStatusType status);
    
    int countByBlogPost(BlogPost blogPost);
    
    Page<CommentPost> findByBlogPostOrderByCreatedTimeDesc(BlogPost blogPost, Pageable pageable);

    Page<CommentPost> findByAuthorAndStatusOrderByCreatedTimeDesc(UserAccount author, CommentStatusType status, Pageable pageable);
    
    Page<CommentPost> findByAuthorOrderByCreatedTimeDesc(UserAccount author, Pageable pageable);
    
    Page<CommentPost> findByBlogPostAndStatusOrderByCreatedTimeAsc(BlogPost blogPost, CommentStatusType status, Pageable pageable);
}
