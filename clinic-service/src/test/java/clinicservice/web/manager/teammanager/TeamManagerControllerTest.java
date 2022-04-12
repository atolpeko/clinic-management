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
public class TeamManagerControllerTest {
    private static String newManager1Json;
    private static String newManager2Json;
    private static String updateManager1Json;
    private static String updateManager2Json;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TeamManagerService managerService;

    @BeforeAll
    public static void createNewManagerJsons() {
        newManager1Json = "{\"name\": \"Alexander\"," +
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

        newManager2Json = "{\"name\": \"Alexander\"," +
                "\"hireDate\": \"2022-03-01\"," +
                "\"phone\": \"123456\"," +
                "\"sex\": \"MALE\"," +
                "\"dateOfBirth\": \"1995-01-22\"," +
                "\"salary\": 1000," +
                "\"department\" : { \"id\": 2 }," +
                "\"email\": \"team-manager2@gmail.com\"," +
                "\"password\": \"12345678\"," +
                "\"address\":{" +
                    "\"country\":\"USA\"," +
                    "\"state\":\"NY\"," +
                    "\"city\":\"NYC\"," +
                    "\"street\":\"23\"," +
                    "\"houseNumber\":11" +
                "}}";
    }

    @BeforeAll
    public static void createUpdateManagerJsons() {
        updateManager1Json = "{\"name\": \"Lucas\"," +
                "\"hireDate\": \"2022-03-01\"," +
                "\"phone\": \"234234\"," +
                "\"sex\": \"FEMALE\"," +
                "\"dateOfBirth\": \"2003-02-11\"," +
                "\"salary\": 1200," +
                "\"department\" : { \"id\": 2 }," +
                "\"email\": \"lucas-manager@gmail.com\"," +
                "\"password\": \"fhujff22df2242\"," +
                "\"address\":{" +
                    "\"country\":\"USA\"," +
                    "\"state\":\"NY\"," +
                    "\"city\":\"NYC\"," +
                    "\"street\":\"11\"," +
                    "\"houseNumber\":23" +
                "}}";

        updateManager2Json = "{\"name\": \"Emmeline\"," +
                "\"hireDate\": \"2022-03-01\"," +
                "\"phone\": \"234234\"," +
                "\"sex\": \"FEMALE\"," +
                "\"dateOfBirth\": \"2003-02-11\"," +
                "\"salary\": 1200," +
                "\"department\" : { \"id\": 2 }," +
                "\"email\": \"emmeline-manager@gmail.com\"," +
                "\"password\": \"dfdfdf2242\"," +
                "\"address\":{" +
                    "\"country\":\"USA\"," +
                    "\"state\":\"NY\"," +
                    "\"city\":\"NYC\"," +
                    "\"street\":\"11\"," +
                    "\"houseNumber\":23" +
                "}}";
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
    @WithMockUser(authorities = { "ADMIN", "TEAM_MANAGER", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyAccessToAllManagersWhenUserIsNotTopManager() throws Exception {
        getAllAndExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyAccessToAllManagesWhenUserIsNotAuthenticated() throws Exception {
        getAllAndExpect( status().isUnauthorized());
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
    @WithMockUser(authorities = { "ADMIN", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyAccessToManagerByEmailWhenUserIsNotTopManager() throws Exception {
        getByEmailAndExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "lucas@gmail.com", authorities = "TEAM_MANAGER")
    public void shouldDenyAccessToManagersByEmailWhenUserIsNotResourceOwner() throws Exception {
        getByEmailAndExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyAccessToManageByEmailWhenUserIsNotAuthenticated() throws Exception {
        getByEmailAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnManagersOnManagerGetByDepartmentIdRequestWhenUserIsTopManager() throws Exception {
        getByDepartmentIdAndExpect(status().isOk());
    }

    private void getByDepartmentIdAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/team-managers").param("departmentId", "1"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = { "ADMIN", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyAccessToManagersByDepartmentIdWhenUserIsNotTopManager() throws Exception {
        getByDepartmentIdAndExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyAccessToManagersByDepartmentIdlWhenUserIsNotAuthenticated() throws Exception {
        getByDepartmentIdAndExpect(status().isUnauthorized());
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
    @WithMockUser(authorities = { "ADMIN", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyAccessToManagerByIdWhenUserIsNotTopManager() throws Exception {
        getByDepartmentIdAndExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "lucas@gmail.com", authorities = "TEAM_MANAGER")
    public void shouldDenyAccessToManagerByIdWhenUserIsNotResourceOwner() throws Exception {
        getByDepartmentIdAndExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyAccessToManagerByIdlWhenUserIsNotAuthenticated() throws Exception {
        getByDepartmentIdAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnSavedManagerOnManagersPostRequestWhenUserIsTopManager() throws Exception {
        int initialCount = managerService.findAll().size();
        postAndExpect(newManager1Json, status().isCreated());

        int newCount = managerService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    private void postAndExpect(String data, ResultMatcher status) throws Exception {
        mvc.perform(post("/team-managers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnSavedManagerOnManagersPostRequestWhenUserIsTeamManager() throws Exception {
        int initialCount = managerService.findAll().size();
        postAndExpect(newManager2Json, status().isCreated());

        int newCount = managerService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    @Test
    @WithMockUser(authorities = { "ADMIN", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyManagerPostingWhenUserIsNotTopMangerOrTeamManager() throws Exception {
        postAndExpect(newManager1Json, status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyManagerPostingWhenUserIsNotAuthenticated() throws Exception {
        postAndExpect(newManager1Json, status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnUpdatedManagerOnManagerPatchRequestWhenUserIsTopManager() throws Exception {
        TeamManager initial = managerService.findById(7).orElseThrow();
        patchByIdAndExpect(7, updateManager1Json, status().isOk());

        TeamManager updated = managerService.findById(7).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    private void patchByIdAndExpect(long id, String data, ResultMatcher status) throws Exception {
        mvc.perform(patch("/team-managers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "emma@gmail.com", authorities = "TEAM_MANAGER")
    public void shouldReturnUpdatedManagerOnManagerPatchRequestWhenUserIsResourceOwner()
            throws Exception {
        TeamManager initial = managerService.findById(8).orElseThrow();
        patchByIdAndExpect(8, updateManager2Json, status().isOk());

        TeamManager updated = managerService.findById(8).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    @Test
    @WithMockUser(authorities = { "ADMIN", "TEAM_MANAGER", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyManagerPatchingWhenUserIsNotTopManager() throws Exception {
        patchByIdAndExpect(8, newManager1Json, status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "oliver@gmail.com", authorities = "TEAM_MANAGER")
    public void shouldDenyManagerPatchingWhenUserIsNotResourceOwner() throws Exception {
        patchByIdAndExpect(8, newManager1Json, status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyManagerPatchingWhenUserIsNotAuthenticated() throws Exception {
        patchByIdAndExpect(8, newManager1Json, status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldDeleteManagerOnManagerDeleteRequestWhenUserIsTopManager() throws Exception {
        deleteByIdAndExpect(9, status().isNoContent());

        Optional<TeamManager> deleted = managerService.findById(9);
        assertThat(deleted, is(Optional.empty()));
    }

    private void deleteByIdAndExpect(long id, ResultMatcher status) throws Exception {
        mvc.perform(delete("/team-managers/" + id))
                .andDo(print())
                .andExpect(status);
    }

    @Test
    @WithMockUser(authorities = { "ADMIN", "TEAM_MANAGER", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyManagerDeletionWhenUserIsNotTopManager() throws Exception {
        deleteByIdAndExpect(9, status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyManagerDeletionWhenUserIsNotAuthenticated() throws Exception {
        deleteByIdAndExpect(9, status().isUnauthorized());
    }
}
