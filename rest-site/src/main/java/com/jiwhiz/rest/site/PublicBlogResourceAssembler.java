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
package com.jiwhiz.rest.site;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.inject.Inject;

import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import com.jiwhiz.domain.post.BlogPost;
import com.jiwhiz.domain.post.CommentPost;
import com.jiwhiz.domain.post.CommentPostRepository;
import com.jiwhiz.domain.post.CommentStatusType;

/**
 * @author Yuan Ji
 */
@Component
public class PublicBlogResourceAssembler implements ResourceAssembler<BlogPost, PublicBlogResource> {
    private final CommentPostRepository commentPostRepository;
    private final PagedResourcesAssembler<CommentPost> assembler;

    @Inject
    public PublicBlogResourceAssembler(CommentPostRepository commentPostRepository,
            PagedResourcesAssembler<CommentPost> assembler) {
        this.commentPostRepository = commentPostRepository;
        this.assembler = assembler;
    }

    @Override
    public PublicBlogResource toResource(BlogPost blogPost) {
        PublicBlogResource resource = new PublicBlogResource(blogPost);
        resource.setApprovedCommentCount(commentPostRepository.countByBlogPostAndStatus(blogPost,
                CommentStatusType.APPROVED));
        
        resource.add(linkTo(methodOn(PublicBlogRestController.class).getPublicBlogPostById(blogPost.getId()))
                .withSelfRel());
        
        Link commentsLink = linkTo(
                methodOn(PublicBlogRestController.class).getBlogApprovedCommentPosts(blogPost.getId(), null, null))
                .withRel(PublicBlogResource.LINK_NAME_COMMENTS);
        resource.add(assembler.appendPaginationParameterTemplates(commentsLink));
                        
        resource.add(linkTo(methodOn(WebsiteRestController.class).getUserProfile(blogPost.getAuthor().getId()))
                .withRel(PublicBlogResource.LINK_NAME_AUTHOR));
        return resource;
    }

}
