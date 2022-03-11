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

package clinicservice.service.doctor;

import clinicservice.service.exception.IllegalModificationException;
import clinicservice.service.exception.RemoteResourceException;

import java.util.List;
import java.util.Optional;

/**
 * Provides doctor business logic.
 */
public interface DoctorService {

    /**
     * Looks for all doctors in the remote doctor repository.
     *
     * @return all doctors from the remote doctor repository
     *
     * @throws RemoteResourceException if there is any problem with the remote doctor repository
     */
    List<Doctor> findAll();

    /**
     * Looks for all doctors with the specified department ID in the remote doctor repository.
     *
     * @param id ID of the department with doctors to get
     *
     * @return all doctors with the specified department ID in the remote doctor repository
     *
     * @throws RemoteResourceException if there is any problem with the remote doctor repository
     */
    List<Doctor> findAllByDepartmentId(Long id);

    /**
     * Looks for all doctors with the specified specialty in the remote doctor repository.
     *
     * @param specialty specialty of the doctors to get
     *
     * @return all doctors with the specified specialty in the remote doctor repository
     *
     * @throws RemoteResourceException if there is any problem with the remote doctor repository
     */
    List<Doctor> findAllBySpecialty(String specialty);

    /**
     * Looks for a doctor with the specified ID in the remote doctor repository.
     *
     * @param id ID of the doctor to get
     *
     * @return the doctor with the specified ID in the remote doctor repository
     * or Optional#empty() if none found
     *
     * @throws RemoteResourceException if there is any problem with the remote doctor repository
     */
    Optional<Doctor> findById(long id);

    /**
     * Saves the specified doctor in the remote doctor repository.
     * Use the returned doctor for further operations as the save operation
     * might have changed the doctor instance completely.
     *
     * @param doctor doctor to save
     *
     * @return the saved doctor
     *
     * @throws IllegalModificationException either if a doctor has invalid data or already exists
     * @throws RemoteResourceException if there is any problem with the remote doctor repository
     */
    Doctor save(Doctor doctor);

    /**
     * Updates the doctor with the specified ID in the remote doctor repository.
     * Use the returned doctor for further operations as the update operation
     * might have changed the doctor instance completely.
     *
     * @param doctor doctor to update
     *
     * @return the updated doctor
     *
     * @throws IllegalModificationException either if a doctor has invalid data or does not exist
     * @throws RemoteResourceException if there is any problem with the remote doctor repository
     */
    Doctor update(Doctor doctor);

    /**
     * Deletes the doctor with the specified ID in the remote doctor repository.
     *
     * @param id the ID of the doctor to be deleted
     *
     * @throws IllegalModificationException if such a doctor does not exist
     * @throws RemoteResourceException if there is any problem with the remote doctor repository
     */
    void deleteById(long id);
}
