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

package clientservice.web;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@Tag("category.IntegrationTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.cloud.config.enabled=false")
@AutoConfigureMockMvc
public class ClientControllerTest {
    private static String newClientJson;
    private static String updateClientJson;

    @Autowired
    private MockMvc mvc;

    @BeforeAll
    public static void createClientJsons() {
        newClientJson = "{\"id\":null," +
                "\"email\":\"altolpeko@gmail.com\"," +
                "\"password\":\"12345678\"," +
                "\"name\":\"Alexander\"," +
                "\"sex\":\"MALE\"," +
                "\"phoneNumber\":\"+375-34-556-70-90\"," +
                "\"country\":\"Belarus\"," +
                "\"city\":\"Minsk\"," +
                "\"street\":\"Nemiga\"," +
                "\"houseNumber\":11}";

        updateClientJson = "{\"id\":null," +
                "\"email\":\"alextolpeko@gmail.com\"," +
                "\"password\":\"87654321\"," +
                "\"name\":\"Alex\"," +
                "\"sex\":\"MALE\"," +
                "\"phoneNumber\":\"+375-21-134-54-67\"," +
                "\"country\":\"Belarus\"," +
                "\"city\":\"Minsk\"," +
                "\"street\":\"Goretskogo\"," +
                "\"houseNumber\":33}";
    }

    @Test
    public void shouldReturnClientsOnClientsGetRequest() throws Exception {
        mvc.perform(get("/clients"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnClientOnClientGetRequest() throws Exception {
        mvc.perform(get("/clients/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnClientOnClientGetByEmailRequest() throws Exception {
        mvc.perform(get("/clients").param("email", "atolpeko@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnSavedClientOnRegisterPostRequest() throws Exception {
        mvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newClientJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnUpdatedClientOnClientPatchRequest() throws Exception {
        mvc.perform(patch("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateClientJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnUpdatedClientOnClientStatusPatchRequest() throws Exception {
        mvc.perform(patch("/clients/1/status").param("isActive", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldDeleteClientOnClientDeleteRequest() throws Exception {
        mvc.perform(delete("/clients/2"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
