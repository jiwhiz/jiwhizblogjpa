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

/**
 * Urls for user REST API endpoints.
 * 
 * @author Yuan Ji
 */
public interface UserApiUrls {
    String URL_USER = "/user";
    String URL_USER_PROFILE = "/user/profile";
    String URL_USER_SOCIAL_CONNECTIONS = "/user/socialConnections";
    String URL_USER_BLOGS_BLOG_COMMENTS = "/user/blogs/{blogId}/comments";
    String URL_USER_COMMENTS = "/user/comments";
    String URL_USER_COMMENTS_COMMENT = "/user/comments/{commentId}";
}
