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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    private final BasicUserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public UserDetailsServiceImpl(BasicUserRepository userRepository,
                                  EmployeeRepository repository,
                                  CircuitBreaker circuitBreaker) {
        this.userRepository = userRepository;
        this.employeeRepository = repository;
        this.circuitBreaker = circuitBreaker;
    }

    /**
     * Locates the user based on the username.
     *
     * @param username the username identifying the user whose data is required
     *
     * @return a fully populated user record
     *
     * @throws UsernameNotFoundException if the user could not be found
     * @throws IllegalStateException if the user database is not available
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Optional<BasicUser> user = findUser(username);
            if (user.isPresent()) {
                return user.get();
            } else {
                return findEmployee(username).get();
            }
        } catch (NoSuchElementException e) {
            throw new UsernameNotFoundException("User not found: " + username, e);
        } catch (Exception e) {
            throw new IllegalStateException("User database unavailable", e);
        }
    }

    private Optional<BasicUser> findUser(String login) {
        Supplier<Optional<BasicUser>> findUser = () -> userRepository.findByLogin(login);
        return circuitBreaker.decorateSupplier(findUser).get();
    }

    private Optional<Employee> findEmployee(String login) {
        Supplier<Optional<Employee>> findEmployee = () -> employeeRepository.findByLogin(login);
        return circuitBreaker.decorateSupplier(findEmployee).get();
    }
}
