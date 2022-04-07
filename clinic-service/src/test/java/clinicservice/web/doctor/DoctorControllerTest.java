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

package clinicservice.web.doctor;

import clinicservice.service.employee.doctor.Doctor;
import clinicservice.service.employee.doctor.DoctorService;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@Tag("category.IntegrationTest")
@SpringBootTest(properties = "spring.cloud.config.enabled=false")
@AutoConfigureMockMvc
public class DoctorControllerTest {
    private static String newDoctor1Json;
    private static String newDoctor2Json;
    private static String updateDoctorJson;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DoctorService doctorService;

    @BeforeAll
    public static void createNewDoctorJsons() {
        newDoctor1Json = "{\"name\": \"Alexander\"," +
                "\"specialty\": \"Surgery\"," +
                "\"hireDate\": \"2022-03-01\"," +
                "\"phone\": \"123456\"," +
                "\"sex\": \"MALE\"," +
                "\"dateOfBirth\": \"1995-01-22\"," +
                "\"salary\": 1000," +
                "\"practiceBeginningDate\": \"2022-03-06\"," +
                "\"department\" : { \"id\": 1 }," +
                "\"email\": \"alexander@gmail.com\"," +
                "\"password\": \"12345678\"," +
                "\"address\":{" +
                    "\"country\":\"USA\"," +
                    "\"state\":\"NY\"," +
                    "\"city\":\"NYC\"," +
                    "\"street\":\"23\"," +
                    "\"houseNumber\":11" +
                "}}";

        newDoctor2Json = "{\"name\": \"Alexander\"," +
                "\"specialty\": \"Surgery\"," +
                "\"hireDate\": \"2022-03-01\"," +
                "\"phone\": \"123456\"," +
                "\"sex\": \"MALE\"," +
                "\"dateOfBirth\": \"1995-01-22\"," +
                "\"salary\": 1000," +
                "\"practiceBeginningDate\": \"2022-03-06\"," +
                "\"department\" : { \"id\": 1 }," +
                "\"email\": \"a@gmail.com\"," +
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
    private static void createUpdateDoctorJson() {
        updateDoctorJson = "{\"name\": \"Alex\"," +
                "\"specialty\": \"Emergency\"," +
                "\"phone\": \"654321\"," +
                "\"practiceBeginningDate\": \"2003-04-01\"}";
    }

    @Test
    public void shouldReturnDoctorsOnDoctorsGetRequest() throws Exception {
        mvc.perform(get("/doctors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnDoctorOnDoctorGetRequest() throws Exception {
        mvc.perform(get("/doctors/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnDoctorOnDoctorGetByEmailRequest() throws Exception {
        mvc.perform(get("/doctors").param("email", "alex@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnDoctorsOnDoctorsGetByDepartmentIdRequest() throws Exception {
        mvc.perform(get("/doctors").param("departmentId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnDoctorsOnDoctorsGetBySpecialtyRequest() throws Exception {
        mvc.perform(get("/doctors").param("specialty", "Surgery"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnSavedDoctorOnDoctorsPostRequestWhenUserIsTopManager() throws Exception {
        int initialCount = doctorService.findAll().size();
        postAndExpect(newDoctor1Json, status().isCreated());

        int newCount = doctorService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    private void postAndExpect(String data, ResultMatcher status) throws Exception {
        mvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = "TEAM_MANAGER")
    public void shouldReturnSavedDoctorOnDoctorsPostRequestWhenUserIsTeamManager() throws Exception {
        int initialCount = doctorService.findAll().size();
        postAndExpect(newDoctor2Json, status().isCreated());

        int newCount = doctorService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    @Test
    @WithMockUser
    public void shouldDenyDoctorPostingWhenUserIsNotTopManager() throws Exception {
        postAndExpect(newDoctor2Json, status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnUpdatedDoctorOnDoctorPatchRequestWhenUserIsTopManager() throws Exception {
        Doctor initial = doctorService.findById(2).orElseThrow();
        patchByIdAndExpect(2, status().isOk());

        Doctor updated = doctorService.findById(2).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    private void patchByIdAndExpect(long id, ResultMatcher status) throws Exception {
        mvc.perform(patch("/doctors/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateDoctorJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = "TEAM_MANAGER")
    public void shouldReturnUpdatedDoctorOnDoctorPatchRequestWhenUserIsTeamManager() throws Exception {
        Doctor initial = doctorService.findById(3).orElseThrow();
        patchByIdAndExpect(3, status().isOk());

        Doctor updated = doctorService.findById(3).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    @Test
    @WithMockUser
    public void shouldDenyDoctorPatchingWhenUserIsNotTopManager() throws Exception {
        patchByIdAndExpect(3, status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldDeleteDoctorOnDoctorDeleteRequestWhenUserIsTopManager() throws Exception {
        deleteByIdAndExpect(4, status().isNoContent());

        Optional<Doctor> deleted = doctorService.findById(4);
        assertThat(deleted, is(Optional.empty()));
    }

    private void deleteByIdAndExpect(long id, ResultMatcher status) throws Exception {
        mvc.perform(delete("/doctors/" + id))
                .andDo(print())
                .andExpect(status);
    }

    @Test
    @WithMockUser(authorities = "TEAM_MANAGER")
    public void shouldDeleteDoctorOnDoctorDeleteRequestWhenUserIsTeamManager() throws Exception {
        deleteByIdAndExpect(5, status().isNoContent());

        Optional<Doctor> deleted = doctorService.findById(5);
        assertThat(deleted, is(Optional.empty()));
    }

    @Test
    @WithMockUser
    public void shouldDenyDoctorDeletionWhenUserIsNotTopManager() throws Exception {
        deleteByIdAndExpect(5, status().isForbidden());
    }
}
