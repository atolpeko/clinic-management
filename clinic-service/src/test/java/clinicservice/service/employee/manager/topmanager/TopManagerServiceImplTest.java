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

package clinicservice.service.employee.manager.topmanager;

import clinicservice.data.TopManagerRepository;
import clinicservice.service.Address;
import clinicservice.service.department.Department;
import clinicservice.service.employee.PersonalData;
import clinicservice.service.exception.IllegalModificationException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.Validator;
import java.math.BigDecimal;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("category.UnitTest")
public class TopManagerServiceImplTest {
    private static TopManagerRepository managerRepository;
    private static Validator validator;
    private static PasswordEncoder encoder;
    private static CircuitBreaker circuitBreaker;

    private static TopManager manager;
    private static TopManager updatedManager;

    private TopManagerServiceImpl managerService;

    @BeforeAll
    public static void setUpMocks() {
        managerRepository = mock(TopManagerRepository.class);
        validator = mock(Validator.class);

        encoder = mock(PasswordEncoder.class);
        when(encoder.encode(anyString())).then(returnsFirstArg());
        when(encoder.matches(anyString(), anyString())).then(invocation -> {
            String rawPassword = invocation.getArgument(0);
            String encodedPassword = invocation.getArgument(1);
            return rawPassword.equals(encodedPassword);
        });

        circuitBreaker = mock(CircuitBreaker.class);
        when(circuitBreaker.decorateSupplier(any())).then(returnsFirstArg());
        when(circuitBreaker.decorateRunnable(any())).then(returnsFirstArg());
    }

    @BeforeAll
    public static void createManager() {
        PersonalData data = new PersonalData();
        data.setName("Name");
        data.setAddress(new Address("USA", "NY", "NYC", "23", 1));
        data.setPhone("1234567");
        data.setSex(PersonalData.Sex.MALE);
        data.setDateOfBirth(LocalDate.now());
        data.setHireDate(LocalDate.now());
        data.setSalary(BigDecimal.valueOf(1000));

        Address address = new Address("USA", "NY", "NYC", "22", 1);
        Department department = new Department();
        department.setAddress(address);
        department.setId(1L);

        manager = new TopManager();
        manager.setPersonalData(data);
        manager.setDepartment(department);
        manager.setId(1L);
        manager.setEmail("admin@gmail.com");
        manager.setPassword("12345678");
    }

    @BeforeAll
    public static void createUpdatedManager() {
        Address address = new Address("USA", "NY", "NYC", "22", 1);
        Department department = new Department();
        department.setAddress(address);
        department.setId(1L);

        updatedManager = new TopManager();
        updatedManager.setId(1L);
        updatedManager.setDepartment(department);
        updatedManager.setEmail("admin2@gmail.com");
        updatedManager.setPassword("12345");
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(managerRepository, validator);
        managerService = new TopManagerServiceImpl(managerRepository,
                encoder, validator, circuitBreaker);
    }

    @Test
    public void shouldReturnManagerByIdWhenContainsIt() {
        when(managerRepository.findById(1L)).thenReturn(Optional.of(manager));

        TopManager saved = managerService.findById(1).orElseThrow();
        assertThat(saved, is(equalTo(manager)));
    }

    @Test
    public void shouldReturnManagerByEmailWhenContainsIt() {
        when(managerRepository.findByEmail(manager.getEmail())).thenReturn(Optional.of(manager));

        TopManager saved = managerService.findByEmail(manager.getEmail()).orElseThrow();
        assertThat(saved, is(equalTo(manager)));
    }

    @Test
    public void shouldReturnListOfManagersWhenContainsMultipleManagers() {
        List<TopManager> managers = List.of(manager, manager, manager);
        when(managerRepository.findAll()).thenReturn(managers);

        List<TopManager> saved = managerService.findAll();
        assertThat(saved, is(equalTo(managers)));
    }

    @Test
    public void shouldCount5ManagersWhenContains5Mangers() {
        when(managerRepository.count()).thenReturn(5L);

        long count = managerService.count();
        assertThat(count, is(equalTo(5L)));
    }

    @Test
    public void shouldSaveManagerWhenManagerIsValid() {
        when(managerRepository.save(any(TopManager.class))).thenReturn(manager);
        when(validator.validate(any(TopManager.class))).thenReturn(Collections.emptySet());

        TopManager saved = managerService.save(manager);
        assertThat(saved, equalTo(manager));
    }

    @Test
    public void shouldThrowExceptionWhenManagerIsInvalid() {
        when(validator.validate(any(TopManager.class))).thenThrow(IllegalModificationException.class);
        assertThrows(IllegalModificationException.class, () -> managerService.save(new TopManager()));
    }

    @Test
    public void shouldUpdateManagerWhenManagerIsValid() {
        when(managerRepository.findById(1L)).thenReturn(Optional.of(manager));
        when(managerRepository.save(any(TopManager.class))).thenReturn(updatedManager);
        when(validator.validate(any(TopManager.class))).thenReturn(Collections.emptySet());

        TopManager updated = managerService.update(updatedManager);
        assertThat(updated, equalTo(updatedManager));
    }

    @Test
    public void shouldNotContainManagerWhenDeletesThisManager() {
        when(managerRepository.findById(any(Long.class))).thenReturn(Optional.of(manager));
        doAnswer(invocation -> when(managerRepository.findById(1L)).thenReturn(Optional.empty()))
                .when(managerRepository).deleteById(1L);

        managerService.deleteById(1);

        Optional<TopManager> deleted = managerService.findById(1);
        assertThat(deleted, is(Optional.empty()));
    }
}
