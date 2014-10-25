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

import static com.jiwhiz.rest.UtilConstants.API_ROOT;
import static com.jiwhiz.rest.site.SiteApiUrls.URL_SITE_BLOGS;
import static com.jiwhiz.rest.site.SiteApiUrls.URL_SITE_BLOGS_BLOG;
import static com.jiwhiz.rest.site.SiteApiUrls.URL_SITE_PROFILES_USER;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import javax.inject.Inject;

import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mvc.BasicLinkBuilder;
import org.springframework.stereotype.Component;

import com.jiwhiz.domain.post.BlogPost;

/**
 * @author Yuan Ji
 */
@Component
public class WebsiteResourceAssembler {
    private final PagedResourcesAssembler<BlogPost> assembler;

    @Inject
    public WebsiteResourceAssembler(PagedResourcesAssembler<BlogPost> assembler) {
        this.assembler = assembler;

    }

    public WebsiteResource toResource() {
        WebsiteResource resource = new WebsiteResource();
        resource.add(linkTo(methodOn(WebsiteRestController.class).getPublicWebsiteResource()).withSelfRel());

        String baseUri = BasicLinkBuilder.linkToCurrentMapping().toString();

        Link blogsLink = new Link(new UriTemplate(baseUri + API_ROOT + URL_SITE_BLOGS),
                WebsiteResource.LINK_NAME_BLOGS);
        resource.add(assembler.appendPaginationParameterTemplates(blogsLink));

        Link blogLink = new Link(new UriTemplate(baseUri + API_ROOT + URL_SITE_BLOGS_BLOG),
                WebsiteResource.LINK_NAME_BLOG);
        resource.add(blogLink);

        resource.add(linkTo(methodOn(WebsiteRestController.class).getLatestBlogPost()).withRel(
                WebsiteResource.LINK_NAME_LATEST_BLOG));
        resource.add(linkTo(methodOn(WebsiteRestController.class).getRecentPublicBlogPosts()).withRel(
                WebsiteResource.LINK_NAME_RECENT_BLOGS));
        resource.add(linkTo(methodOn(WebsiteRestController.class).getRecentPublicCommentPosts()).withRel(
                WebsiteResource.LINK_NAME_RECENT_COMMENTS));
        resource.add(linkTo(methodOn(WebsiteRestController.class).getTagCloud()).withRel(
                WebsiteResource.LINK_NAME_TAG_CLOUD));

        Link profileLink = new Link(new UriTemplate(baseUri + API_ROOT + URL_SITE_PROFILES_USER),
                WebsiteResource.LINK_NAME_PROFILE);
        resource.add(profileLink);

        return resource;
    }
}
