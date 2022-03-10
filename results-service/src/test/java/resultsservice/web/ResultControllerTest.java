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
import org.springframework.test.web.servlet.MockMvc;

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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.cloud.config.enabled=false")
@AutoConfigureMockMvc
public class ResultControllerTest {
    private static String newResultJson;
    private static String updatedJson;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ResultService resultService;

    @BeforeAll
    public static void createResultJson() {
        newResultJson = "{\"data\":\"Data1\"," +
                "\"dutyId\":1," +
                "\"clientId\":1," +
                "\"doctorId\":1}";

        updatedJson = "{\"data\":\"Data2\"," +
                "\"dutyId\":2," +
                "\"clientId\":2," +
                "\"doctorId\":2}";
    }

    @Test
    public void shouldReturnResultOnResultsGetRequest() throws Exception {
        mvc.perform(get("/results"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnResultOnResultGetRequest() throws Exception {
        mvc.perform(get("/results/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnResultOnResultGetByClientIdRequest() throws Exception {
        mvc.perform(get("/results").param("clientId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void shouldReturnSavedResultOnResultsPostRequest() throws Exception {
        int initialCount = resultService.findAll().size();
        mvc.perform(post("/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newResultJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        int newCount = resultService.findAll().size();
        assertThat(newCount, is(initialCount + 1));
    }

    @Test
    public void shouldReturnUpdatedResultOnResultPatchRequest() throws Exception {
        Result initial = resultService.findById(2).orElseThrow();
        mvc.perform(patch("/results/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        Result updated = resultService.findById(2).orElseThrow();
        assertThat(updated, is(not(equalTo(initial))));
    }

    @Test
    public void shouldDeleteResultOnResultDeleteRequest() throws Exception {
        mvc.perform(delete("/results/3"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Optional<Result> deleted = resultService.findById(3);
        assertThat(deleted, is(Optional.empty()));
    }
}
