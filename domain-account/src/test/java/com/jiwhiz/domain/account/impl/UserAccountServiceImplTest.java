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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.social.UserIdSource;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.UserProfile;

import com.jiwhiz.domain.account.UserAccount;
import com.jiwhiz.domain.account.UserAccountRepository;
import com.jiwhiz.domain.account.UserRoleType;


/**
 * 
 * @author Yuan Ji
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class UserAccountServiceImplTest {
    @Mock
    UserAccountRepository accountRepositoryMock;
    
    @Mock
    UserIdSource userIdSourceMock;
    
    UserAccountServiceImpl serviceToTest;
    
    @Before
    public void setup() {
        serviceToTest = new UserAccountServiceImpl(accountRepositoryMock, userIdSourceMock);
    }
        
    @Test
    public void createUserAccount_ShouldCreateNewUser() {
        ConnectionData data = new ConnectionData("google", "jiwhiz", "Yuan Ji", "https://plus.google.com/+YuanJi", 
                "https://someurl", null, null, null, null);
        
        when(accountRepositoryMock.count()).thenReturn(new Long(1l));
        when(accountRepositoryMock.save(isA(UserAccount.class))).thenAnswer( new Answer<UserAccount>() {
            @Override
            public UserAccount answer(InvocationOnMock invocation) throws Throwable {
                UserAccount account = (UserAccount)invocation.getArguments()[0];
                account.setId("user123");
                return account;
            }
            
        });
        
        UserAccount newAccount = serviceToTest.createUserAccount(data, UserProfile.EMPTY);
        
        assertEquals("Yuan Ji", newAccount.getDisplayName());
        assertEquals("https://plus.google.com/+YuanJi", newAccount.getWebSite());
        assertFalse(newAccount.isAdmin());
        assertFalse(newAccount.isAuthor());
        assertFalse(newAccount.isTrustedAccount());
        
        verify(accountRepositoryMock, times(1)).save(isA(UserAccount.class));
    }
    
    @Test
    public void createUserAccount_ShouldCreateAdminForTheFirstAccount() {
        ConnectionData data = new ConnectionData("google", "jiwhiz", "Yuan Ji", "https://plus.google.com/+YuanJi", 
                "https://someurl", null, null, null, null);
        
        when(accountRepositoryMock.count()).thenReturn(new Long(0l));
        when(accountRepositoryMock.save(isA(UserAccount.class))).thenAnswer( new Answer<UserAccount>() {
            @Override
            public UserAccount answer(InvocationOnMock invocation) throws Throwable {
                UserAccount account = (UserAccount)invocation.getArguments()[0];
                account.setId("user123");
                return account;
            }
            
        });
        
        UserAccount newAccount = serviceToTest.createUserAccount(data, UserProfile.EMPTY);
        
        assertEquals("Yuan Ji", newAccount.getDisplayName());
        assertEquals("https://plus.google.com/+YuanJi", newAccount.getWebSite());
        assertTrue(newAccount.isAdmin());
        assertTrue(newAccount.isAuthor());
        assertTrue(newAccount.isTrustedAccount());
        
        verify(accountRepositoryMock, times(1)).save(isA(UserAccount.class));
    }
    
    @Test
    public void addRole_ShouldAddAuthorRole() {
        UserAccount testAccount = new UserAccount();
        testAccount.setId("user123");
        testAccount.getRoles().add(UserRoleType.ROLE_USER);
        
        when(accountRepositoryMock.findOne("user123")).thenReturn(testAccount);
        when(accountRepositoryMock.save(isA(UserAccount.class))).thenReturn(testAccount);
        
        UserAccount newAccount = serviceToTest.addRole("user123", UserRoleType.ROLE_AUTHOR);
        
        assertEquals(2, newAccount.getRoles().size());
        assertTrue(newAccount.isAuthor());
        
    }
    
    @Test
    public void addRole_ShouldNotAddAuthorRoleIfAlreadyAuthor() {
        UserAccount testAccount = new UserAccount();
        testAccount.setId("user123");
        testAccount.getRoles().add(UserRoleType.ROLE_AUTHOR);
        
        when(accountRepositoryMock.findOne("user123")).thenReturn(testAccount);
        when(accountRepositoryMock.save(isA(UserAccount.class))).thenReturn(testAccount);
        
        UserAccount newAccount = serviceToTest.addRole("user123", UserRoleType.ROLE_AUTHOR);
        
        assertEquals(1, newAccount.getRoles().size());
        assertTrue(newAccount.isAuthor());
        
    }
    
    @Test
    public void removeRole_ShouldRemoveAuthorRole() {
        UserAccount testAccount = new UserAccount();
        testAccount.setId("user123");
        testAccount.getRoles().add(UserRoleType.ROLE_USER);
        testAccount.getRoles().add(UserRoleType.ROLE_AUTHOR);
        
        when(accountRepositoryMock.findOne("user123")).thenReturn(testAccount);
        when(accountRepositoryMock.save(isA(UserAccount.class))).thenReturn(testAccount);
        
        UserAccount newAccount = serviceToTest.removeRole("user123", UserRoleType.ROLE_AUTHOR);
        
        assertEquals(1, newAccount.getRoles().size());
        assertFalse(newAccount.isAuthor());
        
    }
    
    @Test
    public void removeRole_ShouldRemoveAddAuthorRoleIfNotAuthor() {
        UserAccount testAccount = new UserAccount();
        testAccount.setId("user123");
        testAccount.getRoles().add(UserRoleType.ROLE_USER);
        
        when(accountRepositoryMock.findOne("user123")).thenReturn(testAccount);
        when(accountRepositoryMock.save(isA(UserAccount.class))).thenReturn(testAccount);
        
        UserAccount newAccount = serviceToTest.removeRole("user123", UserRoleType.ROLE_AUTHOR);
        
        assertEquals(1, newAccount.getRoles().size());
        assertFalse(newAccount.isAuthor());
        
    }
}


