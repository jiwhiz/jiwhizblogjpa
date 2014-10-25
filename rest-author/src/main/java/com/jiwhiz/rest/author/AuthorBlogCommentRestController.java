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

import static com.jiwhiz.rest.author.AuthorApiUrls.URL_AUTHOR_BLOGS_BLOG_COMMENTS;
import static com.jiwhiz.rest.author.AuthorApiUrls.URL_AUTHOR_BLOGS_BLOG_COMMENTS_COMMENT;

import java.util.Map;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jiwhiz.domain.account.UserAccountService;
import com.jiwhiz.domain.post.BlogPost;
import com.jiwhiz.domain.post.BlogPostRepository;
import com.jiwhiz.domain.post.CommentPost;
import com.jiwhiz.domain.post.CommentPostRepository;
import com.jiwhiz.domain.post.CommentStatusType;
import com.jiwhiz.rest.ResourceNotFoundException;
import com.jiwhiz.rest.UtilConstants;

/**
 * @author Yuan Ji
 */
@RestController
@Slf4j
public class AuthorBlogCommentRestController extends AbstractAuthorRestController {
 
    private final CommentPostRepository commentPostRepository;
    private final AuthorBlogCommentResourceAssembler authorBlogCommentResourceAssembler;
    
    @Inject
    public AuthorBlogCommentRestController(
            UserAccountService userAccountService,
            BlogPostRepository blogPostRepository,
            CommentPostRepository commentPostRepository,
            AuthorBlogCommentResourceAssembler authorBlogCommentResourceAssembler) {
        super(userAccountService, blogPostRepository);
        this.commentPostRepository = commentPostRepository;
        this.authorBlogCommentResourceAssembler = authorBlogCommentResourceAssembler;
    }

    @RequestMapping(method = RequestMethod.GET, value = URL_AUTHOR_BLOGS_BLOG_COMMENTS) 
    @Transactional(readOnly=true)
    public HttpEntity<PagedResources<AuthorBlogCommentResource>> getCommentPostsByBlogPostId(
            @PathVariable("blogId") String blogId,
            @PageableDefault(size = UtilConstants.DEFAULT_RETURN_RECORD_COUNT, page = 0) Pageable pageable,
            PagedResourcesAssembler<CommentPost> assembler) 
            throws ResourceNotFoundException {
        BlogPost blogPost = getBlogByIdAndCheckAuthor(blogId);

        Page<CommentPost> commentPosts = commentPostRepository.findByBlogPostOrderByCreatedTimeDesc(blogPost, pageable);
        return new ResponseEntity<>(assembler.toResource(commentPosts, authorBlogCommentResourceAssembler), HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = URL_AUTHOR_BLOGS_BLOG_COMMENTS_COMMENT) 
    @Transactional(readOnly=true)
    public HttpEntity<AuthorBlogCommentResource> getCommentPostById(
            @PathVariable("blogId") String blogId, 
            @PathVariable("commentId") String commentId) 
            throws ResourceNotFoundException {
        BlogPost blogPost = getBlogByIdAndCheckAuthor(blogId);
        CommentPost commentPost = getCommentByIdAndCheckBlog(commentId, blogPost);
        return new ResponseEntity<>(authorBlogCommentResourceAssembler.toResource(commentPost), HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.PATCH, value = URL_AUTHOR_BLOGS_BLOG_COMMENTS_COMMENT)
    @Transactional
    public HttpEntity<Void> updateCommentPost(
            @PathVariable("blogId") String blogId, 
            @PathVariable("commentId") String commentId,
            @RequestBody Map<String, String> updateMap) throws ResourceNotFoundException {
        BlogPost blogPost = getBlogByIdAndCheckAuthor(blogId);
        CommentPost commentPost = getCommentByIdAndCheckBlog(commentId, blogPost);
        
        String content = updateMap.get("content");
        if (content != null) {
            commentPost.update(content);
        }
        String statusString = updateMap.get("status");
        if (statusString != null) {
            CommentStatusType status = CommentStatusType.valueOf(statusString);
            if (status != null) {
                commentPost.setStatus(status);
            } else {
                log.info("Invalid Comment Status:"+statusString);
                //TODO throw exception for invalid status ??
                
            }
        }
        commentPostRepository.save(commentPost);
        
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    private CommentPost getCommentByIdAndCheckBlog(String commentId, BlogPost blogPost) throws ResourceNotFoundException {
        CommentPost commentPost = commentPostRepository.findOne(commentId);
        if (commentPost == null) {
            throw new ResourceNotFoundException("No such comment for id "+commentId);
        }

        if (!commentPost.getBlogPost().equals(blogPost)) {
            throw new ResourceNotFoundException(
                    String.format("Comment(%s) is not for Blog(%s).", commentId, blogPost.getId()));
        }
        return commentPost;
    }
}
