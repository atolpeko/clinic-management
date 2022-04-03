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

package clinicservice.web.manager.topmanager;

import clinicservice.service.employee.manager.topmanager.TopManager;
import clinicservice.service.employee.manager.topmanager.TopManagerService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
public class TopManagerControllerTest {
    private static String newManagerJson;
    private static String updateManagerJson;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TopManagerService managerService;

    @BeforeAll
    public static void createManagerJsons() {
        newManagerJson = "{\"name\": \"Alexander\"," +
                "\"hireDate\": \"2022-03-01\"," +
                "\"phone\": \"123456\"," +
                "\"sex\": \"MALE\"," +
                "\"dateOfBirth\": \"1995-01-22\"," +
                "\"salary\": 1000," +
                "\"department\" : { \"id\": 1 }," +
                "\"email\": \"top-manager@gmail.com\"," +
                "\"password\": \"12345678\"," +
                "\"address\":{" +
                "\"country\":\"USA\"," +
                "\"state\":\"NY\"," +
                "\"city\":\"NYC\"," +
                "\"street\":\"23\"," +
                "\"houseNumber\":11" +
                "}}";

        updateManagerJson = "{\"name\": \"Mark\"," +
                "\"department\" : { \"id\": 2 }," +
                "\"email\": \"top-manager2@gmail.com\"}";
    }

    @Test
    public void shouldReturnManagersOnManagersGetRequest() throws Exception {
        mvc.perform(get("/top-managers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnManagerOnManagerGetRequest() throws Exception {
        mvc.perform(get("/top-managers/6"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnManagerOnManagerGetByEmailRequest() throws Exception {
        mvc.perform(get("/top-managers").param("email", "email6@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnSavedManagerOnManagersPostRequest() throws Exception {
        int initialCount = managerService.findAll().size();
        mvc.perform(post("/top-managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newManagerJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        int newCount = managerService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    @Test
    public void shouldReturnUpdatedManagerOnManagerPatchRequest() throws Exception {
        TopManager initial = managerService.findById(6).orElseThrow();
        mvc.perform(patch("/top-managers/6")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateManagerJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        TopManager updated = managerService.findById(6).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    @Test
    public void shouldDeleteManagerOnManagerDeleteRequest() throws Exception {
        mvc.perform(delete("/top-managers/7"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Optional<TopManager> deleted = managerService.findById(7);
        assertThat(deleted, is(Optional.empty()));
    }
}
