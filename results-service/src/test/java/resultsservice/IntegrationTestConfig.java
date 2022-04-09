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

package resultsservice;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import resultsservice.data.ResultsRepository;
import resultsservice.service.external.client.Client;
import resultsservice.service.external.client.ClientServiceFeignClient;
import resultsservice.service.external.clinic.ClinicServiceFeignClient;
import resultsservice.service.external.clinic.Doctor;
import resultsservice.service.external.registration.Duty;
import resultsservice.service.external.registration.RegistrationServiceFeignClient;
import resultsservice.service.result.ResultService;
import resultsservice.service.result.ResultServiceImpl;

import javax.validation.Validator;

import java.math.BigDecimal;
import java.util.Optional;

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
        return new ResultServiceImpl(resultsRepository, validator, circuitBreaker,
                clientServiceFeignClient(), clinicServiceFeignClient(), registrationServiceFeignClient());
    }

    @Bean
    public ClientServiceFeignClient clientServiceFeignClient() {
        Client firstClient = new Client(1L, "emma@gmail.com", "Emma");
        Client secondClient = new Client(2L, "jain@gmail.com", "Jain");

        ClientServiceFeignClient feignClient = mock(ClientServiceFeignClient.class);
        when(feignClient.findClientById(1L)).thenReturn(Optional.of(firstClient));
        when(feignClient.findClientById(2L)).thenReturn(Optional.of(secondClient));

        return feignClient;
    }

    @Bean
    public ClinicServiceFeignClient clinicServiceFeignClient() {
        Doctor firstDoctor = new Doctor(1L, "mark@gmail.com", "Mark", "Surgery");
        Doctor secondDoctor = new Doctor(2L, "robert@gmail.com", "Robert", "Surgery");

        ClinicServiceFeignClient feignClient = mock(ClinicServiceFeignClient.class);
        when(feignClient.findDoctorById(1L)).thenReturn(Optional.of(firstDoctor));
        when(feignClient.findDoctorById(2L)).thenReturn(Optional.of(secondDoctor));

        return feignClient;
    }

    @Bean
    public RegistrationServiceFeignClient registrationServiceFeignClient() {
        Duty firstDuty = new Duty(1L, "Duty1", BigDecimal.TEN);
        Duty secondDuty = new Duty(2L, "Duty2", BigDecimal.TEN);

        RegistrationServiceFeignClient feignClient = mock(RegistrationServiceFeignClient.class);
        when(feignClient.findDutyById(1L)).thenReturn(Optional.of(firstDuty));
        when(feignClient.findDutyById(2L)).thenReturn(Optional.of(secondDuty));

        return feignClient;
    }
}
