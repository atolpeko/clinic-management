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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.ResultMatcher;

import registrationservice.IntegrationTestConfig;
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
@SpringBootTest(properties = "spring.cloud.config.enabled=false")
@ContextConfiguration(classes = IntegrationTestConfig.class)
@AutoConfigureMockMvc
public class RegistrationControllerTest {
    private static String newRegistrationJson;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RegistrationService registrationService;

    @BeforeAll
    public static void createRegistrationJson() {
        newRegistrationJson = "{\"date\": \"2022-03-08T00:00:00\"," +
                "\"duty\": { \"id\": 1  }," +
                "\"clientId\": 1," +
                "\"doctorId\": 1}";
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnAllRegistrationsOnRegistrationsGetRequestWhenUserIsTopManager() throws Exception {
        getAllAndExpect(status().isOk());
    }

    private void getAllAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/registrations"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    public void shouldDenyAccessToRegistrationsWhenUserIsNotTopManager() throws Exception {
        getAllAndExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnRegistrationOnRegistrationGetByIdRequestWhenUserIsTopManager() throws Exception {
        getAndExpect(status().isOk());
    }

    private void getAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/registrations/1"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "emma@gmail.com", authorities = "USER")
    public void shouldReturnRegistrationOnRegistrationGetByIdRequestWhenUserIsAuthorizedAndResourceOwner()
            throws Exception {
        getAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "mark@gmail.com", authorities = "DOCTOR")
    public void shouldReturnRegistrationOnRegistrationGetByIdRequestWhenUserIsDoctorAndResourceOwner()
            throws Exception {
        getAndExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void shouldDenyAccessToRegistrationByIdWhenUserIsNotResourceOwner() throws Exception {
        getAndExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnRegistrationOnRegistrationGetByDoctorIdRequestWhenUserIsTopManager()
            throws Exception {
        getByDoctorIdAndExpect(status().isOk());
    }

    private void getByDoctorIdAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/registrations").param("doctorId", "1"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "mark@gmail.com", authorities = "DOCTOR")
    public void shouldReturnRegistrationOnRegistrationGetByDoctorIdRequestWhenUserIsResourceOwner()
            throws Exception {
        getByDoctorIdAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "robert@gmail.com", authorities = "DOCTOR")
    public void shouldDenyAccessToRegistrationsByDoctorIdWhenUserIsNotResourceOwner() throws Exception {
        getByDoctorIdAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnRegistrationOnRegistrationGetByClientIdRequestWhenUserIsTopManager()
            throws Exception {
        getByClientIdAndExpect(status().isOk());
    }

    private void getByClientIdAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/registrations").param("clientId", "1"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "mark@gmail.com", authorities = "DOCTOR")
    public void shouldReturnRegistrationOnRegistrationGetByClientIdRequestWhenUserIsDoctorAndResourceOwner()
            throws Exception {
        getByClientIdAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "emma@gmail.com", authorities = "USER")
    public void shouldReturnRegistrationOnRegistrationGetByClientIdWhenUserAuthenticatedAndResourceOwner()
            throws Exception {
        getByClientIdAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "jain@gmail.com", authorities = "USER")
    public void shouldDenyAccessToRegistrationsByClientIdWhenUserIsNotResourceOwner() throws Exception {
        getByClientIdAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void shouldReturnSavedRegistrationOnRegistrationsPostRequestWhenUserIsAuthenticated()
            throws Exception {
        int initialCount = registrationService.findAll().size();
        postAndExpect(status().isCreated());

        int newCount = registrationService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    private void postAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(post("/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newRegistrationJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldDenyRegistrationPostingWhenUserIsNotAuthenticated() throws Exception {
        postAndExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnUpdatedRegistrationOnRegistrationStatusPatchRequestWhenUserIsTopManager()
            throws Exception {
        boolean initial = registrationService.findById(2).orElseThrow().isActive();
        patchStatusByIdAndExpect(2, status().isOk());

        boolean updated = registrationService.findById(2).orElseThrow().isActive();
        assertThat(updated, is(not(equalTo(initial))));
    }

    private void patchStatusByIdAndExpect(long id, ResultMatcher status) throws Exception {
        mvc.perform(patch("/registrations/" + id + "/status")
                        .param("isActive", "false"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "robert@gmail.com", authorities = "DOCTOR")
    public void shouldReturnUpdatedRegistrationOnRegistrationStatusPatchRequestWhenUserIsDoctorAndResourceOwner()
            throws Exception {
        boolean initial = registrationService.findById(3).orElseThrow().isActive();
        patchStatusByIdAndExpect(3, status().isOk());

        boolean updated = registrationService.findById(3).orElseThrow().isActive();
        assertThat(updated, is(not(equalTo(initial))));
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldDeleteRegistrationOnRegistrationDeleteRequestWhenUserIsTopManager() throws Exception {
        deleteAndExpect(status().isNoContent());

        Optional<Registration> deleted = registrationService.findById(4);
        assertThat(deleted, is(Optional.empty()));
    }

    private void deleteAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(delete("/registrations/4"))
                .andDo(print())
                .andExpect(status);
    }

    @Test
    @WithMockUser
    public void shouldDenyRegistrationDeletionWhenUserIsNotTopManager() throws Exception {
        deleteAndExpect(status().isForbidden());
    }
}
