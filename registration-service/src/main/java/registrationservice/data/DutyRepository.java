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

import registrationservice.service.duty.Duty;

import java.util.Optional;

/**
 * A DutyRepository abstracts a collection of Duty objects.
 */
@Repository
public interface DutyRepository extends JpaRepository<Duty, Long> {

    /**
     * Retrieves a duty with the specified name.
     *
     * @param name name of the duty to get
     *
     * @return the duty with the specified name or Optional#empty() if none found
     */
    Optional<Duty> findByName(String name);
}
