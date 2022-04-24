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

package clientservice.service;

import clientservice.service.exception.ClientsModificationException;
import clientservice.service.exception.RemoteResourceException;

import java.util.List;
import java.util.Optional;

/**
 * Provides client business logic.
 */
public interface ClientService {

    /**
     * Looks for all clients in the remote client repository.
     *
     * @return all clients from the remote client repository.
     *
     * @throws RemoteResourceException if there is any problem with the remote client repository
     */
    List<Client> findAll();

    /**
     * Looks for a client with the specified ID in the remote client repository.
     *
     * @param id ID of the client to get
     *
     * @return the client with the specified ID in the remote client repository
     * or Optional#empty() if none found
     *
     * @throws RemoteResourceException if there is any problem with the remote client repository
     */
    Optional<Client> findById(long id);

    /**
     * Looks for a client with the specified email in the remote client repository
     *
     * @param email email of the client to get
     *
     * @return the client with the specified email in the remote client repository
     * or Optional#empty() if none found
     *
     * @throws RemoteResourceException if there is any problem with the remote client repository
     */
    Optional<Client> findByEmail(String email);

    /**
     * Counts the number of clients in the remote client repository.
     *
     * @return the number of clients in the remote client repository
     *
     * @throws RemoteResourceException if there is any problem with the remote client repository
     */
    long count();
    
    /**
     * Saves the specified client in the remote client repository.
     * Use the returned client for further operations as the save operation
     * might have changed the client instance completely.
     *
     * @param client client to register
     *
     * @return the saved client
     *
     * @throws ClientsModificationException either if a client has invalid data or already exists
     * @throws RemoteResourceException if there is any problem with the remote client repository
     */
    Client save(Client client);

    /**
     * Updates the client with the specified ID in the remote client repository.
     * Use the returned client for further operations as the update operation
     * might have changed the client instance completely.
     *
     * @param client client to update
     *
     * @return the updated client
     *
     * @throws ClientsModificationException either if a client has invalid data or does not exist
     * @throws RemoteResourceException if there is any problem with the remote client repository
     */
    Client update(Client client);

    /**
     * Sets an account status for the client with the specified ID in the remote client repository.
     * <br>
     * Use the returned client for further operations as the update operation
     * might have changed the client instance completely.
     *
     * @param id the ID of the client whose account status need to be changed
     * @param isEnabled blocks the account if true, unblocks otherwise
     *
     * @return the updated client
     *
     * @throws ClientsModificationException if such a client does not exist
     * @throws RemoteResourceException if there is any problem with the remote client repository
     */
    Client setEnabled(long id, boolean isEnabled);

    /**
     * Deletes the client with the specified ID in the remote client repository.
     *
     * @param id the ID of the client to be deleted
     *
     * @throws ClientsModificationException if such a client does not exist
     * @throws RemoteResourceException if there is any problem with the remote client repository
     */
    void deleteById(long id);
}
