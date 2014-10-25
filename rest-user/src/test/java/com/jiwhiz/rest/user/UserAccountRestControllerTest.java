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
import static com.jiwhiz.rest.user.UserApiUrls.URL_USER;
import static com.jiwhiz.rest.user.UserApiUrls.URL_USER_COMMENTS;
import static com.jiwhiz.rest.user.UserApiUrls.URL_USER_PROFILE;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;

import com.jiwhiz.config.TestUtils;
import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.account.UserAccountRepository;
import com.jiwhiz.domain.account.UserSocialConnection;
import com.jiwhiz.domain.account.UserSocialConnectionRepository;
import com.jiwhiz.rest.AbstractRestControllerTest;

/**
 * @author Yuan Ji
 */
public class UserAccountRestControllerTest extends AbstractRestControllerTest {
    @Inject
    UserAccountRepository userAccountRepositoryMock;
    
    @Inject
    UserSocialConnectionRepository userSocialConnectionRepositoryMock;
    
    @Before
    public void setup() {
        Mockito.reset(userSocialConnectionRepositoryMock);
        super.setup();
    }

    @Test
    public void getCurrentUserAccount_ShouldReturnCurrentUserAccountWithSocialConnections() throws Exception {
        UserAccount user = getTestLoggedInUserWithAdminRole();
        UserSocialConnection socialConnection1 = new UserSocialConnection();
        socialConnection1.setProviderId("google");
        socialConnection1.setProviderUserId("googleUser");
        UserSocialConnection socialConnection2 = new UserSocialConnection();
        socialConnection2.setProviderId("facebook");
        socialConnection2.setProviderUserId("facebookUser");
        
        when(userAccountServiceMock.getCurrentUser()).thenReturn(user);
        when(userSocialConnectionRepositoryMock.findByUserId(user.getUserId())).thenReturn(Arrays.asList(socialConnection1, socialConnection2));

        mockMvc.perform(get(API_ROOT + URL_USER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$.id", is (ADMIN_USER_ID)))
                .andExpect(jsonPath("$.userId", is (ADMIN_USER_USERNAME)))
                .andExpect(jsonPath("$.displayName", is (USER_DISPLAY_NAME)))
                .andExpect(jsonPath("$.admin", is(true)))
                .andExpect(jsonPath("$.author", is(false)))
                .andExpect(jsonPath("$.socialConnections.google.providerId", is("google")))
                .andExpect(jsonPath("$.socialConnections.google.providerUserId", is("googleUser")))
                .andExpect(jsonPath("$.socialConnections.facebook.providerId", is("facebook")))
                .andExpect(jsonPath("$.socialConnections.facebook.providerUserId", is("facebookUser")))
                //validate links
                .andExpect(jsonPath("$._links.self.href", endsWith(URL_USER)))
                .andExpect(jsonPath("$._links.profile.href", endsWith(URL_USER_PROFILE)))
                .andExpect(jsonPath("$._links.comments.templated", is(true)))
                .andExpect(jsonPath("$._links.comments.href", endsWith(URL_USER_COMMENTS+"{?page,size,sort}")))
                ;
    } 
    
    @Test
    public void getCurrentUser_ShouldReturn401IfNotAuthenticated() throws Exception {
        when(userAccountServiceMock.getCurrentUser()).thenReturn(null);
        mockMvc.perform(get(API_ROOT + URL_USER))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""))
                ;
    } 

    @Test
    public void patchUserProfile_ShouldUpdateUserProfile() throws Exception {
        UserAccount user = getTestLoggedInUserWithAdminRole();
        when(userAccountServiceMock.getCurrentUser()).thenReturn(user);
        Map<String, String> testUpdates = new HashMap<String, String>();
        testUpdates.put("displayName", "New Name");
        
        mockMvc.perform
                (patch(API_ROOT + URL_USER_PROFILE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(testUpdates))
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                ;
        
        verify(userAccountRepositoryMock, times(1)).save(user);
    } 
    
    @Test
    public void patchUserProfile_ShouldReturn401IfNotAuthenticated() throws Exception {
        when(userAccountServiceMock.getCurrentUser()).thenReturn(null);
        Map<String, String> testUpdates = new HashMap<String, String>();
        testUpdates.put("displayName", "New Name");
        
        mockMvc.perform
                (patch(API_ROOT + URL_USER_PROFILE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.convertObjectToJsonBytes(testUpdates))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(""))
                ;
    } 
}
