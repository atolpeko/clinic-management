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

package clinicservice.data;

import clinicservice.service.doctor.Doctor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A DoctorRepository abstracts a collection of Doctor objects.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Retrieves all doctors with the specified department ID.
     *
     * @param departmentId department ID of the doctors to get
     *
     * @return all doctors with the given department ID
     */
    List<Doctor> findAllByDepartmentId(long departmentId);
}
