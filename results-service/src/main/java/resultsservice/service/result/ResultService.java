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

package resultsservice.service.result;

import resultsservice.service.exception.IllegalModificationException;
import resultsservice.service.exception.RemoteResourceException;

import java.util.List;
import java.util.Optional;

/**
 * Provides result business logic.
 */
public interface ResultService {

    /**
     * Looks for all results in the remote result repository.
     *
     * @return all results from the remote result repository
     *
     * @throws RemoteResourceException if there is any problem with the remote result repository
     */
    List<Result> findAll();

    /**
     * Looks for all results with the specified client ID in the remote result repository.
     *
     * @param clientId client ID of results to get
     *
     * @return all results with the specified client ID from the remote result repository
     *
     * @throws RemoteResourceException if there is any problem with the remote result repository
     */
    List<Result> findAllByClientId(long clientId);

    /**
     * Looks for a result with the specified ID in the remote result repository.
     *
     * @param id ID of the result to get
     *
     * @return the result with the specified ID in the remote result repository
     * or Optional#empty() if none found
     *
     * @throws RemoteResourceException if there is any problem with the remote result repository
     */
    Optional<Result> findById(long id);

    /**
     * Saves the specified result in the remote result repository.
     * Use the returned result for further operations as the save operation
     * might have changed the result instance completely.
     *
     * @param result result to save
     *
     * @return the saved result
     *
     * @throws IllegalModificationException either if a result has invalid data or already exists
     * @throws RemoteResourceException if there is any problem with the remote result repository
     */
    Result save(Result result);

    /**
     * Updates the result with the specified ID in the remote result repository.
     * Use the returned result for further operations as the update operation
     * might have changed the result instance completely.
     *
     * @param result result to update
     *
     * @return the updated result
     *
     * @throws IllegalModificationException either if a result has invalid data or does not exist
     * @throws RemoteResourceException if there is any problem with the remote result repository
     */
    Result update(Result result);

    /**
     * Deletes the result with the specified ID in the remote result repository.
     *
     * @param id the ID of the result to be deleted
     *
     * @throws IllegalModificationException if such a result does not exist
     * @throws RemoteResourceException if there is any problem with the remote result repository
     */
    void deleteById(long id);
}
