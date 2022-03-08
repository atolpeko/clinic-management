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

package registrationservice.web.registration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import registrationservice.service.registration.Registration;
import registrationservice.service.registration.RegistrationService;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("category.IntegrationTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.cloud.config.enabled=false")
@AutoConfigureMockMvc
public class RegistrationControllerTest {
    private static String newRegistrationJson;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RegistrationService registrationService;

    @BeforeAll
    public static void createClientJsons() {
        newRegistrationJson = "{\"date\": \"2022-03-08T00:00:00\"," +
                "\"duty\": { \"id\": 1  }," +
                "\"client\": { \"id\": 1  }," +
                "\"doctor\": { \"id\": 1  }}";
    }

    @Test
    public void shouldReturnRegistrationsOnRegistrationsGetRequest() throws Exception {
        mvc.perform(get("/registrations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnRegistrationOnRegistrationGetRequest() throws Exception {
        mvc.perform(get("/registrations/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnRegistrationOnRegistrationGetByClientIdRequest() throws Exception {
        mvc.perform(get("/registrations").param("clientId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnRegistrationOnRegistrationGetByDoctorIdRequest() throws Exception {
        mvc.perform(get("/registrations").param("doctorId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnSavedRegistrationOnRegistrationsPostRequest() throws Exception {
        int initialCount = registrationService.findAll().size();
        mvc.perform(post("/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newRegistrationJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        int newCount = registrationService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }


    @Test
    public void shouldReturnUpdatedRegistrationOnRegistrationStatusPatchRequest() throws Exception {
        boolean initial = registrationService.findById(1).orElseThrow().isActive();
        mvc.perform(patch("/registrations/1/status").param("isActive", "false"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        boolean updated = registrationService.findById(1).orElseThrow().isActive();
        assertThat(updated, is(not(equalTo(initial))));
    }

    @Test
    public void shouldDeleteRegistrationOnRegistrationDeleteRequest() throws Exception {
        mvc.perform(delete("/registrations/3"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Optional<Registration> deleted = registrationService.findById(3);
        assertThat(deleted, is(Optional.empty()));
    }
}
