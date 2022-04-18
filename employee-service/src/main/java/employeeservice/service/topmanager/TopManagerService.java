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

package employeeservice.service.topmanager;

import employeeservice.service.exception.IllegalModificationException;
import employeeservice.service.exception.RemoteResourceException;

import java.util.List;
import java.util.Optional;

public interface TopManagerService {

    /**
     * Looks for all managers in the remote employee repository.
     *
     * @return all managers from the remote employee repository
     *
     * @throws RemoteResourceException if there is any problem with the remote employee repository
     */
    List<TopManager> findAll();

    /**
     * Looks for a manager with the specified ID in the remote employee repository.
     *
     * @param id ID of the manager to get
     *
     * @return the manager with the specified ID in the remote employee repository
     * or Optional#empty() if none found
     *
     * @throws RemoteResourceException if there is any problem with the remote employee repository
     */
    Optional<TopManager> findById(long id);

    /**
     * Looks for a manager with the specified email in the remote employee repository.
     *
     * @param email email of the manager to get
     *
     * @return the manager with the specified email in the remote employee repository
     * or Optional#empty() if none found
     *
     * @throws RemoteResourceException if there is any problem with the remote employee repository
     */
    Optional<TopManager> findByEmail(String email);

    /**
     * Counts the number of managers in the remote employee repository.
     *
     * @return the number of managers in the remote employee repository
     *
     * @throws RemoteResourceException if there is any problem with the remote employee repository
     */
    long count();

    /**
     * Saves the specified manager in the remote employee repository.
     * Use the returned manager for further operations as the save operation
     * might have changed the manager instance completely.
     *
     * @param manager manager to save
     *
     * @return the saved manager
     *
     * @throws IllegalModificationException either if a manager has invalid data or already exists
     * @throws RemoteResourceException if there is any problem with the remote employee repository
     */
    TopManager save(TopManager manager);

    /**
     * Updates the manager with the specified ID in the remote employee repository.
     * Use the returned manager for further operations as the update operation
     * might have changed the manager instance completely.
     *
     * @param manager manager to update
     *
     * @return the updated manager
     *
     * @throws IllegalModificationException either if a manager has invalid data or does not exist
     * @throws RemoteResourceException if there is any problem with the remote employee repository
     */
    TopManager update(TopManager manager);

    /**
     * Deletes the manager with the specified ID in the remote employee repository.
     *
     * @param id the ID of the manager to be deleted
     *
     * @throws IllegalModificationException if such a manager does not exist
     * @throws RemoteResourceException if there is any problem with the remote employee repository
     */
    void deleteById(long id);
}
