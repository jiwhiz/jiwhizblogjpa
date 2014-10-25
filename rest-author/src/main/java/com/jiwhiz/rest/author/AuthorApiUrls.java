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

/**
 * Urls for author REST API endpoints.
 * 
 * @author Yuan Ji
 *
 */
public interface AuthorApiUrls {
    String URL_AUTHOR = "/author";
    String URL_AUTHOR_BLOGS = "/author/blogs";
    String URL_AUTHOR_BLOGS_BLOG = "/author/blogs/{blogId}";
    String URL_AUTHOR_BLOGS_BLOG_COMMENTS = "/author/blogs/{blogId}/comments";
    String URL_AUTHOR_BLOGS_BLOG_COMMENTS_COMMENT = "/author/blogs/{blogId}/comments/{commentId}";
}
