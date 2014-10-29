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

import static com.jiwhiz.rest.user.UserApiUrls.URL_USER_COMMENTS;
import static com.jiwhiz.rest.user.UserApiUrls.URL_USER_COMMENTS_COMMENT;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.account.UserAccountService;
import com.jiwhiz.domain.post.CommentPost;
import com.jiwhiz.domain.post.CommentPostRepository;
import com.jiwhiz.rest.ResourceNotFoundException;
import com.jiwhiz.rest.UtilConstants;
import com.wordnik.swagger.annotations.Api;


/**
 * @author Yuan Ji
 */
@RestController
@Api(value="User Comment", 
     description="User comment management", position = 11)
public class UserCommentRestController extends AbstractUserRestController {
    
    private final CommentPostRepository commentPostRepository;
    private final UserCommentResourceAssembler userCommentResourceAssembler;

    @Inject
    public UserCommentRestController(
            UserAccountService userAccountService, 
            CommentPostRepository commentPostRepository,
            UserCommentResourceAssembler userCommentResourceAssembler) {
        super(userAccountService);
        this.commentPostRepository = commentPostRepository;
        this.userCommentResourceAssembler = userCommentResourceAssembler;
    }

    /**
     * Returns current user comments with pagination.
     * 
     * @param pageable
     * @param assembler
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = URL_USER_COMMENTS)
    @Transactional(readOnly=true)
    public HttpEntity<PagedResources<UserCommentResource>> getCurrentUserComments(
            @PageableDefault(size = UtilConstants.DEFAULT_RETURN_RECORD_COUNT, page = 0) Pageable pageable,
            PagedResourcesAssembler<CommentPost> assembler) {
        UserAccount currentUser = getCurrentAuthenticatedUser();
        Page<CommentPost> comments = 
                commentPostRepository.findByAuthorOrderByCreatedTimeDesc(currentUser, pageable);
        
        return new ResponseEntity<>(assembler.toResource(comments, userCommentResourceAssembler), HttpStatus.OK);
    }
    
    /**
     * Returns comment post by id posted by current user.
     * 
     * @param commentId
     * @return
     * @throws ResourceNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET, value = URL_USER_COMMENTS_COMMENT) 
    @Transactional(readOnly=true)
    public HttpEntity<UserCommentResource> getCommentPostById(
            @PathVariable("commentId") String commentId) 
            throws ResourceNotFoundException {
        CommentPost commentPost = getCommentPostByIdAndCheckAuthor(commentId);
        return new ResponseEntity<>(userCommentResourceAssembler.toResource(commentPost), HttpStatus.OK);
    }

    /**
     * Updates comment content by current user.
     * 
     * @param commentId
     * @param updateMap
     * @return
     */
    @RequestMapping(method = RequestMethod.PATCH, value = URL_USER_COMMENTS_COMMENT)
    @Transactional
    public HttpEntity<Void> updateComment(
            @PathVariable("commentId") String commentId, 
            @RequestBody Map<String, String> updateMap) {
        CommentPost comment = getCommentPostByIdAndCheckAuthor(commentId);
        String content = updateMap.get("content");
        if (content != null) {
            comment.update(content);
            commentPostRepository.save(comment);
        }
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
    
    private CommentPost getCommentPostByIdAndCheckAuthor(String commentId) {
        UserAccount currentUser = getCurrentAuthenticatedUser();
        CommentPost comment = commentPostRepository.findOne(commentId);
        if (comment == null) {
            throw new ResourceNotFoundException("No comment with the id: " + commentId);
        }
        if (!comment.getAuthor().equals(currentUser)){
            throw new AccessDeniedException("Cannot update the comment, becasue current user is not the author of the comment.");
        }
        return comment;
    }

}
