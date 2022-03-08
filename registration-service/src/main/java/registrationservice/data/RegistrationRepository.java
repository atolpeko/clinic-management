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

package registrationservice.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import registrationservice.service.registration.Registration;

import java.util.List;

/**
 * A RegistrationRepository abstracts a collection of Registration objects.
 */
@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    /**
     * Retrieves all registrations with the specified client ID.
     *
     * @param clientId client ID of registration to get
     *
     * @return all registrations with the specified client ID
     */
    List<Registration> findAllByClientId(long clientId);

    /**
     * Retrieves all registrations with the specified doctor ID.
     *
     * @param doctorId doctor ID of registration to get
     *
     * @return all registrations with the specified doctor ID
     */
    List<Registration> findAllByDoctorId(long doctorId);

    /**
     * Retrieves all registrations with the specified duty ID.
     *
     * @param dutyId duty ID of registration to get
     *
     * @return all registrations with the specified duty ID
     */
    List<Registration> findAllByDutyId(long dutyId);
}
