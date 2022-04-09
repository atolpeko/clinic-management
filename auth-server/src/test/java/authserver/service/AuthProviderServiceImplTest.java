/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package authserver.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("category.UnitTest")
public class AuthProviderServiceImplTest {
    private static UserDetailsService userDetailsService;
    private static PasswordEncoder encoder;

    private static BasicUser user;
    private static Employee employee;

    private AuthProviderServiceImpl authProviderService;

    @BeforeAll
    public static void setUpMocks() {
        userDetailsService = mock(UserDetailsServiceImpl.class);

        encoder = mock(PasswordEncoder.class);
        when(encoder.encode(anyString())).then(returnsFirstArg());
        when(encoder.matches(anyString(), anyString())).then(invocation -> {
            String rawPassword = invocation.getArgument(0);
            String encodedPassword = invocation.getArgument(1);
            return rawPassword.equals(encodedPassword);
        });
    }

    @BeforeAll
    public static void createUser() {
        user = new BasicUser();
        user.setId(1L);
        user.setLogin("user@gmail.com");
        user.setPassword("12345678");
    }

    @BeforeAll
    public static void createEmployee() {
        employee = new Employee();
        employee.setId(2L);
        employee.setLogin("emplayee@gmail.com");
        employee.setPassword("12345678");
        employee.setRole(AbstractUser.Role.TEAM_MANAGER);
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(userDetailsService);
        authProviderService = new AuthProviderServiceImpl(userDetailsService, encoder);
    }

    @Test
    public void shouldAuthenticateBasicUser() {
        when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);
        Authentication userAuth = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword(), user.getAuthorities()
        );
        userAuth.setAuthenticated(false);

        Authentication authentication = authProviderService.authenticate(userAuth);
        assertThat(authentication.isAuthenticated(), equalTo(true));
    }

    @Test
    public void shouldAuthenticateEmployee() {
        when(userDetailsService.loadUserByUsername(employee.getUsername())).thenReturn(employee);
        Authentication employeeAuth = new UsernamePasswordAuthenticationToken(
                employee.getUsername(), employee.getPassword(), employee.getAuthorities()
        );
        employeeAuth.setAuthenticated(false);

        Authentication authentication = authProviderService.authenticate(employeeAuth);
        assertThat(authentication.isAuthenticated(), equalTo(true));
    }
}
