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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for BlogPost domain entity class.
 * 
 * @author Yuan Ji
 * 
 */
public class BlogPostTest {

    @Test
    public void parseAndSetTagString_ShouldParseTags() {
        String tagString1 = "  spring, spring-boot , spring-social  ";
        BlogPost blog = new BlogPost();
        blog.parseAndSetTagString(tagString1);
        assertEquals("spring,spring-boot,spring-social", blog.getTagString());
        assertEquals(3, blog.getTags().length);
        
        String tagString2 = "  spring-social, spring, spring  ";
        blog.parseAndSetTagString(tagString2);
        assertEquals("spring,spring-social", blog.getTagString());
        assertEquals(2, blog.getTags().length);
        
        String tagString3 = "  spring-social, spring, spring-social  ";
        blog.parseAndSetTagString(tagString3);
        assertEquals("spring,spring-social", blog.getTagString());
        assertEquals(2, blog.getTags().length);
        
        String tagString4 = " spring, spring-social, , , spring, ,spring-social  ";
        blog.parseAndSetTagString(tagString4);
        assertEquals("spring,spring-social", blog.getTagString());
        assertEquals(2, blog.getTags().length);
    }
    
}
