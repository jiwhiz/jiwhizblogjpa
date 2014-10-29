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
package com.jiwhiz;

import javax.inject.Inject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.security.AuthenticationNameUserIdSource;

import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.account.UserAccountRepository;
import com.jiwhiz.domain.account.UserAccountService;
import com.jiwhiz.domain.account.impl.UserAccountServiceImpl;
import com.jiwhiz.domain.post.BlogPost;
import com.jiwhiz.domain.post.BlogPostRepository;
import com.jiwhiz.domain.post.BlogPostService;
import com.jiwhiz.domain.post.CommentPost;
import com.jiwhiz.domain.post.CommentPostRepository;
import com.jiwhiz.domain.post.CommentPostService;
import com.jiwhiz.domain.post.impl.BlogPostServiceImpl;
import com.jiwhiz.domain.post.impl.CommentPostServiceImpl;
import com.jiwhiz.mail.CommentNotificationSender;
import com.jiwhiz.mail.ContactForm;
import com.jiwhiz.mail.ContactMessageSender;
import com.jiwhiz.mail.SystemMessageSender;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import com.wordnik.swagger.model.ApiInfo;

@Configuration
@EnableAutoConfiguration
@EnableSwagger
@EnableHypermediaSupport(type = { HypermediaType.HAL })
@ComponentScan(basePackages = {"com.jiwhiz.rest"})
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Import(SecurityConfig.class)
public class JiwhizBlogSwaggerUIApplication {
	@Inject
    private UserAccountRepository accountRepository;
    @Inject
    private BlogPostRepository blogPostRepository;
    @Inject
    private CommentPostRepository commentPostRepository;
    
    @Bean
    public UserAccountService userAccountService() {
        return new UserAccountServiceImpl(accountRepository, new AuthenticationNameUserIdSource());
    }

    @Bean
    public BlogPostService blogPostService() {
        return new BlogPostServiceImpl(blogPostRepository);
    }

    @Bean
    public CommentPostService commentPostService() {
        return new CommentPostServiceImpl(commentPostRepository);
    }
    
    @Bean
    public ContactMessageSender contactMessageSender() {
        return new ContactMessageSender() {
            @Override
            public void send(ContactForm contact) {
                System.out.println(String.format("Send email message to jiwhiz. From  %s: \" %s \"", contact.getName(),
                        contact.getMessage()));
            }
        };
    }

    @Bean
    public CommentNotificationSender commentNotificationSender() {
        return new CommentNotificationSender() {
            @Override
            public void send(UserAccount receivingUser, UserAccount commentUser, CommentPost comment, BlogPost blog) {
                System.out.println(String.format(
                        "Send email message to '%s': %s posted a comment to blog '%s': \" %s \"",
                        receivingUser.getDisplayName(), commentUser.getDisplayName(), blog.getTitle(),
                        comment.getContent()));
            }
        };
    }

    @Bean
    public SystemMessageSender systemMessageSender() {
        return new SystemMessageSender() {
            @Override
            public void sendNewUserRegistered(UserAccount user) {
                System.out.println(String.format(
                        "Send email message to admin: a new user '%s' was registered.", user.getDisplayName()));
            }

            @Override
            public void sendNewPostPublished(UserAccount author, BlogPost blog) {
                System.out.println(String.format("'%s' published a new blog '%s'", 
                        author.getDisplayName(), blog.getTitle()));                
            }

        };
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAware<String>() {
            public String getCurrentAuditor() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null || !authentication.isAuthenticated()) {
                  return null;
                }

                return authentication.getName();
            }
        };
    }
    
    private SpringSwaggerConfig springSwaggerConfig;
    
    @Inject
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig;
    }
    
    @Bean
    public SwaggerSpringMvcPlugin customImplementation(){
       return new SwaggerSpringMvcPlugin(springSwaggerConfig)
             .apiInfo(apiInfo())
             .includePatterns("/api/.*");
    }

    private ApiInfo apiInfo() {
    	ApiInfo apiInfo = new ApiInfo(
               "JiwhizBlog API",
               "Jiwhiz Blog RESTful API",
               "https://www.jiwhiz.com/about",
               "support@jiwhiz.com",
               "Apache 2.0",
               "http://www.apache.org/licenses/LICENSE-2.0.html"
         );
       return apiInfo;
     }
    
    public static void main(String[] args) throws Exception {
        SpringApplication.run(JiwhizBlogSwaggerUIApplication.class, args);
    }
}