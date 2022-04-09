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

package clinicservice.service.department;

import clinicservice.data.DepartmentRepository;
import clinicservice.service.Address;
import clinicservice.service.exception.IllegalModificationException;
import clinicservice.service.facility.MedicalFacility;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import javax.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
public class DepartmentServiceImplTest {
    private static DepartmentRepository repository;
    private static CircuitBreaker circuitBreaker;
    private static Validator validator;

    private static Department department;
    private static Department updatedDepartment;

    private DepartmentServiceImpl departmentService;

    @BeforeAll
    public static void setUpMocks() {
        repository = mock(DepartmentRepository.class);
        validator = mock(Validator.class);

        circuitBreaker = mock(CircuitBreaker.class);
        when(circuitBreaker.decorateSupplier(any())).then(returnsFirstArg());
        when(circuitBreaker.decorateRunnable(any())).then(returnsFirstArg());
    }

    @BeforeAll
    public static void createDepartment() {
        department = new Department();
        department.setId(1L);
        department.setAddress(new Address("USA", "NY", "NYC", "23", 1));

        MedicalFacility facility = new MedicalFacility();
        facility.setId(1L);
        facility.setDepartments(Set.of(department));
    }

    @BeforeAll
    public static void createUpdatedDepartment() {
        updatedDepartment = new Department();
        updatedDepartment.setId(1L);
        updatedDepartment.setAddress(new Address("USA", "California", "Los Angels", "36", 2));
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(repository, validator);
        departmentService = new DepartmentServiceImpl(repository, validator, circuitBreaker);
    }

    @Test
    public void shouldReturnDepartmentByIdWhenContainsIt() {
        when(repository.findById(1L)).thenReturn(Optional.of(department));

        Department saved = departmentService.findById(1).orElseThrow();
        assertThat(saved, is(equalTo(department)));
    }

    @Test
    public void shouldReturnListODepartmentsWhenContainsMultipleDepartments() {
        List<Department> departments = List.of(department, department, department);
        when(repository.findAll()).thenReturn(departments);

        List<Department> saved = departmentService.findAll();
        assertThat(saved, is(equalTo(departments)));
    }

    @Test
    public void shouldReturnListODepartmentsByFacilityIdWhenContainsMultipleDepartments() {
        List<Department> departments = List.of(department, department, department);
        when(repository.findAllByFacilityId(1L)).thenReturn(departments);

        List<Department> saved = departmentService.findAllByFacilityId(1L);
        assertThat(saved, is(equalTo(departments)));
    }

    @Test
    public void shouldSaveDepartmentWhenDepartmentIsValid() {
        when(repository.save(department)).thenReturn(department);
        when(validator.validate(any(Department.class))).thenReturn(Collections.emptySet());

        Department saved = departmentService.save(department);
        assertThat(saved, equalTo(department));
    }

    @Test
    public void shouldThrowExceptionWhenDepartmentIsInvalid() {
        when(validator.validate(any(Department.class))).thenThrow(IllegalModificationException.class);
        assertThrows(IllegalModificationException.class, () -> departmentService.save(new Department()));
    }

    @Test
    public void shouldUpdateDepartmentWhenDepartmentIsValid() {
        when(repository.findById(1L)).thenReturn(Optional.of(department));
        when(repository.save(updatedDepartment)).thenReturn(updatedDepartment);
        when(validator.validate(any(Department.class))).thenReturn(Collections.emptySet());

        Department updated = departmentService.update(updatedDepartment);
        assertThat(updated, equalTo(updatedDepartment));
    }

    @Test
    public void shouldNotContainDepartmentWhenDeletesThisDepartment() {
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(department));
        doAnswer(invocation -> when(repository.findById(1L)).thenReturn(Optional.empty()))
                .when(repository).deleteById(1L);

        departmentService.deleteById(1);

        Optional<Department> deleted = departmentService.findById(1);
        assertThat(deleted, is(Optional.empty()));
    }
}
