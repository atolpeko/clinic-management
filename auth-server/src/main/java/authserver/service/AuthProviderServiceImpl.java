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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthProviderServiceImpl implements AuthenticationProvider {
    private static final Logger logger = LogManager.getLogger(AuthProviderServiceImpl.class);

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthProviderServiceImpl(UserDetailsService userDetailsService,
                                   PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            UserDetails user = userDetailsService.loadUserByUsername(authentication.getName());
            String password = authentication.getCredentials().toString();
            return checkCredentials(user, password);
        } catch (UsernameNotFoundException e) {
            logger.info("Cannot authenticate: " + e.getMessage());
            throw e;
        } catch (IllegalStateException e) {
            String msg = "Cannot authenticate: " + e.getMessage();
            logger.info(msg);
            throw new AuthenticationServiceException(msg);
        }
    }

    private Authentication checkCredentials(UserDetails user, String password) {
        if (passwordEncoder.matches(password, user.getPassword())) {
            logger.info("User authenticated: " + user.getUsername());
            return new UsernamePasswordAuthenticationToken(
                    user.getUsername(), user.getPassword(), user.getAuthorities()
            );
        } else {
            String msg = "Cannot authenticate " + user.getUsername() + ": Bad credentials";
            logger.info(msg);
            throw new BadCredentialsException(msg);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class .isAssignableFrom(authentication);
    }
}
