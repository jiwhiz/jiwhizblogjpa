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

import static com.jiwhiz.rest.UtilConstants.API_ROOT;
import static com.jiwhiz.rest.author.AuthorApiUrls.URL_AUTHOR_BLOGS_BLOG;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.inject.Inject;

import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mvc.BasicLinkBuilder;
import org.springframework.stereotype.Component;

import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.post.BlogPost;

/**
 * @author Yuan Ji
 */
@Component
public class AuthorAccountResourceAssembler implements ResourceAssembler<UserAccount, AuthorAccountResource> {
    private final PagedResourcesAssembler<BlogPost> assembler;
    
    @Inject
    public AuthorAccountResourceAssembler(PagedResourcesAssembler<BlogPost> assembler) {
        this.assembler = assembler;
    }

    @Override
    public AuthorAccountResource toResource(UserAccount entity) {
        AuthorAccountResource resource = new AuthorAccountResource(entity);

        resource.add(linkTo(methodOn(AuthorAccountRestController.class).getCurrentAuthorAccount())
                .withSelfRel());
        
        
        Link blogsLink = linkTo(methodOn(AuthorBlogRestController.class).getAuthorBlogPosts(null, null))
                .withRel(AuthorAccountResource.LINK_NAME_BLOGS);
        resource.add(assembler.appendPaginationParameterTemplates(blogsLink));
        
        String baseUri = BasicLinkBuilder.linkToCurrentMapping().toString(); 
        Link blogLink = new Link(
                new UriTemplate(baseUri + API_ROOT + URL_AUTHOR_BLOGS_BLOG), AuthorAccountResource.LINK_NAME_BLOG);
        resource.add(blogLink); // TODO do we need this?
        return resource;

    }

}
