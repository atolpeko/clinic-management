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

import clinicservice.service.doctor.Doctor;
import clinicservice.service.doctor.DoctorService;

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
public class DoctorControllerTest {
    private static String newDoctorJson;
    private static String updateDoctorJson;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DoctorService doctorService;

    @BeforeAll
    public static void createClientJsons() {
        newDoctorJson = "{\"name\": \"Alexander\"," +
                "\"specialty\": \"Surgery\"," +
                "\"practiceBeginningDate\": \"2022-03-06\"," +
                "\"department\" : { \"id\": 1 }}";

        updateDoctorJson = "{\"name\": \"Alex\"," +
                "\"specialty\": \"Emergency\"," +
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
    public void shouldReturnDoctorsOnDoctorsGetByDepartmentIdRequest() throws Exception {
        mvc.perform(get("/doctors").param("departmentId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnSavedDoctorOnDoctorsPostRequest() throws Exception {
        int initialCount = doctorService.findAll().size();
        mvc.perform(post("/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newDoctorJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        int newCount = doctorService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    @Test
    public void shouldReturnUpdatedDoctorOnDoctorPatchRequest() throws Exception {
        Doctor initial = doctorService.findById(1).orElseThrow();
        mvc.perform(patch("/doctors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateDoctorJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        Doctor updated = doctorService.findById(1).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    @Test
    public void shouldDeleteDoctorOnDoctorDeleteRequest() throws Exception {
        mvc.perform(delete("/doctors/2"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Optional<Doctor> deleted = doctorService.findById(2);
        assertThat(deleted, is(Optional.empty()));
    }
}
