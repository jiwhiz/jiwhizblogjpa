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
package com.jiwhiz.rest.admin;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.inject.Inject;

import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import com.jiwhiz.domain.post.BlogPost;
import com.jiwhiz.domain.post.CommentPost;

/**
 * @author Yuan Ji
 */
@Component
public class BlogResourceAssembler implements ResourceAssembler<BlogPost, BlogResource> {
    private final PagedResourcesAssembler<CommentPost> assembler;
    
    @Inject
    public BlogResourceAssembler(PagedResourcesAssembler<CommentPost> assembler) {
        this.assembler = assembler;
    }
    
    @Override
    public BlogResource toResource(BlogPost blogPost) {
        BlogResource resource = new BlogResource(blogPost);
        

        resource.add(linkTo(methodOn(BlogRestController.class).getBlogPostById(blogPost.getId()))
                .withSelfRel());
        resource.add(linkTo(methodOn(UserRestController.class).getUserAccountByUserId(blogPost.getAuthor().getUserId()))
                .withRel(BlogResource.LINK_NAME_AUTHOR));

        Link commentsLink = linkTo(methodOn(BlogRestController.class)
                .getCommentPostsByBlogPostId(blogPost.getId(), null, null))
                .withRel(BlogResource.LINK_NAME_COMMENTS);
        resource.add(assembler.appendPaginationParameterTemplates(commentsLink));
        return resource;
    }

}
