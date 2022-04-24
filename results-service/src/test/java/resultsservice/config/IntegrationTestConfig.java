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

package resultsservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import resultsservice.data.ResultsRepository;
import resultsservice.service.external.client.Client;
import resultsservice.service.external.client.ClientServiceFeignClient;
import resultsservice.service.external.employee.EmployeeServiceFeignClient;
import resultsservice.service.external.employee.Doctor;
import resultsservice.service.external.registration.Duty;
import resultsservice.service.external.registration.RegistrationServiceFeignClient;
import resultsservice.service.result.ResultService;
import resultsservice.service.result.ResultServiceImpl;

import javax.validation.Validator;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class IntegrationTestConfig {

    @Autowired
    private ResultsRepository resultsRepository;

    @Autowired
    private CircuitBreaker circuitBreaker;

    @Autowired
    private Validator validator;

    @Bean
    @Primary
    public ResultService resultService() {
        return new ResultServiceImpl(resultsRepository,
                clientServiceFeignClient(), employeeServiceFeignClient(),
                registrationServiceFeignClient(), validator, circuitBreaker);
    }

    @Bean
    public ClientServiceFeignClient clientServiceFeignClient() {
        Client firstClient = Client.builder()
                .withId(1L)
                .withEmail("emma@gmail.com")
                .withName("Emma")
                .build();

        Client secondClient = Client.builder()
                .withId(2L)
                .withEmail("jain@gmail.com")
                .withName("Jain")
                .build();

        ClientServiceFeignClient feignClient = mock(ClientServiceFeignClient.class);
        when(feignClient.findClientById(1L)).thenReturn((firstClient));
        when(feignClient.findClientById(2L)).thenReturn((secondClient));

        return feignClient;
    }

    @Bean
    public EmployeeServiceFeignClient employeeServiceFeignClient() {
        Doctor firstDoctor = Doctor.builder()
                .withId(1L)
                .withEmail("mark@gmail.com")
                .withName("Mark")
                .withSpecialty("Surgery")
                .build();

        Doctor secondDoctor = Doctor.builder()
                .withId(2L)
                .withEmail("robert@gmail.com")
                .withName("Robert")
                .withSpecialty("Surgery")
                .build();

        EmployeeServiceFeignClient feignClient = mock(EmployeeServiceFeignClient.class);
        when(feignClient.findDoctorById(1L)).thenReturn((firstDoctor));
        when(feignClient.findDoctorById(2L)).thenReturn((secondDoctor));

        return feignClient;
    }

    @Bean
    public RegistrationServiceFeignClient registrationServiceFeignClient() {
        Duty firstDuty = Duty.builder()
                .withId(1L)
                .withName("Duty1")
                .withPrice(BigDecimal.TEN)
                .build();

        Duty secondDuty = Duty.builder()
                .withId(2L)
                .withName("Duty2")
                .withPrice(BigDecimal.TEN)
                .build();

        RegistrationServiceFeignClient feignClient = mock(RegistrationServiceFeignClient.class);
        when(feignClient.findDutyById(1L)).thenReturn((firstDuty));
        when(feignClient.findDutyById(2L)).thenReturn((secondDuty));

        return feignClient;
    }
}
