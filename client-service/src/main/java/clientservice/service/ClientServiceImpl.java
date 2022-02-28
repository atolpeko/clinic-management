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

import clientservice.data.ClientRepository;
import clientservice.service.exception.ClientsModificationException;
import clientservice.service.exception.RemoteResourceException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {
    private static final Logger logger = LogManager.getLogger(ClientServiceImpl.class);

    private final ClientRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public ClientServiceImpl(ClientRepository repository,
                             PasswordEncoder passwordEncoder,
                             CircuitBreaker circuitBreaker) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public List<Client> findAll() {
        try {
            Supplier<List<Client>> findAll = repository::findAll;
            return circuitBreaker.decorateSupplier(findAll).get();
        } catch (CallNotPermittedException e) {
            throw new RemoteResourceException("Client database unavailable", e);
        } catch (Exception e) {
            throw new RemoteResourceException(e);
        }
    }

    @Override
    public Optional<Client> findById(long id) {
        try {
            Supplier<Optional<Client>> findById = () -> repository.findById(id);
            return circuitBreaker.decorateSupplier(findById).get();
        } catch (CallNotPermittedException e) {
            throw new RemoteResourceException("Client database unavailable", e);
        } catch (Exception e) {
            throw new RemoteResourceException(e);
        }
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        try {
            Supplier<Optional<Client>> findByEmail = () -> repository.findByEmail(email);
            return circuitBreaker.decorateSupplier(findByEmail).get();
        } catch (CallNotPermittedException e) {
            throw new RemoteResourceException("Client database unavailable", e);
        } catch (Exception e) {
            throw new RemoteResourceException(e);
        }
    }

    @Override
    public Client register(Client client) {
        try {
            Client clientToSave = new Client(client);
            clientToSave.setPassword(passwordEncoder.encode(client.getPassword()));

            Supplier<Client> save = () -> repository.save(clientToSave);
            Client saved = circuitBreaker.decorateSupplier(save).get();
            logger.info("Client " + saved.getEmail() + " registered. ID - " + saved.getId());
            return saved;
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            throw new ClientsModificationException(e);
        } catch (CallNotPermittedException e) {
            throw new RemoteResourceException("Client database unavailable", e);
        } catch (Exception e) {
            throw new RemoteResourceException(e);
        }
    }

    @Override
    public Client update(Client client) {
        try {
            Supplier<Optional<Client>> findById = () -> repository.findById(client.getId());
            Client clientToUpdate = circuitBreaker.decorateSupplier(findById)
                    .get()
                    .orElseThrow(() -> new ClientsModificationException("No client with id " + client.getId()));
            prepareUpdateData(clientToUpdate, client);

            Supplier<Client> save = () -> repository.save(clientToUpdate);
            Client updated = circuitBreaker.decorateSupplier(save).get();
            logger.info("Client " + updated.getId() + " updated");
            return updated;
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            throw new ClientsModificationException(e);
        } catch (CallNotPermittedException e) {
            throw new RemoteResourceException("Client database unavailable", e);
        } catch (Exception e) {
            throw new RemoteResourceException(e);
        }
    }

    private void prepareUpdateData(Client client, Client updateData) {
        if (updateData.getEmail() != null) {
            client.setEmail(updateData.getEmail());
        }
        if (updateData.getPassword() != null) {
            client.setPassword(passwordEncoder.encode(updateData.getPassword()));
        }
        if (updateData.getName() != null) {
            client.setName(updateData.getName());
        }
        if (updateData.getSex() != null) {
            client.setSex(updateData.getSex());
        }
        if (updateData.getPhoneNumber() != null) {
            client.setPhoneNumber(updateData.getPhoneNumber());
        }
        if (updateData.getCountry() != null) {
            client.setCountry(updateData.getCountry());
        }
        if (updateData.getCity() != null) {
            client.setCity(updateData.getCity());
        }
        if (updateData.getStreet() != null) {
            client.setStreet(updateData.getStreet());
        }
        if (updateData.getHouseNumber() != 0) {
            client.setHouseNumber(updateData.getHouseNumber());
        }
    }

    @Override
    public Client setEnabled(long id, boolean isEnabled) {
        try {
            Supplier<Optional<Client>> findById = () -> repository.findById(id);
            Client clientToUpdate = circuitBreaker.decorateSupplier(findById)
                    .get()
                    .orElseThrow(() -> new ClientsModificationException("No client with id " + id));
            clientToUpdate.setEnabled(isEnabled);

            Supplier<Client> save = () -> repository.save(clientToUpdate);
            Client updated = circuitBreaker.decorateSupplier(save).get();
            logger.info("Client " + updated.getId() + " updated");
            return updated;
        } catch (CallNotPermittedException e) {
            throw new RemoteResourceException("Client database unavailable", e);
        } catch (Exception e) {
            throw new RemoteResourceException(e);
        }
    }

    @Override
    public void deleteById(long id) {
        try {
            Runnable delete = () -> repository.deleteById(id);
            circuitBreaker.decorateRunnable(delete).run();
            logger.info("Client " + id + " deleted");
        } catch (CallNotPermittedException e) {
            throw new RemoteResourceException("Client database unavailable", e);
        } catch (Exception e) {
            throw new RemoteResourceException(e);
        }
    }
}
