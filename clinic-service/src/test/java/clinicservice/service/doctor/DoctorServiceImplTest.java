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

package clinicservice.service.doctor;

import clinicservice.data.DepartmentRepository;
import clinicservice.data.DoctorRepository;
import clinicservice.service.department.Department;
import clinicservice.service.exception.IllegalModificationException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import javax.validation.Validator;
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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("category.UnitTest")
public class DoctorServiceImplTest {
    private static DoctorRepository doctorRepository;
    private static DepartmentRepository departmentRepository;
    private static Validator validator;
    private static CircuitBreaker circuitBreaker;

    private static Doctor doctor;
    private static Doctor updatedDoctor;

    private DoctorServiceImpl doctorService;

    @BeforeAll
    public static void setUpMocks() {
        doctorRepository = mock(DoctorRepository.class);
        departmentRepository = mock(DepartmentRepository.class);
        validator = mock(Validator.class);

        circuitBreaker = mock(CircuitBreaker.class);
        when(circuitBreaker.decorateSupplier(any())).then(returnsFirstArg());
        when(circuitBreaker.decorateRunnable(any())).then(returnsFirstArg());
    }

    @BeforeAll
    public static void createDoctor() {
        Department department = new Department();
        department.setId(1L);

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Doctor1");
        doctor.setSpecialty("Specialty1");
        doctor.setDepartment(department);
        doctor.setPracticeBeginningDate(LocalDate.now());
    }

    @BeforeAll
    public static void createUpdatedDoctor() {
        Department department = new Department();
        department.setId(1L);

        updatedDoctor = new Doctor();
        updatedDoctor.setId(1L);
        updatedDoctor.setName("Doctor2");
        updatedDoctor.setSpecialty("Specialty2");
        updatedDoctor.setDepartment(department);
        updatedDoctor.setPracticeBeginningDate(LocalDate.now());
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(doctorRepository, departmentRepository, validator);
        doctorService = new DoctorServiceImpl(doctorRepository, departmentRepository,
                validator, circuitBreaker);
    }

    @Test
    public void shouldReturnDoctorByIdWhenContainsIt() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        Doctor saved = doctorService.findById(1).orElseThrow();
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
    public void shouldSaveDoctorWhenDoctorIsValid() {
        Department department = doctor.getDepartment();
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
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
        Department department = doctor.getDepartment();
        when(departmentRepository.findById(department.getId())).thenReturn(Optional.of(department));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(updatedDoctor)).thenReturn(updatedDoctor);
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

        Optional<Doctor> deletedDoctor = doctorService.findById(1);
        assertThat(deletedDoctor, is(Optional.empty()));
    }
}
