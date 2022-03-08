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

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import registrationservice.data.RegistrationRepository;
import registrationservice.service.exception.IllegalModificationException;
import registrationservice.service.exception.RemoteResourceException;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService {
    private static final Logger logger = LogManager.getLogger(RegistrationServiceImpl.class);

    private final RegistrationRepository repository;
    private final Validator validator;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public RegistrationServiceImpl(RegistrationRepository repository,
                                   Validator validator,
                                   CircuitBreaker circuitBreaker) {
        this.repository = repository;
        this.validator = validator;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public List<Registration> findAll() {
        try {
            Supplier<List<Registration>> findAll = repository::findAll;
            return circuitBreaker.decorateSupplier(findAll).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Registration database unavailable", e);
        }
    }

    @Override
    public List<Registration> findAllByClientId(long clientId) {
        try {
            Supplier<List<Registration>> findAll = () -> repository.findAllByClientId(clientId);
            return circuitBreaker.decorateSupplier(findAll).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Registration database unavailable", e);
        }
    }

    @Override
    public List<Registration> findAllByDoctorId(long doctorId) {
        try {
            Supplier<List<Registration>> findAll = () -> repository.findAllByDoctorId(doctorId);
            return circuitBreaker.decorateSupplier(findAll).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Registration database unavailable", e);
        }
    }

    @Override
    public Optional<Registration> findById(long id) {
        try {
            Supplier<Optional<Registration>> findById = () -> repository.findById(id);
            return circuitBreaker.decorateSupplier(findById).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Registration database unavailable", e);
        }
    }

    @Override
    public Registration save(Registration registration) {
        try {
            validate(registration);
            Registration registrationToSave = new Registration(registration);
            registrationToSave.setId(null);

            Supplier<Registration> save = () -> {
                Registration saved = repository.save(registrationToSave);
                repository.flush();
                return saved;
            };

            Registration saved = circuitBreaker.decorateSupplier(save).get();
            logger.info("Registration " + saved.getDate() + " saved. ID - " + saved.getId());
            return saved;
        } catch (IllegalModificationException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteResourceException("Registration database unavailable", e);
        }
    }

    private void validate(Registration registration) {
        Set<ConstraintViolation<Registration>> violations = validator.validate(registration);
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<Registration> violation : violations) {
                builder.append(violation.getMessage()).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length() - 1);
            String msg = builder.toString().toLowerCase(Locale.ROOT);
            throw new IllegalModificationException(msg);
        }

        if (registration.getDoctor().getId() == null) {
           throw new IllegalModificationException("Doctor ID is mandatory");
        }
        if (registration.getClient().getId() == null) {
            throw new IllegalModificationException("Client ID is mandatory");
        }
        if (registration.getDuty() == null) {
            throw new IllegalModificationException("Duty is mandatory");
        }
        if (registration.getDuty().getId() == null) {
            throw new IllegalModificationException("Duty ID is mandatory");
        }
    }

    @Override
    public Registration setActive(long id, boolean isActive) {
        try {
            Supplier<Optional<Registration>> findById = () -> repository.findById(id);
            Registration registrationToUpdate = circuitBreaker.decorateSupplier(findById)
                    .get()
                    .orElseThrow(() -> new IllegalModificationException("No registration with id " + id));
            registrationToUpdate.setActive(isActive);

            Supplier<Registration> update = () -> {
                Registration updated = repository.save(registrationToUpdate);
                repository.flush();
                return updated;
            };

            Registration updated = circuitBreaker.decorateSupplier(update).get();
            logger.info("Registration status " + id + " changed");
            return updated;
        } catch (Exception e) {
            throw new RemoteResourceException("Registration database unavailable", e);
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
            logger.info("Registration " + id + " deleted");
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalModificationException("No registration with id " + id);
        } catch (Exception e) {
            throw new RemoteResourceException("Registration database unavailable", e);
        }
    }
}
