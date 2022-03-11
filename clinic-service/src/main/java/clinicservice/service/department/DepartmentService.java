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

package clinicservice.service.department;

import clinicservice.service.exception.IllegalModificationException;
import clinicservice.service.exception.RemoteResourceException;

import java.util.List;
import java.util.Optional;

/**
 * Provides department business logic.
 */
public interface DepartmentService {

    /**
     * Looks for all departments in the remote department repository.
     *
     * @return all departments from the remote department repository.
     *
     * @throws RemoteResourceException if there is any problem with the remote department repository
     */
    List<Department> findAll();

    /**
     * Looks for a department with the specified ID in the remote department repository.
     *
     * @param id ID of the department to get
     *
     * @return the department with the specified ID in the remote department repository
     * or Optional#empty() if none found
     *
     * @throws RemoteResourceException if there is any problem with the remote department repository
     */
    Optional<Department> findById(long id);

    /**
     * Looks for all departments that contain facilities with a specified ID
     * in the remote department repository.
     *
     * @param id ID of the facility that is contained in the department
     *
     * @return all departments that contain facilities with a specified ID
     * in the remote department repository.
     *
     * @throws RemoteResourceException if there is any problem with the remote department repository
     */
    List<Department> findAllByFacilityId(Long id);

    /**
     * Saves the specified department in the remote department repository.
     * Use the returned department for further operations as the save operation
     * might have changed the department instance completely.
     *
     * @param department department to save
     *
     * @return the saved department
     *
     * @throws IllegalModificationException either if a department has invalid data or already exists
     * @throws RemoteResourceException if there is any problem with the remote department repository
     */
    Department save(Department department);

    /**
     * Updates the department with the specified ID in the remote department repository.
     * Use the returned department for further operations as the update operation
     * might have changed the department instance completely.
     *
     * @param department department to update
     *
     * @return the updated department
     *
     * @throws IllegalModificationException either if a department has invalid data or does not exist
     * @throws RemoteResourceException if there is any problem with the remote department repository
     */
    Department update(Department department);

    /**
     * Deletes the department with the specified ID in the remote department repository.
     *
     * @param id the ID of the department to be deleted
     *
     * @throws IllegalModificationException if such a department does not exist
     * @throws RemoteResourceException if there is any problem with the remote department repository
     */
    void deleteById(long id);
}
