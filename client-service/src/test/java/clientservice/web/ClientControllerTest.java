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

import clientservice.service.Client;
import clientservice.service.ClientService;

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

    @Autowired
    private ClientService clientService;

    @BeforeAll
    public static void createClientJsons() {
        newClientJson = "{\"email\":\"altolpeko@gmail.com\"," +
                "\"password\":\"12345678\"," +
                "\"name\":\"Alexander\"," +
                "\"sex\":\"MALE\"," +
                "\"phoneNumber\":\"+375-34-556-70-90\"," +
                "\"address\":{" +
                    "\"country\":\"USA\"," +
                    "\"state\":\"NY\"," +
                    "\"city\":\"NYC\"," +
                    "\"street\":\"23\"," +
                    "\"houseNumber\":11" +
                "}}";

        updateClientJson = "{\"email\":\"alextolpeko@gmail.com\"," +
                "\"password\":\"87654321\"," +
                "\"name\":\"Alex\"," +
                "\"sex\":\"MALE\"," +
                "\"phoneNumber\":\"+375-21-134-54-67\"," +
                "\"address\":{" +
                    "\"country\":\"USA\"," +
                    "\"state\":\"LA\"," +
                    "\"city\":\"California\"," +
                    "\"street\":\"36\"," +
                    "\"houseNumber\":1" +
                "}}";
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
    public void shouldReturnSavedClientOnClientsPostRequest() throws Exception {
        int initialCount = clientService.findAll().size();
        mvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newClientJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        int newCount = clientService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    @Test
    public void shouldReturnUpdatedClientOnClientPatchRequest() throws Exception {
        Client initial = clientService.findById(1).orElseThrow();
        mvc.perform(patch("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateClientJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        Client updated = clientService.findById(1).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    @Test
    public void shouldReturnUpdatedClientOnClientStatusPatchRequest() throws Exception {
        boolean initial = clientService.findById(1).orElseThrow().isEnabled();
        mvc.perform(patch("/clients/1/status").param("isActive", "false"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        boolean updated = clientService.findById(1).orElseThrow().isEnabled();
        assertThat(updated, is(not(equalTo(initial))));
    }

    @Test
    public void shouldDeleteClientOnClientDeleteRequest() throws Exception {
        mvc.perform(delete("/clients/2"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Optional<Client> deleted = clientService.findById(2);
        assertThat(deleted, is(Optional.empty()));
    }
}
