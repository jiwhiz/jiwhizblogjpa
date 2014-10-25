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

import static com.jayway.restassured.RestAssured.given;
import static com.jiwhiz.rest.UtilConstants.API_ROOT;
import static com.jiwhiz.rest.author.AuthorApiUrls.URL_AUTHOR;
import static com.jiwhiz.rest.author.AuthorApiUrls.URL_AUTHOR_BLOGS;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import org.apache.http.HttpStatus;
import org.junit.Test;

import com.damnhandy.uri.template.UriTemplate;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jayway.restassured.filter.session.SessionFilter;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jiwhiz.rest.AbstractRestApiIntegrationTest;

/**
 * @author Yuan Ji
 *
 */
public class AuthorBlogRestApiIT extends AbstractRestApiIntegrationTest {

    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getAuthorBlogs_ShouldReturnBlogsFromJohn() {
        SessionFilter sessionFilter = new SessionFilter();

        // signin with John Doe
        given()
            .param("username", "user001")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        // get blogs link
        String blogsLink = given().filter(sessionFilter).when().get(API_ROOT + URL_AUTHOR).then().extract().path("_links.blogs.href");
        String blogsUrl =  UriTemplate.fromTemplate(blogsLink).set("page", 0).set("size", 10).set("sort", null).expand();
        
        given()
            .filter(sessionFilter)
        .when()
            .get(blogsUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("page.size", equalTo(10))
            .body("page.totalElements", equalTo(2))
            .body("page.totalPages", equalTo(1))
            .body("page.number", equalTo(0))
            .body("_embedded.blogPostList[0].id", equalTo("blog002"))
            .body("_embedded.blogPostList[0].title", equalTo("Blog 2"))
            .body("_embedded.blogPostList[0].content", equalTo("blog 2 content"))
            .body("_embedded.blogPostList[0].published", equalTo(false))
            .body("_embedded.blogPostList[1].id", equalTo("blog001"))
            .body("_embedded.blogPostList[1].title", equalTo("Blog 1"))
            .body("_embedded.blogPostList[1].content", equalTo("blog 1 content"))
            .body("_embedded.blogPostList[1].published", equalTo(true))
        ;
    }

    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getAuthorBlogs_ShouldReturnEmptyFromJane() {
        SessionFilter sessionFilter = new SessionFilter();

        // signin with Jane Doe
        given()
            .param("username", "user002")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        System.out.println(given().filter(sessionFilter).when().get(API_ROOT + URL_AUTHOR).asString());
        
        // get blogs link
        String blogsLink = given().filter(sessionFilter).when().get(API_ROOT + URL_AUTHOR).then().extract().path("_links.blogs.href");
        String blogsUrl =  UriTemplate.fromTemplate(blogsLink).set("page", 0).set("size", 10).set("sort", null).expand();
        
        System.out.println(given().filter(sessionFilter).when().get(blogsUrl).asString());
        
        given()
            .filter(sessionFilter)
        .when()
            .get(blogsUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("page.size", equalTo(10))
            .body("page.totalElements", equalTo(0))
            .body("page.totalPages", equalTo(0))
            .body("page.number", equalTo(0))
            .body("_embedded", nullValue())
        ;
    }

    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getAuthorBlogPostById_ShouldReturnBlog() {
        SessionFilter sessionFilter = new SessionFilter();

        // signin with John Doe
        given()
            .param("username", "user001")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        // get blog link
        String blogLink = given().filter(sessionFilter).when().get(API_ROOT + URL_AUTHOR).then().extract().path("_links.blog.href");
        String blogUrl =  UriTemplate.fromTemplate(blogLink).set("blogId", "blog001").expand();
        
        given()
            .filter(sessionFilter)
        .when()
            .get(blogUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo("blog001"))
            .body("title", equalTo("Blog 1"))
            .body("content", equalTo("blog 1 content"))
            .body("published", equalTo(true))
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getAuthorBlogPostById_ShouldReturn404_IfBlogNotFound() {
        SessionFilter sessionFilter = new SessionFilter();

        // signin with John Doe
        given()
            .param("username", "user001")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        // get blog link
        String blogLink = given().filter(sessionFilter).when().get(API_ROOT + URL_AUTHOR).then().extract().path("_links.blog.href");
        String blogUrl =  UriTemplate.fromTemplate(blogLink).set("blogId", "wrongId").expand();
        
        given()
            .filter(sessionFilter)
        .when()
            .get(blogUrl)
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getAuthorBlogPostById_ShouldReturn403_IfNotBlogAuthor() {
        SessionFilter sessionFilter = new SessionFilter();

        // signin with Jane Doe
        given()
            .param("username", "user002")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        // get blog link
        String blogLink = given().filter(sessionFilter).when().get(API_ROOT + URL_AUTHOR).then().extract().path("_links.blog.href");
        String blogUrl =  UriTemplate.fromTemplate(blogLink).set("blogId", "blog001").expand();
        
        given()
            .filter(sessionFilter)
        .when()
            .get(blogUrl)
        .then()
            .statusCode(HttpStatus.SC_FORBIDDEN)
        ;
    }

    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void createBlogPost_ShouldAddNewBlog() {
        SessionFilter sessionFilter = new SessionFilter();
        // signin with John Doe
        given()
            .param("username", "user001")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;

        String blogsLink = given().filter(sessionFilter).when().get(API_ROOT + URL_AUTHOR).then().extract().path("_links.blogs.href");
        String blogsUrl =  UriTemplate.fromTemplate(blogsLink).set("page", null).set("size", null).set("sort", null).expand();
        BlogPostForm blog = new BlogPostForm("Test Blog", "This is my test blog!", "Test,JiwhizBlog");
        Response response = 
            given()
                .filter(sessionFilter)
                .contentType(ContentType.JSON)
                .body(blog)
            .when()
                .post(blogsUrl)
            ;
                
        response
            .then()
            .statusCode(HttpStatus.SC_CREATED)
            .header("Location", containsString(API_ROOT + URL_AUTHOR_BLOGS))
        ;
    
        // verify new blog
        String blogUrl = response.getHeader("Location");
        given()
            .filter(sessionFilter)
        .when()
            .get(blogUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("title", equalTo("Test Blog"))
            .body("content", equalTo("This is my test blog!"))
            .body("_links.comments.templated", equalTo(true))
            .body("_links.comments.href", endsWith(blogUrl+"/comments{?page,size,sort}"))
        ;
    }
}
