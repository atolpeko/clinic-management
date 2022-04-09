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
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Tag("category.IntegrationTest")
@SpringBootTest(properties = "spring.cloud.config.enabled=false")
@AutoConfigureMockMvc
public class ClientControllerTest {
    private static String newClientJson;
    private static String updateClient1Json;
    private static String updateClient2Json;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ClientService clientService;

    @BeforeAll
    public static void createNewClientJson() {
        newClientJson = "{\"email\":\"alex@gmail.com\"," +
                "\"password\":\"dfd1ld112d\"," +
                "\"name\":\"Alexander\"," +
                "\"sex\":\"MALE\"," +
                "\"phoneNumber\":\"+375-33-556-70-90\"," +
                "\"address\":{" +
                    "\"country\":\"USA\"," +
                    "\"state\":\"NY\"," +
                    "\"city\":\"NYC\"," +
                    "\"street\":\"23\"," +
                    "\"houseNumber\":11" +
                "}}";
    }

    @BeforeAll
    public static void createUpdateClientJsons() {
        updateClient1Json = "{\"password\":\"fdfdef222a44\"," +
                "\"name\":\"Rob\"," +
                "\"sex\":\"MALE\"," +
                "\"phoneNumber\":\"+375-21-134-54-67\"," +
                "\"address\":{" +
                    "\"country\":\"USA\"," +
                    "\"state\":\"California\"," +
                    "\"city\":\"LA\"," +
                    "\"street\":\"36\"," +
                    "\"houseNumber\":1" +
                "}}";

        updateClient2Json = "{\"password\":\"ffdf24343a\"," +
                "\"name\":\"Markus\"," +
                "\"sex\":\"MALE\"," +
                "\"phoneNumber\":\"+375-21-54-54-67\"," +
                "\"address\":{" +
                    "\"country\":\"USA\"," +
                    "\"state\":\"LA\"," +
                    "\"city\":\"California\"," +
                    "\"street\":\"44\"," +
                    "\"houseNumber\":2" +
                "}}";
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnClientsOnClientsGetAllRequestWhenUserIsTopManager() throws Exception {
       getAllAndExpect(status().isOk());
    }

    private void getAllAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/clients"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = { "TEAM_MANAGER", "USER", "DOCTOR", "INTERNAL" })
    public void shouldDenyAccessToAllClientsWhenUserIsNotTopManager() throws Exception {
        getAllAndExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyAccessToAllClientsWhenUserIsNotAuthenticated() throws Exception {
        getAllAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnClientOnClientGetByIdRequestWhenUserIsTopManager() throws Exception {
        getByIdAndExpect(status().isOk());
    }

    private void getByIdAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/clients/1"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = "DOCTOR")
    public void shouldReturnClientOnClientGetByIdRequestWhenUserIsDoctor() throws Exception {
        getByIdAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "alexander@gmail.com", authorities = "USER")
    public void shouldReturnClientOnClientGetByIdRequestWhenUserIsResourceOwner() throws Exception {
       getByIdAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "INTERNAL")
    public void shouldReturnClientOnClientGetByIdRequestWhenUserIsInternal() throws Exception {
        getByIdAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "robert@gmail.com", authorities = "USER")
    public void shouldDenyAccessToClientByIdWhenUserIsNotResourceOwner() throws Exception {
        getByIdAndExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyAccessToClientByIdWhenUserIsNotAuthenticated() throws Exception {
        getByIdAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnClientOnClientGetByEmailRequestWhenUserIsTopManager() throws Exception {
        getByEmailAndExpect(status().isOk());
    }

    private void getByEmailAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/clients").param("email", "alexander@gmail.com"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = "DOCTOR")
    public void shouldReturnClientOnClientGetByEmailRequestWhenUserIsDoctor() throws Exception {
        getByEmailAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "alexander@gmail.com", authorities = "USER")
    public void shouldReturnClientOnClientGetByEmailRequestWhenUserIsResourceOwner() throws Exception {
        getByEmailAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = { "TEAM_MANAGER", "INTERNAL" })
    public void shouldDenyAccessToClientByEmailWhenUserIsNotTopManager() throws Exception {
        getByEmailAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "mark@gmail.com", authorities = "USER")
    public void shouldDenyAccessToClientByEmailWhenUserIsNotResourceOwner() throws Exception {
        getByEmailAndExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyAccessToClientByEmailWhenUserIsNotAuthenticated() throws Exception {
        getByEmailAndExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
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
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnUpdatedClientOnClientPatchRequestWhenUserIsTopManager() throws Exception {
        Client initial = clientService.findById(2).orElseThrow();
        patchByIdAndExpect(2, updateClient1Json, status().isOk());

        Client updated = clientService.findById(2).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    private void patchByIdAndExpect(long id, String data, ResultMatcher status) throws Exception {
        mvc.perform(patch("/clients/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "mark@gmail.com", authorities = "USER")
    public void shouldReturnUpdatedClientOnClientPatchRequestWhenUserIsResourceOwner() throws Exception {
        Client initial = clientService.findById(3).orElseThrow();
        patchByIdAndExpect(3, updateClient2Json, status().isOk());

        Client updated = clientService.findById(3).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    @Test
    @WithMockUser(authorities = { "TEAM_MANAGER", "DOCTOR", "INTERNAL" })
    public void shouldDenyClientPatchingWhenUserIsNotTopManager() throws Exception {
        patchByIdAndExpect(2, updateClient1Json, status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alexander@gmail.com", authorities = "USER")
    public void shouldDenyClientPatchingWhenUserIsNotResourceOwner() throws Exception {
        patchByIdAndExpect(2, updateClient1Json, status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyClientPatchingWhenUserIsNotAuthenticated() throws Exception {
        patchByIdAndExpect(2, updateClient1Json, status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnUpdatedClientOnClientStatusPatchRequestWhenUserIsTopManager() throws Exception {
        boolean initial = clientService.findById(3).orElseThrow().isEnabled();
        patchStatusAndExpect(status().isOk());

        boolean updated = clientService.findById(3).orElseThrow().isEnabled();
        assertThat(updated, is(not(equalTo(initial))));
    }

    private void patchStatusAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(patch("/clients/3/status").param("isActive", "false"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = { "TEAM_MANAGER", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyClientStatusPatchingWhenUserIsNotTopManager() throws Exception {
        patchStatusAndExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyClientStatusPatchingWhenUserIsNotAuthenticated() throws Exception {
        patchStatusAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldDeleteClientOnClientDeleteRequestWhenUserIsTopManager() throws Exception {
        deleteByIdAndExpect(4, status().isNoContent());

        Optional<Client> deleted = clientService.findById(4);
        assertThat(deleted, is(Optional.empty()));
    }

    private void deleteByIdAndExpect(long id, ResultMatcher status) throws Exception {
        mvc.perform(delete("/clients/" + id))
                .andDo(print())
                .andExpect(status);
    }

    @Test
    @WithMockUser(username = "thomas@gmail.com", authorities = "USER")
    public void shouldDeleteClientOnClientDeleteRequestWhenUserIsResourceOwner() throws Exception {
        deleteByIdAndExpect(5, status().isNoContent());

        Optional<Client> deleted = clientService.findById(5);
        assertThat(deleted, is(Optional.empty()));
    }

    @Test
    @WithMockUser(authorities = { "TEAM_MANAGER", "DOCTOR", "INTERNAL" })
    public void shouldDenyClientDeletionWhenUserIsNotTopManager() throws Exception {
        deleteByIdAndExpect(5, status().isForbidden());
    }

    @Test
    @WithMockUser(username = "mark@gmail.com", authorities = "USER")
    public void shouldDenyClientDeletionWhenUserIsNotResourceOwner() throws Exception {
       deleteByIdAndExpect(5, status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyClientDeletionWhenUserIsNotAuthenticated() throws Exception {
        deleteByIdAndExpect(5, status().isUnauthorized());
    }
}
