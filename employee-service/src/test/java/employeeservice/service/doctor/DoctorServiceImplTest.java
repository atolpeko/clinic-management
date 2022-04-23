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

package employeeservice.service.doctor;

import employeeservice.data.DoctorRepository;
import employeeservice.service.Address;
import employeeservice.service.PersonalData;
import employeeservice.service.exception.IllegalModificationException;
import employeeservice.service.external.ClinicServiceFeignClient;
import employeeservice.service.external.Department;

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

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("category.UnitTest")
public class DoctorServiceImplTest {
    private static DoctorRepository doctorRepository;
    private static ClinicServiceFeignClient serviceFeignClient;
    private static PasswordEncoder encoder;
    private static Validator validator;
    private static CircuitBreaker circuitBreaker;

    private static Doctor doctor;
    private static Doctor updatedDoctor;

    private DoctorServiceImpl doctorService;

    @BeforeAll
    public static void setUpMocks() {
        doctorRepository = mock(DoctorRepository.class);
        validator = mock(Validator.class);

        serviceFeignClient = mock(ClinicServiceFeignClient.class);
        when(serviceFeignClient.findDepartmentById(any())).thenReturn(new Department(1L));

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
    public static void createDoctor() {
        Address address = Address.builder()
                .withCountry("USA")
                .withState("NY")
                .withCity("NYC")
                .withStreet("23")
                .withHouseNumber(1)
                .build();

        PersonalData data = PersonalData.builder()
                .withAddress(address)
                .withName("Client")
                .withDateOfBirth(LocalDate.now())
                .withHireDate(LocalDate.now())
                .withSalary(BigDecimal.TEN)
                .withPhone("1234567")
                .withSex(PersonalData.Sex.MALE)
                .build();

        doctor = Doctor.builder()
                .withId(1L)
                .withPersonalData(data)
                .withEmail("doctor@gmail.com")
                .withPassword("12345678")
                .withSpecialty("Specialty")
                .withDepartment(new Department(1L))
                .withPracticeBeginningDate(LocalDate.now())
                .build();
    }

    @BeforeAll
    public static void createUpdatedDoctor() {
        Address address = Address.builder()
                .withCountry("USA")
                .withState("NY")
                .withCity("NYC")
                .withStreet("23")
                .withHouseNumber(1)
                .build();

        PersonalData data = PersonalData.builder()
                .withAddress(address)
                .withName("Doctor")
                .withDateOfBirth(LocalDate.now())
                .withHireDate(LocalDate.now())
                .withSalary(BigDecimal.ONE)
                .withPhone("534987364")
                .withSex(PersonalData.Sex.MALE)
                .build();

        updatedDoctor = Doctor.builder()
                .withId(1L)
                .withPersonalData(data)
                .withEmail("doctor2@gmail.com")
                .withPassword("87654321")
                .withSpecialty("Specialty2")
                .withDepartment(new Department(1L))
                .withPracticeBeginningDate(LocalDate.now())
                .build();
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(doctorRepository, validator);
        doctorService = new DoctorServiceImpl(doctorRepository, serviceFeignClient,
                encoder, validator, circuitBreaker);
    }

    @Test
    public void shouldReturnDoctorByIdWhenContainsIt() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        Doctor saved = doctorService.findById(1).orElseThrow();
        assertThat(saved, is(equalTo(doctor)));
    }

    @Test
    public void shouldReturnDoctorByEmailWhenContainsIt() {
        when(doctorRepository.findByEmail(doctor.getEmail())).thenReturn(Optional.of(doctor));

        Doctor saved = doctorService.findByEmail(doctor.getEmail()).orElseThrow();
        assertThat(saved, is(equalTo(doctor)));
    }

    @Test
    public void shouldReturnListOfDoctorsWhenContainsMultipleDoctors() {
        List<Doctor> doctors = List.of(doctor, doctor, doctor);
        when(doctorRepository.findAll()).thenReturn(doctors);

        List<Doctor> saved = doctorService.findAll();
        assertThat(saved, is(equalTo(doctors)));
    }

    @Test
    public void shouldReturnListOfDoctorsByDepartmentIdWhenContainsMultipleDoctors() {
        long departmentId = doctor.getDepartment().getId();
        List<Doctor> doctors = List.of(doctor, doctor, doctor);
        when(doctorRepository.findAllByDepartmentId(departmentId)).thenReturn(doctors);

        List<Doctor> saved = doctorService.findAllByDepartmentId(departmentId);
        assertThat(saved, is(equalTo(doctors)));
    }

    @Test
    public void shouldReturnListOfDoctorsBySpecialtyWhenContainsMultipleDoctors() {
        List<Doctor> doctors = List.of(doctor, doctor, doctor);
        when(doctorRepository.findAllBySpecialty("Surgery")).thenReturn(doctors);

        List<Doctor> saved = doctorService.findAllBySpecialty("Surgery");
        assertThat(saved, is(equalTo(doctors)));
    }

    @Test
    public void shouldCount5DoctorsWhenContains5Doctors() {
        when(doctorRepository.count()).thenReturn(5L);

        long count = doctorService.count();
        assertThat(count, is(equalTo(5L)));
    }

    @Test
    public void shouldSaveDoctorWhenDoctorIsValid() {
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);
        when(validator.validate(any(Doctor.class))).thenReturn(Collections.emptySet());

        Doctor saved = doctorService.save(doctor);
        assertThat(saved, equalTo(doctor));
    }

    @Test
    public void shouldThrowExceptionWhenDoctorIsInvalid() {
        when(validator.validate(any(Doctor.class))).thenThrow(IllegalModificationException.class);
        assertThrows(IllegalModificationException.class, () -> doctorService.save(new Doctor()));
    }

    @Test
    public void shouldUpdateDoctorWhenDoctorIsValid() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(updatedDoctor);
        when(validator.validate(any(Doctor.class))).thenReturn(Collections.emptySet());

        Doctor updated = doctorService.update(updatedDoctor);
        assertThat(updated, equalTo(updatedDoctor));
    }

    @Test
    public void shouldNotContainDoctorWhenDeletesThisDoctor() {
        when(doctorRepository.findById(any(Long.class))).thenReturn(Optional.of(doctor));
        doAnswer(invocation -> when(doctorRepository.findById(1L)).thenReturn(Optional.empty()))
                .when(doctorRepository).deleteById(1L);

        doctorService.deleteById(1);

        Optional<Doctor> deleted = doctorService.findById(1);
        assertThat(deleted, is(Optional.empty()));
    }
}
