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

package registrationservice.service.registration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import registrationservice.service.duty.Duty;

import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Tag("category.UnitTest")
public class RegistrationTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldPassValidationWhenHasValidData() {
        Registration registration = new Registration();
        registration.setDate(LocalDateTime.now());
        registration.setClientId(1L);
        registration.setDoctorId(1L);
        registration.setDuty(new Duty());

        int errors = validator.validate(registration).size();
        assertThat(errors, is(0));
    }

    @Test
    public void shouldNotPassValidationWhenHasInvalidData() {
        Registration registration = new Registration();
        registration.setDoctorId(-1L);
        registration.setClientId(-1L);

        int errors = validator.validate(registration).size();
        assertThat(errors, is(3));
    }
}
