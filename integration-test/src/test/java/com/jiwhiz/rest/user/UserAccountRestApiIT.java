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
import static com.jayway.restassured.RestAssured.when;
import static com.jiwhiz.rest.UtilConstants.API_ROOT;
import static com.jiwhiz.rest.user.UserApiUrls.URL_USER;
import static com.jiwhiz.rest.user.UserApiUrls.URL_USER_COMMENTS;
import static com.jiwhiz.rest.user.UserApiUrls.URL_USER_COMMENTS_COMMENT;
import static com.jiwhiz.rest.user.UserApiUrls.URL_USER_PROFILE;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;

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
public class UserAccountRestApiIT extends AbstractRestApiIntegrationTest {
    
    @Test
    public void getCurrentUserAccount_ShouldReturn403_IfNotSignin() {
        when()
            .get(API_ROOT + URL_USER)
        .then()
            .statusCode(HttpStatus.SC_FORBIDDEN)
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void getCurrentUserAccount_ShouldReturnLoggedInUserAccount() {
        SessionFilter sessionFilter = new SessionFilter();

        //signin first
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
            .get(API_ROOT + URL_USER)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo("user001"))
            .body("email", equalTo("user1@jiwhizblog.com"))
            .body("_links.self.href", endsWith(URL_USER))
            .body("_links.profile.href", endsWith(URL_USER_PROFILE))
            .body("_links.comments.templated", equalTo(true))
            .body("_links.comments.href", endsWith(URL_USER_COMMENTS+"{?page,size,sort}"))
            .body("_links.comment.templated", equalTo(true))
            .body("_links.comment.href", endsWith(URL_USER_COMMENTS_COMMENT))
        ;
    }
    
    @Test
    @DatabaseSetup("classpath:/data/testBlogs.xml")
    public void patchUserProfile_ShouldUpdateProfile() {
        SessionFilter sessionFilter = new SessionFilter();

        //signin first
        given()
            .param("username", "user001")
            .filter(sessionFilter)
        .when()
            .post("/signin")
        .then()
            .statusCode(HttpStatus.SC_MOVED_TEMPORARILY)
        ;
        
        Map<String, String> testUpdates = new HashMap<>();
        testUpdates.put("displayName", "New Name");
        testUpdates.put("email", "john.doe@jiwhizbog.com");
        
        given()
            .filter(sessionFilter)
            .contentType(ContentType.JSON)
            .body(testUpdates)
        .when()
            .patch(API_ROOT + URL_USER_PROFILE)
        .then()
            .statusCode(HttpStatus.SC_NO_CONTENT)
        ;

        // verify
        given()
            .filter(sessionFilter)
        .when()
            .get(API_ROOT + URL_USER)
        .then()
            .statusCode(HttpStatus.SC_OK)
            .contentType(ContentType.JSON)
            .body("id", equalTo("user001"))
            .body("email", equalTo("john.doe@jiwhizbog.com"))
            .body("displayName", equalTo("New Name"))
        ;

    }

}
