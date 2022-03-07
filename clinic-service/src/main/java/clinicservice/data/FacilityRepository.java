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

import clinicservice.service.facility.MedicalFacility;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A FacilityRepository abstracts a collection of MedicalFacility objects.
 */
@Repository
public interface FacilityRepository extends JpaRepository<MedicalFacility, Long> {

    /**
     * Retrieves all facilities with the specified department ID.
     *
     * @param departmentId department ID of the facilities to get
     *
     * @return all facilities with the given department ID
     */
    @Query("SELECT d.facilities FROM Department d WHERE d.id = ?1")
    List<MedicalFacility> findAllByDepartmentId(long departmentId);

    /**
     * Deletes all medical facilities from the department with the specified ID.
     *
     * @param departmentId ID of the department with medical facilities to delete
     */
    @Query(nativeQuery = true, value = "DELETE FROM department_facility " +
            "WHERE department_id = ?1")
    @Modifying
    void deleteAllByDepartmentId(long departmentId);

    /**
     * Deletes the facility with the specified ID from the department with the specified ID.
     *
     * @param departmentId ID of the department with medical facility to delete
     * @param facilityId the ID of the medical facility to be deleted
     */
    @Query(nativeQuery = true, value = "DELETE FROM department_facility " +
            "WHERE department_id = ?1 AND facility_id = ?2")
    @Modifying
    void deleteFromDepartment(long departmentId, long facilityId);
}
