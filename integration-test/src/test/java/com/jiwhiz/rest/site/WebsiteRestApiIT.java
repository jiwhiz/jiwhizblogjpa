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
import static com.jiwhiz.rest.site.SiteApiUrls.URL_SITE_BLOGS;
import static com.jiwhiz.rest.site.SiteApiUrls.URL_SITE_BLOGS_BLOG;
import static com.jiwhiz.rest.site.SiteApiUrls.URL_SITE_LATEST_BLOG;
import static com.jiwhiz.rest.site.SiteApiUrls.URL_SITE_PROFILES_USER;
import static com.jiwhiz.rest.site.SiteApiUrls.URL_SITE_RECENT_BLOGS;
import static com.jiwhiz.rest.site.SiteApiUrls.URL_SITE_RECENT_COMMENTS;
import static com.jiwhiz.rest.site.SiteApiUrls.URL_SITE_TAG_CLOUDS;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

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
public class WebsiteRestApiIT extends AbstractRestApiIntegrationTest {
    /**
     * Test REST API GET /api/public
     * 
     */
    @Test
    public void getPublic_ShouldReturnWebsiteResource() {
        when()
            .get(API_ROOT + URL_SITE)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("_links.self.href", endsWith(URL_SITE))
            .body("_links.blogs.templated", equalTo(true))
            .body("_links.blogs.href", endsWith(URL_SITE_BLOGS+"{?page,size,sort}"))
            .body("_links.blog.templated", equalTo(true))
            .body("_links.blog.href", endsWith(URL_SITE_BLOGS_BLOG))
            .body("_links.profile.templated", equalTo(true))
            .body("_links.profile.href", endsWith(URL_SITE_PROFILES_USER))
            .body("_links.latestBlog.href", endsWith(URL_SITE_LATEST_BLOG))
            .body("_links.recentBlogs.href", endsWith(URL_SITE_RECENT_BLOGS))
            .body("_links.recentComments.href", endsWith(URL_SITE_RECENT_COMMENTS))
            .body("_links.tagCloud.href", endsWith(URL_SITE_TAG_CLOUDS))
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getUserProfile_ShouldReturnUserProfileForJohn() {
        String profileLink = when().get(API_ROOT + URL_SITE).then().extract().path("_links.profile.href");
        
        //John Doe is an author/admin
        when()
            .get(profileLink, "user001")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("userId", equalTo("user001"))
            .body("displayName", equalTo("John Doe"))
            .body("admin", equalTo(true))
            .body("author", equalTo(true))
        ;

        //Jane Doe is an author
        when()
            .get(profileLink, "user002")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("userId", equalTo("user002"))
            .body("displayName", equalTo("Jane Doe"))
            .body("admin", equalTo(false))
            .body("author", equalTo(true))
        ;
        
        //Junior Doe is a user
        when()
            .get(profileLink, "user003")
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("userId", equalTo("user003"))
            .body("displayName", equalTo("Junior Doe"))
            .body("admin", equalTo(false))
            .body("author", equalTo(false))
        ;

    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getUserComments_ShouldReturnUserPublicComments() {
        String profileLink = when().get(API_ROOT + URL_SITE).then().extract().path("_links.profile.href");
        
        // John has one approved comment
        String commentsLink = when().get(profileLink, "user001").then().extract().path("_links.comments.href");
        String commentsUrl =  UriTemplate.fromTemplate(commentsLink).set("page", 0).set("size", 10).set("sort", null).expand();
        
        when()
            .get(commentsUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("page.size", equalTo(10))
            .body("page.totalElements", equalTo(1))
            .body("page.totalPages", equalTo(1))
            .body("page.number", equalTo(0))
            .body("_embedded.commentPostList[0].id", equalTo("comm002"))
            .body("_embedded.commentPostList[0].status", equalTo("APPROVED"))
        ;
        
        // Jane has one approved comment
        commentsLink = when().get(profileLink, "user002").then().extract().path("_links.comments.href");
        commentsUrl =  UriTemplate.fromTemplate(commentsLink).set("page", 0).set("size", 10).set("sort", null).expand();
        
        when()
            .get(commentsUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("page.size", equalTo(10))
            .body("page.totalElements", equalTo(1))
            .body("page.totalPages", equalTo(1))
            .body("page.number", equalTo(0))
            .body("_embedded.commentPostList[0].id", equalTo("comm001"))
            .body("_embedded.commentPostList[0].status", equalTo("APPROVED"))
        ;

    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getLatestBlog_ShouldReturnOneBlog() {
        String latestBlogLink = when().get(API_ROOT + URL_SITE).then().extract().path("_links.latestBlog.href");
        
        when()
            .get(latestBlogLink)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("content", equalTo("blog 1 content"))
            .body("published", equalTo(true))
            .body("approvedCommentCount", equalTo(2))
        ;

        String latestBlogAuthorLink = when().get(latestBlogLink).then().extract().path("_links.author.href");
        when()
            .get(latestBlogAuthorLink)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("userId", equalTo("user001"))
            .body("displayName", equalTo("John Doe"))
            .body("admin", equalTo(true))
            .body("author", equalTo(true))
        ;

        String latestBlogCommentsLink = when().get(latestBlogLink).then().extract().path("_links.comments.href");
        String latestBlogCommentsUrl =  UriTemplate.fromTemplate(latestBlogCommentsLink).set("page", 0).set("size", 10).set("sort", null).expand();
        when()
            .get(latestBlogCommentsUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("page.size", equalTo(10))
            .body("page.totalElements", equalTo(2))
            .body("page.totalPages", equalTo(1))
            .body("page.number", equalTo(0))
            .body("_embedded.commentPostList[0].id", equalTo("comm001"))
            .body("_embedded.commentPostList[0].status", equalTo("APPROVED"))
            .body("_embedded.commentPostList[1].id", equalTo("comm002"))
            .body("_embedded.commentPostList[1].status", equalTo("APPROVED"))
        ;

    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getRecentPublicBlogs_ShouldReturnOneBlog() {
        String recentBlogsLink = when().get(API_ROOT + URL_SITE).then().extract().path("_links.recentBlogs.href");
        
        when()
            .get(recentBlogsLink)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("_embedded.blogPostList", hasSize(1))
            .body("_embedded.blogPostList[0].content", equalTo("blog 1 content"))
            .body("_embedded.blogPostList[0].published", equalTo(true))
            .body("_embedded.blogPostList[0].approvedCommentCount", equalTo(2))
        ;

    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getRecentPublicComments_ShouldReturnTwoComments() {
        String recentCommentsLink = when().get(API_ROOT + URL_SITE).then().extract().path("_links.recentComments.href");
        
        when()
            .get(recentCommentsLink)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("_embedded.commentPostList", hasSize(2))
            .body("_embedded.commentPostList[0].id", equalTo("comm002"))
            .body("_embedded.commentPostList[0].content", equalTo("comment by author John Doe"))
            .body("_embedded.commentPostList[0].status", equalTo("APPROVED"))
            .body("_embedded.commentPostList[1].id", equalTo("comm001"))
            .body("_embedded.commentPostList[1].content", equalTo("approved comment by Jane Doe"))
            .body("_embedded.commentPostList[1].status", equalTo("APPROVED"))
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getTagCloud_ShouldReturnTwoTags() {
        String tagCloudLink = when().get(API_ROOT + URL_SITE).then().extract().path("_links.tagCloud.href");
        
        when()
            .get(tagCloudLink)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("tag", hasItems("JiwhizBlog", "Test"))
        ;

    }
}
