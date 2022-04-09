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

package clinicservice.service.facility;

import clinicservice.data.DepartmentRepository;
import clinicservice.data.FacilityRepository;
import clinicservice.service.department.Department;
import clinicservice.service.exception.IllegalModificationException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import javax.validation.Validator;

import java.util.Collections;
import java.util.HashSet;
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
public class FacilityServiceImplTest {
    private static FacilityRepository facilityRepository;
    private static DepartmentRepository departmentRepository;
    private static Validator validator;
    private static CircuitBreaker circuitBreaker;

    private static MedicalFacility facility;
    private static MedicalFacility updatedFacility;

    private FacilityServiceImpl facilityService;

    @BeforeAll
    public static void setUpMocks() {
        facilityRepository = mock(FacilityRepository.class);
        departmentRepository = mock(DepartmentRepository.class);
        validator = mock(Validator.class);

        circuitBreaker = mock(CircuitBreaker.class);
        when(circuitBreaker.decorateSupplier(any())).then(returnsFirstArg());
        when(circuitBreaker.decorateRunnable(any())).then(returnsFirstArg());
    }

    @BeforeAll
    public static void createFacility() {
        Department department = new Department();
        department.setId(1L);

        facility = new MedicalFacility();
        facility.setId(1L);
        facility.setName("Facility1");
        facility.setDepartments(new HashSet<>(Set.of(department)));
    }

    @BeforeAll
    public static void createUpdatedFacility() {
        Department department = new Department();
        department.setId(1L);

        updatedFacility = new MedicalFacility();
        updatedFacility.setId(1L);
        updatedFacility.setName("Facility2");
        updatedFacility.setDepartments(new HashSet<>(Set.of(department)));
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(facilityRepository, departmentRepository, validator);
        facilityService = new FacilityServiceImpl(facilityRepository, departmentRepository,
                validator, circuitBreaker);
    }

    @Test
    public void shouldReturnFacilityByIdWhenContainsIt() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));

        MedicalFacility saved = facilityService.findById(1).orElseThrow();
        assertThat(saved, is(equalTo(facility)));
    }

    @Test
    public void shouldReturnListOfFacilitiesWhenContainsMultipleFacilities() {
        List<MedicalFacility> facilities = List.of(facility, facility, facility);
        when(facilityRepository.findAll()).thenReturn(facilities);

        List<MedicalFacility> saved = facilityService.findAll();
        assertThat(saved, is(equalTo(facilities)));
    }

    @Test
    public void shouldReturnListOfFacilitiesByDepartmentIdWhenContainsMultipleFacilities() {
        List<MedicalFacility> facilities = List.of(facility, facility, facility);
        when(facilityRepository.findAllByDepartmentId(1L)).thenReturn(facilities);

        List<MedicalFacility> saved = facilityService.findAllByDepartmentId(1L);
        assertThat(saved, is(equalTo(facilities)));
    }

    @Test
    public void shouldSaveFacilityWhenFacilityIsValid() {
        when(departmentRepository.findById(any())).thenReturn(facility.getDepartments().stream().findAny());
        when(facilityRepository.save(any(MedicalFacility.class))).thenReturn(facility);
        when(validator.validate(any(MedicalFacility.class))).thenReturn(Collections.emptySet());

        MedicalFacility saved = facilityService.save(facility);
        assertThat(saved, equalTo(facility));
    }

    @Test
    public void shouldThrowExceptionWhenFacilityIsInvalid() {
        when(validator.validate(any(MedicalFacility.class))).thenThrow(IllegalModificationException.class);
        assertThrows(IllegalModificationException.class, () -> facilityService.save(new MedicalFacility()));
    }

    @Test
    public void shouldUpdateFacilityWhenFacilityIsValid() {
        when(departmentRepository.findById(any())).thenReturn(facility.getDepartments().stream().findAny());
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(facilityRepository.save(updatedFacility)).thenReturn(updatedFacility);
        when(validator.validate(any(MedicalFacility.class))).thenReturn(Collections.emptySet());

        MedicalFacility updated = facilityService.update(updatedFacility);
        assertThat(updated, equalTo(updatedFacility));
    }

    @Test
    public void shouldNotContainFacilityWhenDeletesThisFacility() {
        when(facilityRepository.findById(any(Long.class))).thenReturn(Optional.of(facility));
        doAnswer(invocation -> when(facilityRepository.findById(1L)).thenReturn(Optional.empty()))
                .when(facilityRepository).deleteById(1L);

        facilityService.deleteById(1);

        Optional<MedicalFacility> deleted = facilityService.findById(1);
        assertThat(deleted, is(Optional.empty()));
    }
}
