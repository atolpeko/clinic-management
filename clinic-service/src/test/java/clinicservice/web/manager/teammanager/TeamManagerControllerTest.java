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

package clinicservice.web.manager.teammanager;

import clinicservice.service.employee.manager.teammanager.TeamManager;
import clinicservice.service.employee.manager.teammanager.TeamManagerService;

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
public class TeamManagerControllerTest {
    private static String newManagerJson;
    private static String updateManagerJson;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TeamManagerService managerService;

    @BeforeAll
    public static void createManagerJsons() {
        newManagerJson = "{\"name\": \"Alexander\"," +
                "\"hireDate\": \"2022-03-01\"," +
                "\"phone\": \"123456\"," +
                "\"sex\": \"MALE\"," +
                "\"dateOfBirth\": \"1995-01-22\"," +
                "\"salary\": 1000," +
                "\"department\" : { \"id\": 1 }," +
                "\"email\": \"team-manager@gmail.com\"," +
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
                "\"email\": \"team-manager2@gmail.com\"}";
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnManagersOnManagersGetRequestWhenUserIsTopManager() throws Exception {
        getAllAndExpect(status().isOk());
    }

    private void getAllAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/team-managers"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    public void shouldDenyAccessToAllManagersWhenUserIsNotTopManager() throws Exception {
        getAllAndExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnManagerOnManagerGetByEmailRequestWhenUserIsTopManager() throws Exception {
        getByEmailAndExpect(status().isOk());
    }

    private void getByEmailAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/team-managers").param("email", "oliver@gmail.com"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "oliver@gmail.com", authorities = "TEAM_MANAGER")
    public void shouldReturnManagerOnManagerGetByEmailRequestWhenUserIsResourceOwner() throws Exception {
        getByEmailAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "lucas@gmail.com", authorities = "TEAM_MANAGER")
    public void shouldDenyAccessToManagerByEmailWhenUserIsNotResourceOwnerManager() throws Exception {
        getByEmailAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnManagersOnManagersGetByDepartmentIdRequestWhenUserIsTopManager() throws Exception {
        getByDepartmentIdAndExpect(status().isOk());
    }

    private void getByDepartmentIdAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/team-managers").param("departmentId", "1"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    public void shouldDenyAccessToManagersByDepartmentIdWhenUserIsNotTopManager() throws Exception {
        getByDepartmentIdAndExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnManagerOnManagerGetByIdRequestWhenUserIsTopManager() throws Exception {
        getByIdAndExpect(status().isOk());
    }

    private void getByIdAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/team-managers/6"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "oliver@gmail.com", authorities = "TEAM_MANAGER")
    public void shouldReturnManagerOnManagerGetByIdRequestWhenUserIsResourceOwner() throws Exception {
        getByIdAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "lucas@gmail.com", authorities = "TEAM_MANAGER")
    public void shouldDenyAccessToManagerByIdWhenUserIsNotResourceOwner() throws Exception {
        getByDepartmentIdAndExpect(status().isUnauthorized());
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
        mvc.perform(post("/team-managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newManagerJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    public void shouldDenyManagerPostingWhenUserIsNotTopManger() throws Exception {
        postAndExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnUpdatedManagerOnManagerPatchRequestWhenUserIsTopManager() throws Exception {
        TeamManager initial = managerService.findById(7).orElseThrow();
        patchAndExpect(status().isOk());

        TeamManager updated = managerService.findById(7).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    private void patchAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(patch("/team-managers/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateManagerJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    public void shouldDenyManagerPatchingWhenUserIsNotTopManager() throws Exception {
        patchAndExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldDeleteManagerOnManagerDeleteRequestWhenUserIsTopManager() throws Exception {
        deleteAndExpect(status().isNoContent());

        Optional<TeamManager> deleted = managerService.findById(8);
        assertThat(deleted, is(Optional.empty()));
    }

    private void deleteAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(delete("/team-managers/8"))
                .andDo(print())
                .andExpect(status);
    }

    @Test
    @WithMockUser
    public void shouldDenyManagerDeletionWhenUserIsNotTopManager() throws Exception {
        getByDepartmentIdAndExpect(status().isForbidden());
    }
}
