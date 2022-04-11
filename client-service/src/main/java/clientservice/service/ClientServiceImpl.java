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

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {
    private static final Logger logger = LogManager.getLogger(ClientServiceImpl.class);

    private final ClientRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public ClientServiceImpl(ClientRepository repository,
                             PasswordEncoder passwordEncoder,
                             Validator validator,
                             CircuitBreaker circuitBreaker) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public List<Client> findAll() {
        try {
            Supplier<List<Client>> findAll = repository::findAll;
            return circuitBreaker.decorateSupplier(findAll).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Client database unavailable", e);
        }
    }

    @Override
    public Optional<Client> findById(long id) {
        try {
            Supplier<Optional<Client>> findById = () -> repository.findById(id);
            return circuitBreaker.decorateSupplier(findById).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Client database unavailable", e);
        }
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        try {
            Supplier<Optional<Client>> findByEmail = () -> repository.findByEmail(email);
            return circuitBreaker.decorateSupplier(findByEmail).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Client database unavailable", e);
        }
    }

    @Override
    public long count() {
        try {
            Supplier<Long> count = repository::count;
            return circuitBreaker.decorateSupplier(count).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Client database unavailable", e);
        }
    }

    @Override
    public Client register(Client client) {
        try {
            validate(client);
            Client clientToSave = new Client(client);
            clientToSave.setId(null);
            clientToSave.setPassword(passwordEncoder.encode(client.getPassword()));

            Supplier<Client> save = () -> {
                Client saved = repository.save(clientToSave);
                repository.flush();
                return saved;
            };

            Client saved = circuitBreaker.decorateSupplier(save).get();
            logger.info("Client " + saved.getEmail() + " registered. ID - " + saved.getId());
            return saved;
        } catch (ClientsModificationException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new ClientsModificationException("Such a client already exists", e);
        } catch (Exception e) {
            throw new RemoteResourceException("Client database unavailable", e);
        }
    }

    private void validate(Client client) {
        Set<ConstraintViolation<Client>> violations = validator.validate(client);
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<Client> violation : violations) {
                builder.append(violation.getMessage()).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length() - 1);
            String msg = builder.toString().toLowerCase(Locale.ROOT);
            throw new ClientsModificationException(msg);
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
            validate(clientToUpdate);

            Supplier<Client> update = () -> {
                Client updated = repository.save(clientToUpdate);
                repository.flush();
                return updated;
            };

            Client updated = circuitBreaker.decorateSupplier(update).get();
            logger.info("Client " + updated.getId() + " updated");
            return updated;
        } catch (ClientsModificationException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new ClientsModificationException("Such a client already exists", e);
        } catch (Exception e) {
            throw new RemoteResourceException("Client database unavailable", e);
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
        if (updateData.getAddress() != null) {
            prepareAddress(client.getAddress(), updateData.getAddress());
        }
    }

    private void prepareAddress(Address address, Address source) {
        if (source.getCountry() != null) {
            address.setCountry(source.getCountry());
        }
        if (source.getState() != null) {
            address.setState(source.getState());
        }
        if (source.getCity() != null) {
            address.setCity(source.getCity());
        }
        if (source.getStreet() != null) {
            address.setStreet(source.getStreet());
        }
        if (source.getHouseNumber() != null) {
            address.setHouseNumber(source.getHouseNumber());
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

            Supplier<Client> update = () -> {
                Client updated = repository.save(clientToUpdate);
                repository.flush();
                return updated;
            };

            Client updated = circuitBreaker.decorateSupplier(update).get();
            logger.info("Account status of client " + id + " changed");
            return updated;
        } catch (Exception e) {
            throw new RemoteResourceException("Client database unavailable", e);
        }
    }

    @Override
    public void deleteById(long id) {
        try {
            Runnable delete = () -> {
                repository.deleteById(id);
                repository.flush();
            };

            circuitBreaker.decorateRunnable(delete).run();
            logger.info("Client " + id + " deleted");
        } catch (EmptyResultDataAccessException e) {
            throw new ClientsModificationException("No client with id " + id, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Client database unavailable", e);
        }
    }
}
