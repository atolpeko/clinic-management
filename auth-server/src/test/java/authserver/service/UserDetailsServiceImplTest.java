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

import authserver.data.BasicUserRepository;
import authserver.data.EmployeeRepository;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("category.UnitTest")
public class UserDetailsServiceImplTest {
    private static BasicUserRepository userRepository;
    private static EmployeeRepository employeeRepository;
    private static CircuitBreaker circuitBreaker;

    private static BasicUser user;
    private static Employee employee;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeAll
    public static void setUpMocks() {
        userRepository = mock(BasicUserRepository.class);
        employeeRepository = mock(EmployeeRepository.class);

        circuitBreaker = mock(CircuitBreaker.class);
        when(circuitBreaker.decorateSupplier(any())).then(returnsFirstArg());
        when(circuitBreaker.decorateRunnable(any())).then(returnsFirstArg());
    }

    @BeforeAll
    public static void createUser() {
        user = BasicUser.builder()
                .withId(1L)
                .withLogin("user@gmail.com")
                .withPassword("12345678")
                .build();
    }

    @BeforeAll
    public static void createEmployee() {
        employee = Employee.builder()
                .withId(2L)
                .withLogin("employee@gmail.com")
                .withPassword("12345678")
                .withRole(AbstractUser.Role.TEAM_MANAGER)
                .build();
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(userRepository, employeeRepository);
        userDetailsService = new UserDetailsServiceImpl(userRepository, employeeRepository, circuitBreaker);
    }

    @Test
    public void shouldReturnUserByLoginWhenContainsIt() {
        String login = user.getLogin();
        when(userRepository.findByLogin(login)).thenReturn(Optional.of(user));

        UserDetails saved = userDetailsService.loadUserByUsername(login);
        assertThat(saved, is(equalTo(user)));
    }

    @Test
    public void shouldReturnEmployeeByLoginWhenContainsIt() {
        String login = employee.getLogin();
        when(employeeRepository.findByLogin(login)).thenReturn(Optional.of(employee));

        UserDetails saved = userDetailsService.loadUserByUsername(login);
        assertThat(saved, is(equalTo(employee)));
    }
}
