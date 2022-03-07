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

import clinicservice.service.department.Department;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Tag("category.UnitTest")
public class MedicalFacilityTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldPassValidationWhenHasValidData() {
        MedicalFacility facility = new MedicalFacility("Facility", Set.of(new Department()));

        int errors = validator.validate(facility).size();
        assertThat(errors, is(0));
    }

    @Test
    public void shouldNotPassValidationWhenHasInvalidData() {
        MedicalFacility facility = new MedicalFacility();

        int errors = validator.validate(facility).size();
        assertThat(errors, is(2));
    }
}
