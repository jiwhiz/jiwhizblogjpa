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

import static com.jiwhiz.rest.author.AuthorApiUrls.URL_AUTHOR_BLOGS;
import static com.jiwhiz.rest.author.AuthorApiUrls.URL_AUTHOR_BLOGS_BLOG;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import com.jiwhiz.rest.ResourceNotFoundException;
import com.jiwhiz.rest.UtilConstants;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author Yuan Ji
 */
@RestController
@Api(value="Author Blog",
     description="Author blog management", position = 21)
public class AuthorBlogRestController extends AbstractAuthorRestController {

    private final AuthorBlogResourceAssembler authorBlogResourceAssembler;
    @Inject
    public AuthorBlogRestController(
            UserAccountService userAccountService, 
            BlogPostRepository blogPostRepository,
            AuthorBlogResourceAssembler authorBlogResourceAssembler) {
        super(userAccountService, blogPostRepository);
        this.authorBlogResourceAssembler = authorBlogResourceAssembler;
    }

    @ApiOperation(value = "Get Author Blog Posts", 
            notes = "Return blog post resources from logged in author, order by created time.")
    @RequestMapping(method = RequestMethod.GET, value = URL_AUTHOR_BLOGS) 
    @Transactional(readOnly=true)
    public HttpEntity<PagedResources<AuthorBlogResource>> getAuthorBlogPosts(
            @PageableDefault(size = UtilConstants.DEFAULT_RETURN_RECORD_COUNT, page = 0)Pageable pageable,
            PagedResourcesAssembler<BlogPost> assembler) {
        UserAccount currentUser = getCurrentAuthenticatedAuthor();        
        Page<BlogPost> blogPosts = this.blogPostRepository.findByAuthorOrderByCreatedTimeDesc(currentUser, pageable);
        return new ResponseEntity<>(assembler.toResource(blogPosts, authorBlogResourceAssembler), HttpStatus.OK);
    }

    @ApiOperation(value = "Get Admin Account", 
            notes = "Return logged in admin account resource, with links to other resources.")
    @RequestMapping(method = RequestMethod.GET, value = URL_AUTHOR_BLOGS_BLOG) 
    @Transactional(readOnly=true)
    public HttpEntity<AuthorBlogResource> getAuthorBlogPostById(@PathVariable("blogId") String blogId) 
            throws ResourceNotFoundException {
        BlogPost blogPost = getBlogByIdAndCheckAuthor(blogId);
        return new ResponseEntity<>(authorBlogResourceAssembler.toResource(blogPost), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = URL_AUTHOR_BLOGS)
    @Transactional
    public HttpEntity<Void> createBlogPost(@RequestBody BlogPostForm blogPostForm) {
        UserAccount currentUser = getCurrentAuthenticatedAuthor();
        BlogPost blogPost = new BlogPost(currentUser, blogPostForm.getTitle(), 
                blogPostForm.getContent(), blogPostForm.getTagString());
        
        blogPost = blogPostRepository.save(blogPost);
        AuthorBlogResource resource = authorBlogResourceAssembler.toResource(blogPost);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(resource.getLink("self").getHref()));

        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = URL_AUTHOR_BLOGS_BLOG)
    @Transactional
    public HttpEntity<Void> updateBlogPost(
            @PathVariable("blogId") String blogId, 
            @RequestBody BlogPostForm blogPostForm) throws ResourceNotFoundException {
        BlogPost blogPost = getBlogByIdAndCheckAuthor(blogId);
        blogPost.setContent(blogPostForm.getContent());
        blogPost.setTitle(blogPostForm.getTitle());
        blogPost.parseAndSetTagString(blogPostForm.getTagString());
        blogPost = blogPostRepository.save(blogPost);
        
        AuthorBlogResource resource = authorBlogResourceAssembler.toResource(blogPost);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(resource.getLink("self").getHref()));
        
        return new ResponseEntity<Void>(httpHeaders, HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(method = RequestMethod.PATCH, value = URL_AUTHOR_BLOGS_BLOG)
    @Transactional
    public HttpEntity<AuthorBlogResource> patchBlogPost(
            @PathVariable("blogId") String blogId, 
            @RequestBody Map<String, String> updateMap) throws ResourceNotFoundException {
        BlogPost blogPost = getBlogByIdAndCheckAuthor(blogId);
        String content = updateMap.get("content");
        if (content != null) {
            blogPost.setContent(content);
        }
        String title = updateMap.get("title");
        if (title != null) {
            blogPost.setTitle(title);
        }
        String tagString = updateMap.get("tagString");
        if (tagString != null) {
            blogPost.parseAndSetTagString(tagString);
        }
        String published = updateMap.get("published");
        if (published != null) {
            blogPost.setPublished(published.equals("true"));
        }
        
        blogPost = blogPostRepository.save(blogPost);

        AuthorBlogResource resource = authorBlogResourceAssembler.toResource(blogPost);
        return new ResponseEntity<>(resource, HttpStatus.OK);

    }
}
