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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import resultsservice.service.external.client.Client;
import resultsservice.service.external.employee.Doctor;
import resultsservice.service.external.registration.Duty;

import javax.validation.Validation;
import javax.validation.Validator;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Tag("category.UnitTest")
public class ResultTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldPassValidationWhenHasValidData() {
        Client client = Client.builder().withId(1L).build();
        Doctor doctor = Doctor.builder().withId(1L).build();
        Duty duty = Duty.builder().withId(1L).build();
        Result result = Result.builder()
                .withData("Data")
                .withClient(client)
                .withDoctor(doctor)
                .withDuty(duty)
                .build();

        int errors = validator.validate(result).size();
        assertThat(errors, is(0));
    }

    @Test
    public void shouldNotPassValidationWhenHasInvalidData() {
        Result result = new Result();

        int errors = validator.validate(result).size();
        assertThat(errors, is(4));
    }
}
