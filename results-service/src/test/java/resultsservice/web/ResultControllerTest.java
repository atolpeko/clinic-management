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

package resultsservice.web;

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

import resultsservice.IntegrationTestConfig;
import resultsservice.service.result.Result;
import resultsservice.service.result.ResultService;

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
public class ResultControllerTest {
    private static String newResult1Json;
    private static String newResult2Json;
    private static String update1Json;
    private static String update2Json;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ResultService resultService;

    @BeforeAll
    public static void createNewResultJsons() {
        newResult1Json = "{\"data\":\"New Data 1\"," +
                "\"dutyId\":1," +
                "\"clientId\":2," +
                "\"doctorId\":1}";

        newResult2Json = "{\"data\":\"New Data 2\"," +
                "\"dutyId\":2," +
                "\"clientId\":1," +
                "\"doctorId\":2}";
    }

    @BeforeAll
    public static void createUpdateResultJsons() {
        update1Json = "{\"data\":\"Update 1\"," +
                "\"dutyId\":2," +
                "\"clientId\":1," +
                "\"doctorId\":2}";

        update2Json = "{\"data\":\"Update 2\"," +
                "\"dutyId\":1," +
                "\"clientId\":2," +
                "\"doctorId\":3}";
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnResultOnResultsGetAllRequestWhenUserIsTopManager() throws Exception {
        getAllAndExpect(status().isOk());
    }

    private void getAllAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/results"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    public void shouldDenyAccessToAllResultsWhenUserIsNotTopManager() throws Exception {
        getAllAndExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnResultOnResultGetByIdRequestWhenUserIsTopManager() throws Exception {
        getByIdAndExpect(status().isOk());
    }

    private void getByIdAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/results/1"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "mark@gmail.com", authorities = "DOCTOR")
    public void shouldReturnResultOnResultGetByIdRequestWhenUserIsDoctorAndResourceOwner()
            throws Exception {
        getByIdAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "emma@gmail.com", authorities = "USER")
    public void shouldReturnResultOnResultGetByIdRequestWhenUserIsAuthenticatedAndResourceOwner()
            throws Exception {
        getByIdAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "robert@gmail.com", authorities = "DOCTOR")
    public void shouldDenyAccessToResultsByIdWhenUserIsNotResourceOwner() throws Exception {
        getByIdAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnResultsOnResultGetByClientIdRequestWhenUserIsTopManger()
            throws Exception {
        getByClientIdAndExpect(status().isOk());
    }

    private void getByClientIdAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/results").param("clientId", "1"))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "mark@gmail.com", authorities = "DOCTOR")
    public void shouldReturnResultsOnResultGetByClientIdRequestWhenUserIsDoctorAndResourceOwner()
            throws Exception {
        getByClientIdAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "emma@gmail.com", authorities = "USER")
    public void shouldReturnResultsOnResultGetByClientIdRequestWhenUserIsAuthenticatedAndResourceOwner()
            throws Exception {
        getByClientIdAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "jain@gmail.com", authorities = "DOCTOR")
    public void shouldAccessToResultByClientIdWhenUserIsNotResourceOwner() throws Exception {
        getByClientIdAndExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnSavedResultOnResultsPostRequestWhenUserIsTopManager() throws Exception {
        int initialCount = resultService.findAll().size();
        postAndExpect(newResult1Json, status().isCreated());

        int newCount = resultService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    private void postAndExpect(String data, ResultMatcher status) throws Exception {
        mvc.perform(post("/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnSavedResultOnResultsPostRequestWhenUserIsDoctorAndResourceOwner()
            throws Exception {
        int initialCount = resultService.findAll().size();
        postAndExpect(newResult2Json, status().isCreated());

        int newCount = resultService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    @Test
    @WithMockUser(username = "robert@gmail.com", authorities = "DOCTOR")
    public void shouldDenyResultPostingWhenUserIsNotResourceOwner() throws Exception {
        postAndExpect(newResult1Json, status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldReturnUpdatedResultOnResultPatchRequestWhenUserIsTopManager() throws Exception {
        Result initial = resultService.findById(2).orElseThrow();
        patchByIdAndExpect(2, update1Json, status().isOk());

        Result updated = resultService.findById(2).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    private void patchByIdAndExpect(long id, String data, ResultMatcher status) throws Exception {
        mvc.perform(patch("/results/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "mark@gmail.com", authorities = "DOCTOR")
    public void shouldReturnUpdatedResultOnResultPatchRequestWhenUserIsDoctorAndResourceOwner()
            throws Exception {
        Result initial = resultService.findById(3).orElseThrow();
        patchByIdAndExpect(3, update2Json, status().isOk());

        Result updated = resultService.findById(3).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    @Test
    @WithMockUser(username = "robert@gmail.com", authorities = "DOCTOR")
    public void shouldDenyResultPatchingWhenUserIsNotResourceOwner() throws Exception {
        patchByIdAndExpect(3, update2Json, status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "TOP_MANAGER")
    public void shouldDeleteResultOnResultDeleteRequestWhenUserIsTopManager() throws Exception {
        deleteByIdAndExpect(4, status().isNoContent());

        Optional<Result> deleted = resultService.findById(4);
        assertThat(deleted, is(Optional.empty()));
    }

    private void deleteByIdAndExpect(long id, ResultMatcher status) throws Exception {
        mvc.perform(delete("/results/" + id))
                .andDo(print())
                .andExpect(status);
    }

    @Test
    @WithMockUser(username = "robert@gmail.com", authorities = "DOCTOR")
    public void shouldDeleteResultOnResultDeleteRequestWhenUserIsDoctorAndResourceOwner() throws Exception {
        deleteByIdAndExpect(5, status().isNoContent());

        Optional<Result> deleted = resultService.findById(5);
        assertThat(deleted, is(Optional.empty()));
    }

    @Test
    @WithMockUser(username = "mark@gmail.com", authorities = "DOCTOR")
    public void shouldDenyResultDeletionWhenUserIsNotResourceOwner() throws Exception {
        deleteByIdAndExpect(5, status().isUnauthorized());
    }
}
