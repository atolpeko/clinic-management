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

package registrationservice.service.registration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import registrationservice.data.RegistrationRepository;
import registrationservice.service.duty.Duty;
import registrationservice.service.exception.IllegalModificationException;
import registrationservice.service.external.Client;
import registrationservice.service.external.Doctor;

import javax.validation.Validator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("category.UnitTest")
public class RegistrationServiceImplTest {
    private static RegistrationRepository registrationRepository;
    private static Validator validator;
    private static CircuitBreaker circuitBreaker;

    private static Registration registration;

    private RegistrationServiceImpl registrationService;

    @BeforeAll
    public static void setUpMocks() {
        registrationRepository = mock(RegistrationRepository.class);
        validator = mock(Validator.class);

        circuitBreaker = mock(CircuitBreaker.class);
        when(circuitBreaker.decorateSupplier(any())).then(returnsFirstArg());
        when(circuitBreaker.decorateRunnable(any())).then(returnsFirstArg());
    }

    @BeforeAll
    public static void createRegistration() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);

        Client client = new Client();
        client.setId(1L);

        Duty duty = new Duty();
        duty.setId(1L);

        registration = new Registration();
        registration.setDate(LocalDateTime.now());
        registration.setDoctor(doctor);
        registration.setClient(client);
        registration.setDuty(duty);
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(registrationRepository, validator);
        registrationService = new RegistrationServiceImpl(registrationRepository, validator, circuitBreaker);
    }

    @Test
    public void shouldReturnRegistrationByIdWhenContainsIt() {
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));

        Registration saved = registrationService.findById(1).orElseThrow();
        assertThat(saved, is(equalTo(registration)));
    }

    @Test
    public void shouldReturnListOfRegistrationsWhenContainsMultipleRegistrations() {
        List<Registration> registrations = List.of(registration, registration, registration);
        when(registrationRepository.findAll()).thenReturn(registrations);

        List<Registration> saved = registrationService.findAll();
        assertThat(saved, is(equalTo(registrations)));
    }

    @Test
    public void shouldReturnRegistrationByClientIdWhenContainsRegistration() {
        List<Registration> registrations = List.of(registration, registration, registration);
        when(registrationRepository.findAllByClientId(1)).thenReturn(registrations);

        List<Registration> saved = registrationService.findAllByClientId(1);
        assertThat(saved, is(equalTo(registrations)));
    }

    @Test
    public void shouldReturnRegistrationByDoctorIdWhenContainsRegistration() {
        List<Registration> registrations = List.of(registration, registration, registration);
        when(registrationRepository.findAllByDoctorId(1)).thenReturn(registrations);

        List<Registration> saved = registrationService.findAllByDoctorId(1);
        assertThat(saved, is(equalTo(registrations)));
    }

    @Test
    public void shouldSaveRegistrationWhenRegistrationIsValid() {
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);
        when(validator.validate(any(Registration.class))).thenReturn(Collections.emptySet());

        Registration saved = registrationService.save(registration);
        assertThat(saved, equalTo(registration));
    }

    @Test
    public void shouldThrowExceptionWhenRegistrationIsInvalid() {
        when(validator.validate(any(Registration.class))).thenThrow(IllegalModificationException.class);
        assertThrows(IllegalModificationException.class, () -> registrationService.save(new Registration()));
    }

    @Test
    public void shouldNotContainRegistrationWhenDeletesThisRegistration() {
        when(registrationRepository.findById(any(Long.class))).thenReturn(Optional.of(registration));
        doAnswer(invocation -> when(registrationRepository.findById(1L)).thenReturn(Optional.empty()))
                .when(registrationRepository).deleteById(1L);

        registrationService.deleteById(1);

        Optional<Registration> deletedRegistration = registrationService.findById(1);
        assertThat(deletedRegistration, is(Optional.empty()));
    }
}
