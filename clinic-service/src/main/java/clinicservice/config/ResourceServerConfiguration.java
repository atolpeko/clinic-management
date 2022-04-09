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

package clinicservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Value("${spring.security.oauth2.jwt-key}")
    private String jwtKey;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(jwtKey);
        return converter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenStore(tokenStore());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .requestMatchers()
                .mvcMatchers("/**")
            .and()
            .authorizeRequests()
                .mvcMatchers(HttpMethod.POST, "/departments/**")
                    .hasAuthority("TOP_MANAGER")
                .mvcMatchers(HttpMethod.PATCH, "/departments/**")
                    .hasAuthority("TOP_MANAGER")
                .mvcMatchers(HttpMethod.DELETE, "/departments/**")
                    .hasAuthority("TOP_MANAGER")
                .mvcMatchers(HttpMethod.POST, "/facilities/**")
                    .hasAuthority("TOP_MANAGER")
                .mvcMatchers(HttpMethod.PATCH, "/facilities/**")
                    .hasAuthority("TOP_MANAGER")
                .mvcMatchers(HttpMethod.DELETE, "/facilities/**")
                    .hasAuthority("TOP_MANAGER")
                .mvcMatchers(HttpMethod.POST, "/doctors/**")
                    .hasAnyAuthority("TEAM_MANAGER", "TOP_MANAGER")
                .mvcMatchers(HttpMethod.PATCH, "/doctors/**")
                    .hasAnyAuthority("TEAM_MANAGER", "TOP_MANAGER")
                .mvcMatchers(HttpMethod.DELETE, "/doctors/**")
                    .hasAnyAuthority("TEAM_MANAGER", "TOP_MANAGER")
                .mvcMatchers(HttpMethod.DELETE, "/team-managers/**")
                    .hasAuthority("TOP_MANAGER")
                .mvcMatchers("/team-managers/**")
                    .hasAnyAuthority("TEAM_MANAGER", "TOP_MANAGER")
                .mvcMatchers("/top-managers/**")
                    .hasAuthority("TOP_MANAGER")
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER);
    }
}
