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

package employeeservice.config;

import employeeservice.data.DoctorRepository;
import employeeservice.data.TeamManagerRepository;
import employeeservice.service.doctor.DoctorService;
import employeeservice.service.doctor.DoctorServiceImpl;
import employeeservice.service.external.ClinicServiceFeignClient;
import employeeservice.service.external.Department;
import employeeservice.service.teammanager.TeamManagerService;
import employeeservice.service.teammanager.TeamManagerServiceImpl;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.Validator;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class IntegrationTestConfig {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TeamManagerRepository teamManagerRepository;

    @Autowired
    private CircuitBreaker circuitBreaker;

    @Autowired
    private Validator validator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @Primary
    public DoctorService doctorService() {
        return new DoctorServiceImpl(doctorRepository, clinicServiceFeignClient(),
                passwordEncoder, validator, circuitBreaker);
    }

    @Bean
    @Primary
    public TeamManagerService teamManagerService() {
        return new TeamManagerServiceImpl(teamManagerRepository, doctorRepository,
                clinicServiceFeignClient(), passwordEncoder, validator, circuitBreaker);
    }

    @Bean
    public ClinicServiceFeignClient clinicServiceFeignClient() {
        Department firstDepartment = new Department(1L);
        Department secondDepartment = new Department(2L);

        ClinicServiceFeignClient feignClient = mock(ClinicServiceFeignClient.class);
        when(feignClient.findDepartmentById(1L)).thenReturn(Optional.of(firstDepartment));
        when(feignClient.findDepartmentById(2L)).thenReturn(Optional.of(secondDepartment));

        return feignClient;
    }
}
