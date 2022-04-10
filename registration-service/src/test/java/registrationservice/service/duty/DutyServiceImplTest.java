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

package registrationservice.service.duty;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import registrationservice.data.DutyRepository;
import registrationservice.data.RegistrationRepository;
import registrationservice.service.exception.IllegalModificationException;
import registrationservice.service.external.clinic.ClinicServiceFeignClient;

import javax.validation.Validator;

import java.math.BigDecimal;
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
public class DutyServiceImplTest {
    private static DutyRepository dutyRepository;
    private static RegistrationRepository registrationRepository;
    private static Validator validator;
    private static ClinicServiceFeignClient feignClient;
    private static CircuitBreaker circuitBreaker;

    private static Duty duty;
    private static Duty updatedDuty;

    private DutyServiceImpl dutyService;

    @BeforeAll
    public static void setUpMocks() {
        dutyRepository = mock(DutyRepository.class);
        registrationRepository = mock(RegistrationRepository.class);
        validator = mock(Validator.class);
        feignClient = mock(ClinicServiceFeignClient.class);

        circuitBreaker = mock(CircuitBreaker.class);
        when(circuitBreaker.decorateSupplier(any())).then(returnsFirstArg());
        when(circuitBreaker.decorateRunnable(any())).then(returnsFirstArg());
    }

    @BeforeAll
    public static void createDuty() {
        duty = new Duty();
        duty.setId(1L);
        duty.setName("Name1");
        duty.setDescription("Description1");
        duty.setNeededSpecialty("Specialty1");
        duty.setPrice(new BigDecimal(10));
    }

    @BeforeAll
    public static void createUpdatedDuty() {
        updatedDuty = new Duty();
        updatedDuty.setId(1L);
        updatedDuty.setName("Name2");
        updatedDuty.setDescription("Description2");
        updatedDuty.setNeededSpecialty("Specialty2");
        updatedDuty.setPrice(new BigDecimal(20));
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(dutyRepository, validator);
        dutyService = new DutyServiceImpl(dutyRepository, registrationRepository,
                validator, feignClient, circuitBreaker);
    }

    @Test
    public void shouldReturnDutyByIdWhenContainsIt() {
        when(dutyRepository.findById(1L)).thenReturn(Optional.of(duty));

        Duty saved = dutyService.findById(1).orElseThrow();
        assertThat(saved, is(equalTo(duty)));
    }

    @Test
    public void shouldReturnListOfDutiesWhenContainsMultipleDuties() {
        List<Duty> duties = List.of(duty, duty, duty);
        when(dutyRepository.findAll()).thenReturn(duties);

        List<Duty> saved = dutyService.findAll();
        assertThat(saved, is(equalTo(duties)));
    }

    @Test
    public void shouldReturnDutyByNameWhenContainsDuty() {
        when(dutyRepository.findByName(duty.getName())).thenReturn(Optional.ofNullable(duty));

        Duty saved = dutyService.findByName(duty.getName()).orElseThrow();
        assertThat(saved, is(equalTo(duty)));
    }

    @Test
    public void shouldSaveDutyWhenDutyIsValid() {
        when(dutyRepository.save(any(Duty.class))).thenReturn(duty);
        when(validator.validate(any(Duty.class))).thenReturn(Collections.emptySet());

        Duty saved = dutyService.save(duty);
        assertThat(saved, equalTo(duty));
    }

    @Test
    public void shouldThrowExceptionWhenDutyIsInvalid() {
        when(validator.validate(any(Duty.class))).thenThrow(IllegalModificationException.class);
        assertThrows(IllegalModificationException.class, () -> dutyService.save(new Duty()));
    }

    @Test
    public void shouldUpdateDutyWhenDutyIsValid() {
        when(dutyRepository.findById(1L)).thenReturn(Optional.of(duty));
        when(dutyRepository.save(updatedDuty)).thenReturn(updatedDuty);
        when(validator.validate(any(Duty.class))).thenReturn(Collections.emptySet());

        Duty updated = dutyService.update(updatedDuty);
        assertThat(updated, equalTo(updatedDuty));
    }

    @Test
    public void shouldNotContainDutyWhenDeletesThisDuty() {
        when(dutyRepository.findById(any(Long.class))).thenReturn(Optional.of(duty));
        doAnswer(invocation -> when(dutyRepository.findById(1L)).thenReturn(Optional.empty()))
                .when(dutyRepository).deleteById(1L);

        dutyService.deleteById(1);

        Optional<Duty> deleted = dutyService.findById(1);
        assertThat(deleted, is(Optional.empty()));
    }
}
