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

import static com.jiwhiz.rest.user.UserApiUrls.URL_USER_BLOGS_BLOG_COMMENTS;

import java.net.URI;

import javax.inject.Inject;

import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.account.UserAccountService;
import com.jiwhiz.domain.post.BlogPost;
import com.jiwhiz.domain.post.BlogPostRepository;
import com.jiwhiz.domain.post.CommentPost;
import com.jiwhiz.domain.post.CommentPostService;
import com.jiwhiz.mail.CommentNotificationSender;
import com.jiwhiz.rest.ResourceNotFoundException;

/**
 * @author Yuan Ji
 */
@RestController
public class PostCommentRestController extends AbstractUserRestController {

    private final BlogPostRepository blogPostRepository;
    private final CommentPostService commentPostService;
    private final UserCommentResourceAssembler userCommentResourceAssembler;
    private final CommentNotificationSender commentNotificationSender;

    @Inject
    public PostCommentRestController(
            UserAccountService userAccountService, 
            BlogPostRepository blogPostRepository,
            CommentPostService commentPostService,
            UserCommentResourceAssembler userCommentResourceAssembler,
            CommentNotificationSender commentNotificationSender) {
        super(userAccountService);
        this.blogPostRepository = blogPostRepository;
        this.commentPostService = commentPostService;
        this.userCommentResourceAssembler = userCommentResourceAssembler;
        this.commentNotificationSender = commentNotificationSender;
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_USER_BLOGS_BLOG_COMMENTS, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Void> postComment(
            @PathVariable("blogId") String blogId,
            @RequestBody CommentForm newComment) throws ResourceNotFoundException {
        UserAccount currentUser = getCurrentAuthenticatedUser();
        BlogPost blogPost = this.blogPostRepository.findOne(blogId);
        if (blogPost == null || !blogPost.isPublished()) {
            throw new ResourceNotFoundException("No published blog post with the id: " + blogId);
        }
        
        CommentPost commentPost = commentPostService.postComment(currentUser, blogPost, newComment.getContent());
        
        //send email to author if someone else posted a comment to blog.
        if (this.commentNotificationSender != null && !blogPost.getAuthor().equals(currentUser)) {
            this.commentNotificationSender.send(blogPost.getAuthor(), currentUser, commentPost, blogPost);
        }

        UserCommentResource resource = userCommentResourceAssembler.toResource(commentPost);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(resource.getLink(Link.REL_SELF).getHref()));
        
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

}
