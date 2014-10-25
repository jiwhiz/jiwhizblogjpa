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
import static com.jiwhiz.rest.user.UserApiUrls.URL_USER;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.Test;

import com.damnhandy.uri.template.UriTemplate;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jayway.restassured.filter.session.SessionFilter;
import com.jayway.restassured.http.ContentType;
import com.jiwhiz.rest.AbstractRestApiIntegrationTest;

/**
 * @author Yuan Ji
 *
 */
public class UserCommentRestApiIT extends AbstractRestApiIntegrationTest {
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getCurrentUserComments_ShouldReturnOneCommentFromJohn() {
        SessionFilter sessionFilter = new SessionFilter();

        // signin with John Doe first
        given()
            .param("username", "user001")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        // get comments link
        String commentsLink = given().filter(sessionFilter).when().get(API_ROOT + URL_USER).then().extract().path("_links.comments.href");
        String commentsUrl =  UriTemplate.fromTemplate(commentsLink).set("page", 0).set("size", 10).set("sort", null).expand();
        
        given()
            .filter(sessionFilter)
        .when()
            .get(commentsUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("page.size", equalTo(10))
            .body("page.totalElements", equalTo(1))
            .body("page.totalPages", equalTo(1))
            .body("page.number", equalTo(0))
            .body("_embedded.commentPostList[0].id", equalTo("comm002"))
            .body("_embedded.commentPostList[0].content", equalTo("comment by author John Doe"))
            .body("_embedded.commentPostList[0].status", equalTo("APPROVED"))
        ;
    }

    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getCurrentUserComments_ShouldReturnThreeCommentsFromJane() {
        SessionFilter sessionFilter = new SessionFilter();

        // signin with Jane Doe first
        given()
            .param("username", "user002")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        // get comments link
        String commentsLink = given().filter(sessionFilter).when().get(API_ROOT + URL_USER).then().extract().path("_links.comments.href");
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
            .body("_embedded.commentPostList[0].id", equalTo("comm101"))
            .body("_embedded.commentPostList[1].id", equalTo("comm003"))
            .body("_embedded.commentPostList[1].content", equalTo("pending comment by Jane Doe"))
            .body("_embedded.commentPostList[1].status", equalTo("PENDING"))
            .body("_embedded.commentPostList[2].id", equalTo("comm001"))
            .body("_embedded.commentPostList[2].content", equalTo("approved comment by Jane Doe"))
            .body("_embedded.commentPostList[2].status", equalTo("APPROVED"))
            
        ;
    }

    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getCommentById_ShouldReturnOneCommentFromJohn() {
        SessionFilter sessionFilter = new SessionFilter();

        // signin with John Doe first
        given()
            .param("username", "user001")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        // get comment link
        String commentLink = given().filter(sessionFilter).when().get(API_ROOT + URL_USER).then().extract().path("_links.comment.href");
        String commentUrl =  UriTemplate.fromTemplate(commentLink).set("commentId", "comm002").expand();
        
        given()
            .filter(sessionFilter)
        .when()
            .get(commentUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo("comm002"))
            .body("content", equalTo("comment by author John Doe"))
            .body("status", equalTo("APPROVED"))
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getCommentById_ShouldReturn404_IfCommentNotFound() {
        SessionFilter sessionFilter = new SessionFilter();

        // signin with John Doe first
        given()
            .param("username", "user001")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        // get comment link
        String commentLink = given().filter(sessionFilter).when().get(API_ROOT + URL_USER).then().extract().path("_links.comment.href");
        String commentUrl =  UriTemplate.fromTemplate(commentLink).set("commentId", "wrongId").expand();
        
        given()
            .filter(sessionFilter)
        .when()
            .get(commentUrl)
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getCommentById_ShouldReturn403_IfCommentAuthorIsNotCurrentUser() {
        SessionFilter sessionFilter = new SessionFilter();

        // signin with John Doe first
        given()
            .param("username", "user001")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        // get Jane's comment link
        String commentLink = given().filter(sessionFilter).when().get(API_ROOT + URL_USER).then().extract().path("_links.comment.href");
        String commentUrl =  UriTemplate.fromTemplate(commentLink).set("commentId", "comm001").expand();
        
        given()
            .filter(sessionFilter)
        .when()
            .get(commentUrl)
        .then()
            .statusCode(HttpStatus.SC_FORBIDDEN)
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void updateComment_ShouldChangeCommentContent() {
        SessionFilter sessionFilter = new SessionFilter();

        // signin with John Doe first
        given()
            .param("username", "user001")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        String commentLink = given().filter(sessionFilter).when().get(API_ROOT + URL_USER).then().extract().path("_links.comment.href");
        String commentUrl =  UriTemplate.fromTemplate(commentLink).set("commentId", "comm002").expand();
        
        Map<String, String> testUpdates = new HashMap<>();
        testUpdates.put("content", "updated comment by John Doe");
        
        given()
            .filter(sessionFilter)
            .contentType(ContentType.JSON)
            .body(testUpdates)
        .when()
            .patch(commentUrl)
        .then()
            .statusCode(HttpStatus.SC_NO_CONTENT)
        ;

        //verify
        given()
            .filter(sessionFilter)
        .when()
            .get(commentUrl)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo("comm002"))
            .body("content", equalTo("updated comment by John Doe"))
            .body("status", equalTo("APPROVED"))
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void updateComment_ShouldReturn404_IfCommentNotFound() {
        SessionFilter sessionFilter = new SessionFilter();

        // signin with John Doe first
        given()
            .param("username", "user001")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        String commentLink = given().filter(sessionFilter).when().get(API_ROOT + URL_USER).then().extract().path("_links.comment.href");
        String commentUrl =  UriTemplate.fromTemplate(commentLink).set("commentId", "wrongId").expand();
        
        Map<String, String> testUpdates = new HashMap<>();
        testUpdates.put("content", "updated comment by John Doe");
        
        given()
            .filter(sessionFilter)
            .contentType(ContentType.JSON)
            .body(testUpdates)
        .when()
            .patch(commentUrl)
        .then()
            .statusCode(HttpStatus.SC_NOT_FOUND)
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void updateComment_ShouldReturn403_IfCommentAuthorIsNotCurrentUser() {
        SessionFilter sessionFilter = new SessionFilter();

        // signin with John Doe first
        given()
            .param("username", "user001")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        String commentLink = given().filter(sessionFilter).when().get(API_ROOT + URL_USER).then().extract().path("_links.comment.href");
        String commentUrl =  UriTemplate.fromTemplate(commentLink).set("commentId", "comm001").expand();
        
        Map<String, String> testUpdates = new HashMap<>();
        testUpdates.put("content", "updated comment by John Doe");
        
        given()
            .filter(sessionFilter)
            .contentType(ContentType.JSON)
            .body(testUpdates)
        .when()
            .patch(commentUrl)
        .then()
            .statusCode(HttpStatus.SC_FORBIDDEN)
        ;
    }
}
