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

import employeeservice.service.Address;
import employeeservice.service.PersonalData;
import employeeservice.service.external.Department;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Tag("category.UnitTest")
public class DoctorTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldPassValidationWhenHasValidData() {
        PersonalData data = new PersonalData();
        data.setName("Name");
        data.setAddress(new Address("USA", "NY", "NYC", "23", 1));
        data.setPhone("1234567");
        data.setSex(PersonalData.Sex.MALE);
        data.setDateOfBirth(LocalDate.now());
        data.setHireDate(LocalDate.now());
        data.setSalary(BigDecimal.valueOf(1000));

        Doctor doctor = new Doctor();
        doctor.setPersonalData(data);
        doctor.setEmail("doctor@gmail.com");
        doctor.setPassword("12345678");
        doctor.setSpecialty("Specialty");
        doctor.setDepartment(new Department(1L));
        doctor.setPracticeBeginningDate(LocalDate.now());

        int errors = validator.validate(doctor).size();
        assertThat(errors, is(0));
    }

    @Test
    public void shouldNotPassValidationWhenHasInvalidData() {
        Address address = new Address();
        address.setHouseNumber(-1);

        PersonalData data = new PersonalData();
        data.setAddress(address);
        data.setSalary(BigDecimal.valueOf(-1));

        Doctor doctor = new Doctor();
        doctor.setPersonalData(data);
        doctor.setEmail("not-a-email");

        int errors = validator.validate(doctor).size();
        assertThat(errors, is(16));
    }
}
