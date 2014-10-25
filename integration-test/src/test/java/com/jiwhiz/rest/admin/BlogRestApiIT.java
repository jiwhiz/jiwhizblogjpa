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
package com.jiwhiz.rest.admin;

import static com.jayway.restassured.RestAssured.given;
import static com.jiwhiz.rest.UtilConstants.API_ROOT;
import static com.jiwhiz.rest.admin.AdminApiUrls.*;
import static org.hamcrest.Matchers.*;

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
public class BlogRestApiIT extends AbstractRestApiIntegrationTest {
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getAllBlogPosts_ShouldReturnAllBlogs() {
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
        
        // get blogs link
        String blogsLink = given().filter(sessionFilter).when().get(API_ROOT + URL_ADMIN).then().extract().path("_links.blogs.href");
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
            .body("_embedded.blogPostList[0]._links.author.href", endsWith(URL_ADMIN_USERS+"/user001"))
            .body("_embedded.blogPostList[0]._links.comments.href", endsWith("blog002/comments{?page,size,sort}"))
            .body("_embedded.blogPostList[1].id", equalTo("blog001"))
        ;
    }

}
