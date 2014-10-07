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
package com.jiwhiz.domain.account.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.UserIdSource;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.UserProfile;

import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.account.UserAccountRepository;
import com.jiwhiz.domain.account.UserAccountService;
import com.jiwhiz.domain.account.UserRoleType;

/**
 * Implementation for UserAccountService.
 * 
 * @author Yuan Ji
 * 
 */
public class UserAccountServiceImpl implements UserAccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountServiceImpl.class);

    private final UserAccountRepository accountRepository;
    private final UserIdSource userIdSource;

    public UserAccountServiceImpl(UserAccountRepository accountRepository, UserIdSource userIdSource) {
        this.accountRepository = accountRepository;
        this.userIdSource = userIdSource;
    }

    @Override
    public UserAccount createUserAccount(ConnectionData data, UserProfile profile) {
        UserAccount account = new UserAccount();
        
        if (accountRepository.count() == 0l) {
            LOGGER.info("First user, set as admin and author.");
            account.getRoles().add(UserRoleType.ROLE_AUTHOR);
            account.getRoles().add(UserRoleType.ROLE_ADMIN);
            account.setTrustedAccount(true);
        }
        
        account.setEmail(profile.getEmail());
        account.setDisplayName(data.getDisplayName());
        account.setImageUrl(data.getImageUrl());
        account.setWebSite(data.getProfileUrl());
        account.getRoles().add(UserRoleType.ROLE_USER);
        account = accountRepository.save(account);
        LOGGER.info(String.format("A new user is created (userId='%s') for '%s'.", account.getUserId(),
                account.getDisplayName()));
        
        
        return account;
    }

    @Override
    public UserAccount addRole(String userId, UserRoleType role) throws UsernameNotFoundException {
        UserAccount account = loadUserByUserId(userId);
        if (!account.getRoles().contains(role)) {
            account.getRoles().add(role);
        }
        return this.accountRepository.save(account);
    }
    
    @Override
    public UserAccount removeRole(String userId, UserRoleType role) throws UsernameNotFoundException {
        UserAccount account = loadUserByUserId(userId);
        if (account.getRoles().contains(role)) {
            account.getRoles().remove(role);
        }
        return this.accountRepository.save(account);
    }

    @Override
    public UserAccount loadUserByUserId(String userId) throws UsernameNotFoundException {
        UserAccount account = accountRepository.findOne(userId);
        if (account == null) {
            throw new UsernameNotFoundException("Cannot find user by userId " + userId);
        }
        return account;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByUserId(username);
    }

    @Override
    public UserAccount getCurrentUser() {
        try {
            return accountRepository.findOne(userIdSource.getUserId());
        } catch (IllegalStateException ex) {
            //no logged in user, return null
            return null;
        }
    }
    
}
