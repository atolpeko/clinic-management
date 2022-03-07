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

package clinicservice.web.facility;

import clinicservice.service.department.Department;
import clinicservice.service.facility.FacilityService;
import clinicservice.service.facility.MedicalFacility;

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
public class FacilityControllerTest {
    private static String newFacilityJson;
    private static String updateFacilityJson;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private FacilityService facilityService;

    @BeforeAll
    public static void createClientJsons() {
        newFacilityJson = "{\"name\": \"Facility1\"," +
                "\"departments\" : [{ \"id\": 1 }]}";

        updateFacilityJson = "{\"name\": \"Alex\"}";
    }

    @Test
    public void shouldReturnFacilityOnFacilitiesGetRequest() throws Exception {
        mvc.perform(get("/facilities"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnFacilityOnFacilityGetRequest() throws Exception {
        mvc.perform(get("/facilities/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnFacilitiesOnFacilitiesGetByDepartmentIdRequest() throws Exception {
        mvc.perform(get("/facilities").param("departmentId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnSavedFacilityOnFacilitiesPostRequest() throws Exception {
        int initialCount = facilityService.findAll().size();
        mvc.perform(post("/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newFacilityJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        int newCount = facilityService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    @Test
    public void shouldReturnUpdatedFacilityOnFacilitiesPatchRequest() throws Exception {
        MedicalFacility initial = facilityService.findById(1).orElseThrow();
        mvc.perform(patch("/facilities/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateFacilityJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        MedicalFacility updated = facilityService.findById(1).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    @Test
    public void shouldDeleteFacilityFromDepartmentOnFacilitiesDeleteRequest() throws Exception {
        mvc.perform(delete("/facilities/")
                        .param("departmentId", "2")
                        .param("facilityId", "3"))
                .andDo(print())
                .andExpect(status().isNoContent());

        MedicalFacility facility = facilityService.findById(3).orElseThrow();
        Optional<Department> deleted = facility.getDepartments().stream()
                        .filter(department -> department.getId() == 2)
                        .findAny();
        assertThat(deleted, is(Optional.empty()));
    }
}
