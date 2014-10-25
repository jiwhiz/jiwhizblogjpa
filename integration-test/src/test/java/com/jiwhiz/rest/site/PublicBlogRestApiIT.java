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

import static com.jayway.restassured.RestAssured.when;
import static com.jiwhiz.rest.UtilConstants.API_ROOT;
import static com.jiwhiz.rest.site.SiteApiUrls.URL_SITE;
import static com.jiwhiz.rest.site.SiteApiUrls.URL_SITE_BLOGS_BLOG_COMMENTS;
import static com.jiwhiz.rest.site.SiteApiUrls.URL_SITE_BLOGS_BLOG_COMMENTS_COMMENT;
import static org.hamcrest.Matchers.equalTo;

import org.apache.http.HttpStatus;
import org.junit.Test;

import com.damnhandy.uri.template.UriTemplate;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jayway.restassured.http.ContentType;
import com.jiwhiz.rest.AbstractRestApiIntegrationTest;

/**
 * @author Yuan Ji
 *
 */
public class PublicBlogRestApiIT extends AbstractRestApiIntegrationTest {
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getPublicBlogPosts_ShouldReturnPublicBlogs() {
        String blogsLink = when().get(API_ROOT + URL_SITE).then().extract().path("_links.blogs.href");
        String blogsUrl =  UriTemplate.fromTemplate(blogsLink).set("page", 0).set("size", 10).set("sort", null).expand();
        
        when()
            .get(blogsUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("page.size", equalTo(10))
            .body("page.totalElements", equalTo(1))
            .body("page.totalPages", equalTo(1))
            .body("page.number", equalTo(0))
            .body("_embedded.blogPostList[0].id", equalTo("blog001"))
            .body("_embedded.blogPostList[0].published", equalTo(true))
            ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getPublicBlogPostById_ShouldReturnPublicBlog() {
        String blogLink = when().get(API_ROOT + URL_SITE).then().extract().path("_links.blog.href");
        String blogUrl =  UriTemplate.fromTemplate(blogLink).set("blogId", "blog001").expand();
        
        when()
            .get(blogUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo("blog001"))
            .body("published", equalTo(true))
            .body("title", equalTo("Blog 1"))
            ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getPublicBlogPostById_ShouldReturn404_IfBlogNotPublished() {
        String blogLink = when().get(API_ROOT + URL_SITE).then().extract().path("_links.blog.href");
        String blogUrl =  UriTemplate.fromTemplate(blogLink).set("blogId", "blog002").expand();
        
        when()
            .get(blogUrl)
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getPublicBlogPostById_ShouldReturn404_IfBlogNotFound() {
        String blogLink = when().get(API_ROOT + URL_SITE).then().extract().path("_links.blog.href");
        String blogUrl =  UriTemplate.fromTemplate(blogLink).set("blogId", "blogXXX").expand();
        
        when()
            .get(blogUrl)
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getBlogApprovedCommentPosts_ShouldReturnApprovedComments() {
        String blogLink = when().get(API_ROOT + URL_SITE).then().extract().path("_links.blog.href");
        String blogUrl =  UriTemplate.fromTemplate(blogLink).set("blogId", "blog001").expand();
        String commentsLink = when().get(blogUrl).then().extract().path("_links.comments.href");
        String commentsUrl =  UriTemplate.fromTemplate(commentsLink).set("page", 0).set("size", 10).set("sort", null).expand();
        
        when()
            .get(commentsUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("page.size", equalTo(10))
            .body("page.totalElements", equalTo(2))
            .body("page.totalPages", equalTo(1))
            .body("page.number", equalTo(0))
            .body("_embedded.commentPostList[0].id", equalTo("comm001"))
            .body("_embedded.commentPostList[0].content", equalTo("approved comment by Jane Doe"))
            .body("_embedded.commentPostList[1].id", equalTo("comm002"))
            .body("_embedded.commentPostList[1].content", equalTo("comment by author John Doe"))
            ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getBlogApprovedCommentPosts_ShouldReturn404_IfBlogNotFound() {
        String commentsUrl =  UriTemplate.fromTemplate(API_ROOT + URL_SITE_BLOGS_BLOG_COMMENTS).set("blogId", "blogXXX").expand();
        
        when()
            .get(commentsUrl)
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getBlogApprovedCommentPostById_ShouldReturnApprovedComment() {
        String commentUrl =  UriTemplate.fromTemplate(API_ROOT + URL_SITE_BLOGS_BLOG_COMMENTS_COMMENT)
                .set("blogId", "blog001").set("commentId", "comm001").expand();
        
        when()
            .get(commentUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo("comm001"))
            .body("status", equalTo("APPROVED"))
            .body("content", equalTo("approved comment by Jane Doe"))
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getBlogApprovedCommentPostByID_ShouldReturn404_IfBlogNotFound() {
        String commentUrl =  UriTemplate.fromTemplate(API_ROOT + URL_SITE_BLOGS_BLOG_COMMENTS_COMMENT)
                .set("blogId", "blogXXX").set("commentId", "comm001").expand();
        
        when()
            .get(commentUrl)
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
            ;
    }
}
