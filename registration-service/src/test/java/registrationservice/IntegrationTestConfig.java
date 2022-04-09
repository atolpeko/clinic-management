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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import registrationservice.data.DutyRepository;
import registrationservice.data.RegistrationRepository;
import registrationservice.service.duty.DutyService;
import registrationservice.service.duty.DutyServiceImpl;
import registrationservice.service.external.client.Client;
import registrationservice.service.external.client.ClientServiceFeignClient;
import registrationservice.service.external.clinic.ClinicServiceFeignClient;
import registrationservice.service.external.clinic.Doctor;
import registrationservice.service.registration.RegistrationService;
import registrationservice.service.registration.RegistrationServiceImpl;

import javax.validation.Validator;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class IntegrationTestConfig {

    @Autowired
    private DutyRepository dutyRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private CircuitBreaker circuitBreaker;

    @Autowired
    private Validator validator;

    @Bean
    @Primary
    public RegistrationService registrationService() {
        return new RegistrationServiceImpl(registrationRepository, validator, circuitBreaker,
                clinicServiceFeignClient(), clientServiceFeignClient());
    }

    @Bean
    @Primary
    public DutyService dutyService() {
        return new DutyServiceImpl(dutyRepository, registrationRepository, validator,
                clinicServiceFeignClient(), circuitBreaker);
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
}
