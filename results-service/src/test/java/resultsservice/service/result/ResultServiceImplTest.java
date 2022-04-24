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

package resultsservice.service.result;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import resultsservice.data.ResultsRepository;
import resultsservice.service.exception.IllegalModificationException;
import resultsservice.service.external.client.Client;
import resultsservice.service.external.client.ClientServiceFeignClient;
import resultsservice.service.external.employee.EmployeeServiceFeignClient;
import resultsservice.service.external.employee.Doctor;
import resultsservice.service.external.registration.Duty;
import resultsservice.service.external.registration.RegistrationServiceFeignClient;

import javax.validation.Validator;

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
public class ResultServiceImplTest {
    private static ResultsRepository resultsRepository;
    private static Validator validator;
    private static CircuitBreaker circuitBreaker;
    private static ClientServiceFeignClient clientService;
    private static EmployeeServiceFeignClient employeeService;
    private static RegistrationServiceFeignClient registrationService;

    private static Result result;
    private static Result updatedResult;

    private ResultServiceImpl resultService;

    @BeforeAll
    public static void setUpMocks() {
        resultsRepository = mock(ResultsRepository.class);
        validator = mock(Validator.class);

        circuitBreaker = mock(CircuitBreaker.class);
        when(circuitBreaker.decorateSupplier(any())).then(returnsFirstArg());
        when(circuitBreaker.decorateRunnable(any())).then(returnsFirstArg());

        clientService = mock(ClientServiceFeignClient.class);
        Client client = Client.builder().withId(1L).build();
        when(clientService.findClientById(any(Long.class))).thenReturn((client));

        employeeService = mock(EmployeeServiceFeignClient.class);
        Doctor doctor = Doctor.builder().withId(1L).build();
        when(employeeService.findDoctorById(any(Long.class))).thenReturn((doctor));

        registrationService = mock(RegistrationServiceFeignClient.class);
        Duty duty = Duty.builder().withId(1L).build();
        when(registrationService.findDutyById(any(Long.class))).thenReturn((duty));
    }

    @BeforeAll
    public static void createResult() {
        Client client = Client.builder().withId(1L).build();
        Doctor doctor = Doctor.builder().withId(1L).build();
        Duty duty = Duty.builder().withId(1L).build();

        result = Result.builder()
                .withId(1L)
                .withData("Data")
                .withClient(client)
                .withDoctor(doctor)
                .withDuty(duty)
                .build();
    }

    @BeforeAll
    public static void createUpdatedResult() {
        Client client = Client.builder().withId(2L).build();
        Doctor doctor = Doctor.builder().withId(2L).build();
        Duty duty = Duty.builder().withId(2L).build();

        updatedResult = Result.builder()
                .withId(1L)
                .withData("Data2")
                .withClient(client)
                .withDoctor(doctor)
                .withDuty(duty)
                .build();
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(resultsRepository, validator);
        resultService = new ResultServiceImpl(resultsRepository, clientService, employeeService,
                registrationService, validator, circuitBreaker);
    }

    @Test
    public void shouldReturnResultByIdWhenContainsIt() {
        when(resultsRepository.findById(1L)).thenReturn(Optional.of(result));

        Result saved = resultService.findById(1).orElseThrow();
        assertThat(saved, is(equalTo(result)));
    }

    @Test
    public void shouldReturnListOfResultsWhenContainsMultipleResults() {
        List<Result> results = List.of(result, result, result);
        when(resultsRepository.findAll()).thenReturn(results);

        List<Result> saved = resultService.findAll();
        assertThat(saved, is(equalTo(results)));
    }

    @Test
    public void shouldReturnListOfResultsByClientIdWhenContainsMultipleResults() {
        List<Result> results = List.of(result, result, result);
        when(resultsRepository.findAllByClientId(1)).thenReturn(results);

        List<Result> saved = resultService.findAllByClientId(1);
        assertThat(saved, is(equalTo(results)));
    }

    @Test
    public void shouldCount5ResultsWhenContains5Results() {
        when(resultsRepository.count()).thenReturn(5L);

        long count = resultService.count();
        assertThat(count, is(equalTo(5L)));
    }

    @Test
    public void shouldSaveResultWhenResultIsValid() {
        when(resultsRepository.save(any(Result.class))).thenReturn(result);
        when(validator.validate(any(Result.class))).thenReturn(Collections.emptySet());

        Result saved = resultService.save(result);
        assertThat(saved, equalTo(result));
    }

    @Test
    public void shouldThrowExceptionWhenResultIsInvalid() {
        when(validator.validate(any(Result.class))).thenThrow(IllegalModificationException.class);
        assertThrows(IllegalModificationException.class, () -> resultService.save(new Result()));
    }

    @Test
    public void shouldUpdateResultWhenResultIsValid() {
        when(resultsRepository.findById(1L)).thenReturn(Optional.of(result));
        when(resultsRepository.save(any(Result.class))).thenReturn(updatedResult);
        when(validator.validate(any(Result.class))).thenReturn(Collections.emptySet());

        Result updated = resultService.update(updatedResult);
        assertThat(updated, equalTo(updatedResult));
    }

    @Test
    public void shouldNotContainResultWhenDeletesThisResult() {
        when(resultsRepository.findById(any(Long.class))).thenReturn(Optional.of(result));
        doAnswer(invocation -> when(resultsRepository.findById(1L)).thenReturn(Optional.empty()))
                .when(resultsRepository).deleteById(1L);

        resultService.deleteById(1);

        Optional<Result> deleted = resultService.findById(1);
        assertThat(deleted, is(Optional.empty()));
    }
}
