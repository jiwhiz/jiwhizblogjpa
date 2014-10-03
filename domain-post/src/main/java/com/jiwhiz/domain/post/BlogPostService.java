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
package com.jiwhiz.domain.post;

import com.jiwhiz.domain.account.UserAccount;

/**
 * Domain Service Interface for BlogPost.
 * 
 * @author Yuan Ji
 * 
 */
public interface BlogPostService {
    
    /**
     * Create a blog post.
     * 
     * @param author
     * @param title
     * @param content
     * @param tagString
     * @return new BlogPost object with author as login user.
     */
    BlogPost createPost(UserAccount author, String title, String content, String tagString);

}
