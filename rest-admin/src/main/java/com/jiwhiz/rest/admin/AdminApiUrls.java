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

/**
 * Urls for admin REST API endpoints.
 * 
 * @author Yuan Ji
 *
 */
public interface AdminApiUrls {
    String URL_ADMIN = "/admin";
    String URL_ADMIN_USERS = "/admin/users";
    String URL_ADMIN_USERS_USER = "/admin/users/{userId}";
    String URL_ADMIN_USERS_USER_SOCIAL_CONNECTIONS = "/admin/users/{userId}/socialConnections";
    String URL_ADMIN_BLOGS = "/admin/blogs";
    String URL_ADMIN_BLOGS_BLOG = "/admin/blogs/{blogId}";
    String URL_ADMIN_BLOGS_BLOG_COMMENTS = "/admin/blogs/{blogId}/comments";
    String URL_ADMIN_COMMENTS = "/admin/comments";
    String URL_ADMIN_COMMENTS_COMMENT = "/admin/comments/{commentId}";
}
