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
import static com.jiwhiz.rest.author.AuthorApiUrls.*;
import static org.hamcrest.Matchers.equalTo;

import org.apache.http.HttpStatus;
import org.junit.Test;

import com.damnhandy.uri.template.UriTemplate;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jayway.restassured.filter.session.SessionFilter;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jiwhiz.rest.AbstractRestApiIntegrationTest;

/**
 * 
 * @author Yuan Ji
 *
 */
public class AuthorBlogCommentRestApiIT extends AbstractRestApiIntegrationTest {
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getCommentPostsByBlogPostId_ShouldReturnCommentsOfBlog() {
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
        
        //get comment link
        String commentsLink = given().filter(sessionFilter).when().get(blogUrl).then().extract().path("_links.comments.href");
        String commentsUrl =  UriTemplate.fromTemplate(commentsLink).set("page", 0).set("size", 10).set("sort", null).expand();
        
        given()
            .filter(sessionFilter)
        .when()
            .get(commentsUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("page.size", equalTo(10))
            .body("page.totalElements", equalTo(3))
            .body("page.totalPages", equalTo(1))
            .body("page.number", equalTo(0))
            .body("_embedded.commentPostList[0].id", equalTo("comm003"))
            .body("_embedded.commentPostList[0].content", equalTo("pending comment by Jane Doe"))
            .body("_embedded.commentPostList[0].status", equalTo("PENDING"))
            .body("_embedded.commentPostList[1].id", equalTo("comm002"))
            .body("_embedded.commentPostList[2].id", equalTo("comm001"))
            
        ;
    }

    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getCommentPostsByBlogPostId_ShouldReturn404_IfBlogNotFound() {
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

        given()
            .filter(sessionFilter)
        .when()
            .get(API_ROOT + URL_AUTHOR_BLOGS_BLOG_COMMENTS, "wrongId")
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getCommentPostsByBlogPostId_ShouldReturn403_IfNotBlogAuthor() {
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
        
        Response response = 
            given()
                .filter(sessionFilter)
            .when()
                .get(API_ROOT + URL_AUTHOR_BLOGS_BLOG_COMMENTS, "blog001")
            ;
        
        //System.out.println(response.asString());
        
        response
        .then()
            .statusCode(HttpStatus.SC_FORBIDDEN)
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getCommentPostById_ShouldReturnComment() {
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
        
        given()
            .filter(sessionFilter)
        .when()
            .get(API_ROOT + URL_AUTHOR_BLOGS_BLOG_COMMENTS_COMMENT, "blog001", "comm001")
        .then()
            .statusCode(HttpStatus.SC_OK)
        ;

    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getCommentPostById_ShouldReturn404_IfWrongId() {
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

        given()
            .filter(sessionFilter)
        .when()
            .get(API_ROOT + URL_AUTHOR_BLOGS_BLOG_COMMENTS_COMMENT, "wrongId", "comm001")
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
        ;
        
        given()
            .filter(sessionFilter)
        .when()
            .get(API_ROOT + URL_AUTHOR_BLOGS_BLOG_COMMENTS_COMMENT, "blog001", "wrongId")
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
        ;

        
        given()
            .filter(sessionFilter)
        .when()
            .get(API_ROOT + URL_AUTHOR_BLOGS_BLOG_COMMENTS_COMMENT, "blog001", "comm101")
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
        ;
    }

}
