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

package discoveryserver;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("category.IntegrationTest")
@SpringBootTest(properties = "spring.cloud.config.enabled=false")
@AutoConfigureMockMvc
public class ActuatorTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void shouldAllowAccessToActuatorWhenUserIsAdmin() throws Exception {
        getActuatorAndExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "INTERNAL")
    public void shouldAllowAccessToActuatorWhenUserIsInternal() throws Exception {
        getActuatorAndExpect(status().isOk());
    }

    private void getActuatorAndExpect(ResultMatcher status) throws Exception {
        mvc.perform(get("/actuator/"))
                .andDo(print())
                .andExpect(status);
    }

    @Test
    @WithMockUser(authorities = { "TOP_MANAGER", "TEAM_MANAGER", "DOCTOR", "USER" })
    public void shouldDenyAccessToActuatorWhenUserIsNotAdminOrInternal() throws Exception {
        getActuatorAndExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void shouldDenyAccessToActuatorWhenUserIsNotAuthenticated() throws Exception {
        getActuatorAndExpect(status().isUnauthorized());
    }
}
