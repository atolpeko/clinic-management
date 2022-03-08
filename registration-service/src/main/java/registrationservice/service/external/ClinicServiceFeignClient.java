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

package registrationservice.service.external;

import feign.FeignException.FeignClientException;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Provides access to clinic microservice.
 */
@FeignClient("clinic-service")
public interface ClinicServiceFeignClient {

    /**
     * Looks for all doctors with the specified speciality.
     *
     * @param specialty speciality of the doctors to get
     *
     * @return all doctors with the specified speciality
     *
     * @throws FeignClientException if there is any problem with feign client
     */
    @RequestMapping(method = RequestMethod.GET,
            value = "",
            consumes = "application/json")
    List<Doctor> findAllDoctorsWithSpecialty(String specialty);
}
