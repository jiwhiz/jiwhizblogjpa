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

import static com.jiwhiz.rest.UtilConstants.API_ROOT;
import static com.jiwhiz.rest.user.UserApiUrls.URL_USER_COMMENTS_COMMENT;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import javax.inject.Inject;

import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mvc.BasicLinkBuilder;
import org.springframework.stereotype.Component;

import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.account.UserSocialConnection;
import com.jiwhiz.domain.account.UserSocialConnectionRepository;
import com.jiwhiz.domain.post.CommentPost;

/**
 * @author Yuan Ji
 */
@Component
public class UserAccountResourceAssembler implements ResourceAssembler<UserAccount, UserAccountResource> {
    
    private final UserSocialConnectionRepository userSocialConnectionRepository;
    private final PagedResourcesAssembler<CommentPost> assembler;
    
    @Inject
    public UserAccountResourceAssembler(
            UserSocialConnectionRepository userSocialConnectionRepository,
            PagedResourcesAssembler<CommentPost> assembler) {
        this.userSocialConnectionRepository = userSocialConnectionRepository;
        this.assembler = assembler;
    }
    
    @Override
    public UserAccountResource toResource(UserAccount userAccount) {
        UserAccountResource resource = new UserAccountResource(userAccount);
        List<UserSocialConnection> socialConnections = userSocialConnectionRepository.findByUserId(userAccount.getUserId());
        for (UserSocialConnection socialConnection : socialConnections) {
            resource.getSocialConnections().put(socialConnection.getProviderId(), socialConnection);
        }
        
        resource.add(linkTo(methodOn(UserAccountRestController.class).getCurrentUserAccount())
                .withSelfRel());
        resource.add(linkTo(methodOn(UserAccountRestController.class).patchUserProfile(null))
                .withRel(UserAccountResource.LINK_NAME_PROFILE));

        Link commentsLink = linkTo(methodOn(UserCommentRestController.class).getCurrentUserComments(null, null))
                .withRel(UserAccountResource.LINK_NAME_COMMENTS);
        resource.add(assembler.appendPaginationParameterTemplates(commentsLink));
        
        String baseUri = BasicLinkBuilder.linkToCurrentMapping().toString();
        Link commentLink = new Link(new UriTemplate(baseUri + API_ROOT + URL_USER_COMMENTS_COMMENT),
                UserAccountResource.LINK_NAME_COMMENT);
        resource.add(commentLink);
        
        

        return resource;
    }

}
