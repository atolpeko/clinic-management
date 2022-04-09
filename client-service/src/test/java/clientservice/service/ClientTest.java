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

package clientservice.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Tag("category.UnitTest")
public class ClientTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void shouldPassValidationWhenHasValidData() {
        Client client = new Client();
        client.setEmail("alex@gmail.com");
        client.setPassword("12345678");
        client.setName("Alexander");
        client.setSex(Client.Sex.MALE);
        client.setPhoneNumber("+375-34-556-70-90");
        client.setAddress(new Address("USA", "NY", "NYC", "23", 1));

        int errors = validator.validate(client).size();
        assertThat(errors, is(0));
    }

    @Test
    public void shouldNotPassValidationWhenHasInvalidData() {
        Address address = new Address();
        address.setHouseNumber(-1);

        Client client = new Client();
        client.setEmail("not-a-email");
        client.setPassword("12345");
        client.setAddress(address);

        int errors = validator.validate(client).size();
        assertThat(errors, is(10));
    }
}
