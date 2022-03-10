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

package registrationservice;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.time.Duration;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class RegistrationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RegistrationServiceApplication.class, args);
    }

    @Bean
    public CircuitBreaker circuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig
                .custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .slowCallRateThreshold(70.0f)
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .recordExceptions(InvalidDataAccessResourceUsageException.class)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        return registry.circuitBreaker("circuitBreaker");
    }
    
    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        FilterRegistrationBean<ForwardedHeaderFilter> filter = new FilterRegistrationBean<>();
        filter.setFilter(new ForwardedHeaderFilter());
        filter.setOrder(0);
        return filter;
    }
}
