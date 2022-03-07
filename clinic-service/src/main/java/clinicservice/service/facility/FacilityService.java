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

package clinicservice.service.facility;

import clinicservice.service.exception.IllegalModificationException;
import clinicservice.service.exception.RemoteResourceException;

import java.util.List;
import java.util.Optional;

/**
 * Provides medical facility business logic.
 */
public interface FacilityService {

    /**
     * Looks for all medical facilities in the remote facility repository.
     *
     * @return all medical facilities from the remote facility repository
     *
     * @throws RemoteResourceException if there is any problem with the remote facility repository
     */
    List<MedicalFacility> findAll();

    /**
     * Looks for all medical facilities with the specified department ID in the remote facility repository.
     *
     * @param id ID of the department with medical facilities to get
     *
     * @return all medical facilities with the specified department ID in the remote facility repository.
     *
     * @throws RemoteResourceException if there is any problem with the remote facility repository
     */
    List<MedicalFacility> findAllByDepartmentId(Long id);

    /**
     * Looks for a medical facility with the specified ID in the remote facility repository.
     *
     * @param id ID of the facility to get
     *
     * @return the medical facility with the specified ID in the remote facility repository
     *
     * @throws RemoteResourceException if there is any problem with the remote facility repository
     */
    Optional<MedicalFacility> findById(long id);

    /**
     * Saves the specified medical facility in the remote facility repository.
     * Use the returned medical facility for further operations as the save operation
     * might have changed the medical facility instance completely.
     *
     * @param facility medical facility to save
     *
     * @return the saved medical facility
     *
     * @throws IllegalModificationException either if a medical facility has invalid data or already exists
     * @throws RemoteResourceException if there is any problem with the remote facility repository
     */
    MedicalFacility save(MedicalFacility facility);

    /**
     * Updates the medical facility with the specified ID in the remote facility repository.
     * Use the returned medical facility for further operations as the update operation
     * might have changed the medical facility instance completely.
     *
     * @param facility medical facility to update
     *
     * @return the updated medical facility
     *
     * @throws IllegalModificationException either if a medical facility has invalid data or does not exist
     * @throws RemoteResourceException if there is any problem with the remote facility repository
     */
    MedicalFacility update(MedicalFacility facility);

    /**
     * Deletes all medical facilities from the department with the specified ID.
     *
     * @param departmentId ID of the department with medical facilities to delete
     *
     * @throws IllegalModificationException if such a department does not exist
     * @throws RemoteResourceException if there is any problem with the remote facility repository
     */
    void deleteAllByDepartmentId(long departmentId);

    /**
     * Deletes the medical facility with the specified ID from the department with the specified ID.
     *
     * @param departmentId ID of the department with medical facility to delete
     * @param facilityId the ID of the medical facility to be deleted
     *
     * @throws IllegalModificationException either if such a facility or department does not exist
     * @throws RemoteResourceException if there is any problem with the remote facility repository
     */
    void deleteFromDepartmentById(long departmentId, long facilityId);

    /**
     * Deletes the medical facility with the specified ID in the remote facility repository.
     * Does nothing if such a medical facility does not exist.
     *
     * @param id the ID of the medical facility to be deleted
     *
     * @throws IllegalModificationException if such a facility does not exist
     * @throws RemoteResourceException if there is any problem with the remote facility repository
     */
    void deleteById(long id);
}
