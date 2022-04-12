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

package clinicservice.web.department;

import clinicservice.service.department.Department;
import clinicservice.service.department.DepartmentService;

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
public class DepartmentControllerTest {
    private static String newDepartmentJson;
    private static String updateDepartmentJson;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DepartmentService departmentService;

    @BeforeAll
    public static void createClientJsons() {
        newDepartmentJson = "{\"address\":{" +
                "\"country\":\"USA\"," +
                "\"state\":\"NY\"," +
                "\"city\":\"NYC\"," +
                "\"street\":\"23\"," +
                "\"houseNumber\":11" +
                "}}";

        updateDepartmentJson = "{\"address\":{" +
                "\"country\":\"USA\"," +
                "\"state\":\"LA\"," +
                "\"city\":\"California\"," +
                "\"street\":\"36\"," +
                "\"houseNumber\":1" +
                "}}";
    }

    @Test
    public void shouldReturnDepartmentsOnDepartmentsGetRequest() throws Exception {
        mvc.perform(get("/departments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnDepartmentOnDepartmentGetByIdRequest() throws Exception {
        mvc.perform(get("/departments/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnDepartmentsOnDepartmentGetByFacilityIdRequest() throws Exception {
        mvc.perform(get("/departments").param("facilityId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnSavedDepartmentOnDepartmentsPostRequestWhenUserIsTopManager() throws Exception {
        int initialCount = departmentService.findAll().size();
        postAndExpect(status().isCreated());

        int newCount = departmentService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    private void postAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(post("/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newDepartmentJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = { "ADMIN", "TEAM_MANAGER", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyDepartmentPostingWhenUserIsNotTopManager() throws Exception {
        postAndExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyDepartmentPostingWhenUserIsNotAuthorized() throws Exception {
        postAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnUpdatedDepartmentOnDepartmentPatchRequestWhenUserIsTopManager() throws Exception {
        Department initial = departmentService.findById(1).orElseThrow();
        patchAndExpect(status().isOk());

        Department updated = departmentService.findById(1).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    private void patchAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(patch("/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateDepartmentJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = { "ADMIN", "TEAM_MANAGER", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyDepartmentPatchingWhenUserIsNotTopManager() throws Exception {
        patchAndExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyDepartmentPatchingWhenUserIsNotAuthorized() throws Exception {
        patchAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldDeleteDepartmentOnDepartmentDeleteRequestWhenUserIsTopManager() throws Exception {
        deleteAndExpect(status().isNoContent());

        Optional<Department> deleted = departmentService.findById(3);
        assertThat(deleted, is(Optional.empty()));
    }

    private void deleteAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(delete("/departments/3"))
                .andDo(print())
                .andExpect(status);
    }

    @Test
    @WithMockUser(authorities = { "ADMIN", "TEAM_MANAGER", "DOCTOR", "USER", "INTERNAL" })
    public void shouldDenyDepartmentDeletionWhenUserIsNotTopManager() throws Exception {
        deleteAndExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyDepartmentDeletionWhenUserIsNotAuthorized() throws Exception {
        deleteAndExpect(status().isUnauthorized());
    }
}
