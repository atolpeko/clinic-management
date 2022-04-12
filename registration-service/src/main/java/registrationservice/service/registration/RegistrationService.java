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

package registrationservice.service.registration;

import registrationservice.service.exception.IllegalModificationException;
import registrationservice.service.exception.RemoteResourceException;

import java.util.List;
import java.util.Optional;

/**
 * Provides registration business logic.
 */
public interface RegistrationService {

    /**
     * Looks for all registrations in the remote registration repository.
     *
     * @return all registrations from the remote registration repository
     *
     * @throws RemoteResourceException if there is any problem with the remote registration repository
     */
    List<Registration> findAll();

    /**
     * Looks for all registrations with the specified client ID
     * in the remote registration repository.
     *
     * @param clientId client ID of registration to get
     *
     * @return all registrations with the specified client ID from the remote registration repository
     *
     * @throws RemoteResourceException if there is any problem with the remote registration repository
     */
    List<Registration> findAllByClientId(long clientId);

    /**
     * Looks for all registrations with the specified doctor ID
     * in the remote registration repository.
     *
     * @param doctorId doctor ID of registration to get
     *
     * @return all registrations with the specified doctor ID from the remote registration repository
     *
     * @throws RemoteResourceException if there is any problem with the remote registration repository
     */
    List<Registration> findAllByDoctorId(long doctorId);

    /**
     * Looks for a registration with the specified ID in the remote registration repository.
     *
     * @param id ID of the registration to get
     *
     * @return the registration with the specified ID in the remote registration repository
     * or Optional#empty() if none found
     *
     * @throws RemoteResourceException if there is any problem with the remote registration repository
     */
    Optional<Registration> findById(long id);

    /**
     * Counts the number of the registrations in the remote registration repossitory.
     *
     * @return the number of registrations in the remote registration repossitory
     *
     * @throws RemoteResourceException if there is any problem with the remote registration repository
     */
    long count();

    /**
     * Saves the specified registration in the remote registration repository.
     * Use the returned registration for further operations as the save operation
     * might have changed the registration instance completely.
     *
     * @param registration registration to save
     *
     * @return the saved registration
     *
     * @throws IllegalModificationException either if a registration has invalid data or already exists
     * @throws RemoteResourceException if there is any problem with the remote registration repository
     */
    Registration save(Registration registration);

    /**
     * Sets a status for the registration with the specified ID in the remote registration repository.
     * <br>
     * Use the returned registration for further operations as the update operation
     * might have changed the registration instance completely.
     *
     * @param id the ID of the registration whose status need to be changed
     * @param isActive makes a registration active if true, inactive otherwise
     *
     * @return the updated registration
     *
     * @throws IllegalModificationException if such a registration does not exist
     * @throws RemoteResourceException if there is any problem with the remote registration repository
     */
    Registration setActive(long id, boolean isActive);

    /**
     * Deletes the registration with the specified ID in the remote registration repository.
     *
     * @param id the ID of the registration to be deleted
     *
     * @throws IllegalModificationException if such a registration does not exist
     * @throws RemoteResourceException if there is any problem with the remote registration repository
     */
    void deleteById(long id);
}
