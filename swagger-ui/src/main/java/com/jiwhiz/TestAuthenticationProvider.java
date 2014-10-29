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

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author Yuan Ji
 *
 */
@Slf4j
public class TestAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    
    public TestAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("Try to authenticate "+authentication.getName());
        
        TestAuthenticationToken authToken = (TestAuthenticationToken)authentication;
        UserDetails user = userDetailsService.loadUserByUsername(authToken.getName());
        log.debug("Found user "+user.getUsername()+" with authorities "+user.getAuthorities());
        Authentication authenticationToken = new TestAuthenticationToken(user.getUsername(), user.getAuthorities());
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TestAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
