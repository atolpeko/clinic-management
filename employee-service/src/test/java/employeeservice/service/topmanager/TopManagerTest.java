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

package employeeservice.service.topmanager;

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
public class TopManagerTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldPassValidationWhenHasValidData() {
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

        TopManager manager = TopManager.builder()
                .withPersonalData(data)
                .withEmail("doctor@gmail.com")
                .withPassword("12345678")
                .withDepartment(new Department(1L))
                .build();

        int errors = validator.validate(manager).size();
        assertThat(errors, is(0));
    }

    @Test
    public void shouldNotPassValidationWhenHasInvalidData() {
        Address address = Address.builder().withHouseNumber(-1).build();
        PersonalData data = PersonalData.builder()
                .withAddress(address)
                .withSalary(BigDecimal.valueOf(-1))
                .build();

        TopManager manager = TopManager.builder()
                .withPersonalData(data)
                .withEmail("not-a-email")
                .withPassword("123")
                .build();

        int errors = validator.validate(manager).size();
        assertThat(errors, is(13));
    }
}
