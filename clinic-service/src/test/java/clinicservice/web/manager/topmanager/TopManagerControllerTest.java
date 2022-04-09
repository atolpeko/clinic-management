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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("category.IntegrationTest")
@SpringBootTest(properties = "spring.cloud.config.enabled=false")
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
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnManagersOnManagersGetRequestWhenUserIsTopManager() throws Exception {
        getAllAndExpect(status().isOk());
    }

    private void getAllAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/top-managers"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = { "TEAM_MANAGER", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyAccessToManagersWhenUserIsNotTopManager() throws Exception {
        getAllAndExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyAccessToManagersWhenUserIsNotAuthorized() throws Exception {
        getAllAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnManagerOnManagerGetRequest() throws Exception {
        getAndExpect(status().isOk());
    }

    private void getAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/top-managers/10"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = { "TEAM_MANAGER", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyAccessToManagerByIdWhenUserIsNotTopManager() throws Exception {
        getAndExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyAccessToManagerByIdWhenUserIsNotAuthorized() throws Exception {
        getAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnManagerOnManagerGetByEmailRequestWhenUserIsTopManager() throws Exception {
        getByEmailAndExpect(status().isOk());
    }

    private void getByEmailAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/top-managers").param("email", "evelyn@gmail.com"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = { "TEAM_MANAGER", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyAccessToManagerByEmailWhenUserIsNotTopManager() throws Exception {
        getByEmailAndExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyAccessToManagerByEmailWhenUserIsNotAuthorized() throws Exception {
        getByEmailAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnSavedManagerOnManagersPostRequestWhenUserIsTopManager() throws Exception {
        int initialCount = managerService.findAll().size();
        postAndExpect(status().isCreated());

        int newCount = managerService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    private void postAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(post("/top-managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newManagerJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = { "TEAM_MANAGER", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyManagerPostingWhenUserIsNotTopManager() throws Exception {
        postAndExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyManagerPostingWhenUserIsNotAuthorized() throws Exception {
        postAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "william@gmail.com", authorities = "TOP_MANAGER")
    public void shouldReturnUpdatedManagerOnManagerPatchRequestWhenUserIsResourceOwner() throws Exception {
        TopManager initial = managerService.findById(11).orElseThrow();
        patchAndExpect(status().isOk());

        TopManager updated = managerService.findById(11).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    private void patchAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(patch("/top-managers/11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateManagerJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = { "TEAM_MANAGER", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyManagerPathingWhenUserIsNotTopManager() throws Exception {
        patchAndExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "james@gmail.com", authorities = "TOP_MANAGER")
    public void shouldDenyManagerPatchingWhenUserIsNotResourceOwner() throws Exception {
        patchAndExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyManagerPatchingWhenUserIsNotAuthorized() throws Exception {
        patchAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "james@gmail.com", authorities = "TOP_MANAGER")
    public void shouldDeleteManagerOnManagerDeleteRequestWhenUserIsResourceOwner() throws Exception {
        deleteAndExpect(status().isNoContent());

        Optional<TopManager> deleted = managerService.findById(12);
        assertThat(deleted, is(Optional.empty()));
    }

    private void deleteAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(delete("/top-managers/12"))
                .andDo(print())
                .andExpect(status);
    }

    @Test
    @WithMockUser(username = "william@gmail.com", authorities = "TOP_MANAGER")
    public void shouldDenyManagerDeletionWhenUserIsNotResourceOwner() throws Exception {
        deleteAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = { "TEAM_MANAGER", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyManagerDeletionWhenUserIsNotTopManager() throws Exception {
        deleteAndExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyManagerDeletionWhenUserIsNotAuthorized() throws Exception {
        deleteAndExpect(status().isUnauthorized());
    }
}
