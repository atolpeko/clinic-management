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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.ResultMatcher;
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
@SpringBootTest(properties = "spring.cloud.config.enabled=false")
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
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnSavedDutyOnDutiesPostRequest() throws Exception {
        int initialCount = dutyService.findAll().size();
        postAndExpect(status().isCreated());

        int newCount = dutyService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    private void postAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(post("/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newDutyJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    public void shouldDenyDutyPostingWhenUserIsNotTopManager() throws Exception {
        postAndExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnUpdatedDutyOnDutyPatchRequestWhenUserIsTopManager() throws Exception {
        Duty initial = dutyService.findById(2).orElseThrow();
        patchAndExpect(status().isOk());

        Duty updated = dutyService.findById(2).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    private void patchAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(patch("/services/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateDutyJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    public void shouldDenyDutyPatchingWhenUserIsNotTopManager() throws Exception {
        patchAndExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldDeleteDutyOnDutyDeleteRequestWhenUserIsTopManager() throws Exception {
        deleteAndExpect(status().isNoContent());

        Optional<Duty> deleted = dutyService.findById(3);
        assertThat(deleted, is(Optional.empty()));
    }

    private void deleteAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(delete("/services/3"))
                .andDo(print())
                .andExpect(status);
    }

    @Test
    @WithMockUser
    public void shouldDenyDutyDeletionWhenUserIsNotTopManager() throws Exception {
        deleteAndExpect(status().isForbidden());
    }
}
