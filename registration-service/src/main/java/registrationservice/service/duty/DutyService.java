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

package registrationservice.service.duty;

import registrationservice.service.exception.IllegalModificationException;
import registrationservice.service.exception.RemoteResourceException;

import java.util.List;
import java.util.Optional;

/**
 * Provides duty business logic.
 */
public interface DutyService {

    /**
     * Looks for all duties in the remote duty repository.
     *
     * @return all duties from the remote duty repository
     *
     * @throws RemoteResourceException if there is any problem with the remote duty repository
     */
    List<Duty> findAll();

    /**
     * Looks for a duty with the specified ID in the remote duty repository.
     *
     * @param id ID of the duty to get
     *
     * @return the duty with the specified ID in the remote duty repository
     * or Optional#empty() if none found
     *
     * @throws RemoteResourceException if there is any problem with the remote duty repository
     */
    Optional<Duty> findById(long id);

    /**
     * Looks for a duty with the specified name in the remote duty repository.
     *
     * @param name name of the duty to get
     *
     * @return the duty with the specified name in the remote duty repository
     * or Optional#empty() if none found
     *
     * @throws RemoteResourceException if there is any problem with the remote duty repository
     */
    Optional<Duty> findByName(String name);

    /**
     * Saves the specified duty in the remote duty repository.
     * Use the returned duty for further operations as the save operation
     * might have changed the duty instance completely.
     *
     * @param duty duty to save
     *
     * @return the saved duty
     *
     * @throws IllegalModificationException either if a duty has invalid data or already exists
     * @throws RemoteResourceException if there is any problem with the remote duty repository
     */
    Duty save(Duty duty);

    /**
     * Updates the duty with the specified ID in the remote duty repository.
     * Use the returned duty for further operations as the update operation
     * might have changed the duty instance completely.
     *
     * @param duty duty to update
     *
     * @return the updated duty
     *
     * @throws IllegalModificationException either if a duty has invalid data or does not exist
     * @throws RemoteResourceException if there is any problem with the remote duty repository
     */
    Duty update(Duty duty);

    /**
     * Deletes the duty with the specified ID in the remote duty repository.
     *
     * @param id the ID of the duty to be deleted
     *
     * @throws IllegalModificationException if such a duty does not exist
     * @throws RemoteResourceException if there is any problem with the remote duty repository
     */
    void deleteById(long id);
}
