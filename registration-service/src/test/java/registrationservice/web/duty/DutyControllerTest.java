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

package registrationservice.web.duty;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import registrationservice.service.duty.Duty;
import registrationservice.service.duty.DutyService;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@Tag("category.IntegrationTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.cloud.config.enabled=false")
@AutoConfigureMockMvc
public class DutyControllerTest {
    private static String newDutyJson;
    private static String updateDutyJson;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DutyService dutyService;

    @BeforeAll
    public static void createClientJsons() {
        newDutyJson = "{\"name\": \"New Duty\"," +
                "\"description\": \"Description\"," +
                "\"neededSpecialty\": \"Surgery\"," +
                "\"price\" : \"10\"}";

        updateDutyJson = "{\"name\": \"Updated Duty\"," +
                "\"description\": \"Description2\"," +
                "\"neededSpecialty\": \"Specialty\"," +
                "\"price\" : \"30\"}";
    }

    @Test
    public void shouldReturnDutiesOnDutiesGetRequest() throws Exception {
        mvc.perform(get("/services"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnDutyOnDutyGetRequest() throws Exception {
        mvc.perform(get("/services/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnDutyOnDutyGetByNameRequest() throws Exception {
        mvc.perform(get("/services/1").param("name", "Duty1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnSavedDutyOnDutiesPostRequest() throws Exception {
        int initialCount = dutyService.findAll().size();
        mvc.perform(post("/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newDutyJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        int newCount = dutyService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    @Test
    public void shouldReturnUpdatedDutyOnDutyPatchRequest() throws Exception {
        Duty initial = dutyService.findById(2).orElseThrow();
        mvc.perform(patch("/services/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateDutyJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        Duty updated = dutyService.findById(2).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    @Test
    public void shouldDeleteDutyOnDutyDeleteRequest() throws Exception {
        mvc.perform(delete("/services/3"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Optional<Duty> deleted = dutyService.findById(3);
        assertThat(deleted, is(Optional.empty()));
    }
}
