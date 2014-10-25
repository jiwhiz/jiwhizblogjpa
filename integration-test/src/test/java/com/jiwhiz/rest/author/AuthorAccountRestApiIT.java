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
import static com.jayway.restassured.RestAssured.when;
import static com.jiwhiz.rest.UtilConstants.API_ROOT;
import static com.jiwhiz.rest.author.AuthorApiUrls.URL_AUTHOR;
import static com.jiwhiz.rest.author.AuthorApiUrls.URL_AUTHOR_BLOGS;
import static com.jiwhiz.rest.author.AuthorApiUrls.URL_AUTHOR_BLOGS_BLOG;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

import org.apache.http.HttpStatus;
import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jayway.restassured.filter.session.SessionFilter;
import com.jayway.restassured.http.ContentType;
import com.jiwhiz.rest.AbstractRestApiIntegrationTest;

/**
 * @author Yuan Ji
 *
 */
public class AuthorAccountRestApiIT extends AbstractRestApiIntegrationTest {
    @Test
    public void getCurrentAuthorAccount_ShouldReturn403_IfNotSignin() {
        when()
            .get(API_ROOT + URL_AUTHOR)
        .then()
            .statusCode(HttpStatus.SC_FORBIDDEN)
        ;
    }

    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getCurrentAuthorAccount_ShouldReturn403_IfNotAuthor() {
        SessionFilter sessionFilter = new SessionFilter();

        //signin Junior Doe
        given()
            .param("username", "user003")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        given()
            .filter(sessionFilter)
        .when()
            .get(API_ROOT + URL_AUTHOR)
        .then()
            .statusCode(HttpStatus.SC_FORBIDDEN)
        ;
    }

    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getCurrentAuthorAccount_ShouldReturnLoggedInAuthorAccount() {
        SessionFilter sessionFilter = new SessionFilter();

        //signin John
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
            .get(API_ROOT + URL_AUTHOR)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo("user001"))
            .body("email", equalTo("user1@jiwhizblog.com"))
            .body("_links.self.href", endsWith(URL_AUTHOR))
            .body("_links.blogs.templated", equalTo(true))
            .body("_links.blogs.href", endsWith(URL_AUTHOR_BLOGS+"{?page,size,sort}"))
            .body("_links.blog.templated", equalTo(true))
            .body("_links.blog.href", endsWith(URL_AUTHOR_BLOGS_BLOG))
        ;
        
        //signin Jane
        given()
            .param("username", "user002")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        given()
            .filter(sessionFilter)
        .when()
            .get(API_ROOT + URL_AUTHOR)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo("user002"))
            .body("email", equalTo("user2@jiwhizblog.com"))
            .body("_links.self.href", endsWith(URL_AUTHOR))
            .body("_links.blogs.templated", equalTo(true))
            .body("_links.blogs.href", endsWith(URL_AUTHOR_BLOGS+"{?page,size,sort}"))
            .body("_links.blog.templated", equalTo(true))
            .body("_links.blog.href", endsWith(URL_AUTHOR_BLOGS_BLOG))
        ;

    }
}
