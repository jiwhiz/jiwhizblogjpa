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

import static com.jayway.restassured.RestAssured.given;
import static com.jiwhiz.rest.UtilConstants.API_ROOT;
import static com.jiwhiz.rest.user.UserApiUrls.URL_USER_BLOGS_BLOG_COMMENTS;
import static com.jiwhiz.rest.user.UserApiUrls.URL_USER_COMMENTS;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import org.apache.http.HttpStatus;
import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jayway.restassured.filter.session.SessionFilter;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jiwhiz.rest.AbstractRestApiIntegrationTest;

/**
 * @author Yuan Ji
 *
 */
public class PostCommentRestApiIT extends AbstractRestApiIntegrationTest {
    
    @Test
    public void postComment_ShouldReturn403_IfNotSignin() {
        CommentForm comment = new CommentForm("Test comment.");
        given()
            .contentType(ContentType.JSON)
            .body(comment)
        .when()
            .post(API_ROOT + URL_USER_BLOGS_BLOG_COMMENTS, "blog001")
        .then()
            .statusCode(HttpStatus.SC_FORBIDDEN)
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void postComment_ShouldAddNewApprovedComment_IfPostByTrustedAccount() {
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

        CommentForm comment = new CommentForm("Test comment.");
        Response response = given()
            .filter(sessionFilter)
            .contentType(ContentType.JSON)
            .body(comment)
        .when()
            .post(API_ROOT + URL_USER_BLOGS_BLOG_COMMENTS, "blog001")
        ;
        
        response
            .then()
            .statusCode(HttpStatus.SC_CREATED)
            .header("Location", containsString(API_ROOT + URL_USER_COMMENTS))
        ;
        
        String commentUrl = response.getHeader("Location");
        given()
            .filter(sessionFilter)
        .when()
            .get(commentUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("content", equalTo("Test comment."))
            .body("status", equalTo("APPROVED"))
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void postComment_ShouldAddNewPendingComment_IfPostByUntrustedAccount() {
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

        CommentForm comment = new CommentForm("Test comment by Jane.");
        Response response = given()
            .filter(sessionFilter)
            .contentType(ContentType.JSON)
            .body(comment)
        .when()
            .post(API_ROOT + URL_USER_BLOGS_BLOG_COMMENTS, "blog001")
        ;
        
        response
            .then()
            .statusCode(HttpStatus.SC_CREATED)
            .header("Location", containsString(API_ROOT + URL_USER_COMMENTS))
        ;
        
        // verify the new comment
        String commentUrl = response.getHeader("Location");
        given()
            .filter(sessionFilter)
        .when()
            .get(commentUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("content", equalTo("Test comment by Jane."))
            .body("status", equalTo("PENDING"))
        ;
    }
}
