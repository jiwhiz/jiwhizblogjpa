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

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.post.BlogPost;
import com.jiwhiz.domain.post.CommentPost;
import com.jiwhiz.domain.post.CommentPostRepository;
import com.jiwhiz.domain.post.CommentPostService;

/**
 * Implementation for CommentPostService.
 * 
 * @author Yuan Ji
 * 
 */
@Slf4j
public class CommentPostServiceImpl implements CommentPostService {
    
    private final CommentPostRepository commentPostRepository;

    @Inject
    public CommentPostServiceImpl(CommentPostRepository commentPostRepository) {
        this.commentPostRepository = commentPostRepository;
    }

    @Override
    public CommentPost postComment(UserAccount user, BlogPost blogPost, String content) {
        CommentPost comment = new CommentPost(user, blogPost, content);
        log.debug(String.format("Add comment to blog ('%s'), by %s.", blogPost.getTitle(), user.getDisplayName()));

        if (user.isTrustedAccount()) {
            comment.approve();
        }

        return commentPostRepository.save(comment);
    }

}
