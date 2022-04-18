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

package employeeservice.service.external;

import employeeservice.config.OauthFeignConfiguration;

import feign.FeignException;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;

/**
 * Provides access to clinic microservice.
 */
@FeignClient(name = "clinic-service", configuration = OauthFeignConfiguration.class)
public interface ClinicServiceFeignClient {

    /**
     * Looks for a department with the specified ID.
     *
     * @param id ID of the department to get
     *
     * @return department with the specified ID or Optional#empty() if none found
     *
     * @throws FeignException.FeignClientException if there is any problem with feign client
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "/departments/{id}",
            consumes = "application/json")
    Optional<Department> findDepartmentById(@PathVariable Long id);
}
