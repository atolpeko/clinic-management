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
import registrationservice.service.external.client.Client;
import registrationservice.service.external.client.ClientServiceFeignClient;
import registrationservice.service.external.clinic.ClinicServiceFeignClient;
import registrationservice.service.external.clinic.Doctor;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
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

    private final ClinicServiceFeignClient clinicService;
    private final ClientServiceFeignClient clientService;

    @Autowired
    public RegistrationServiceImpl(RegistrationRepository repository,
                                   Validator validator,
                                   CircuitBreaker circuitBreaker,
                                   ClinicServiceFeignClient clinicService,
                                   ClientServiceFeignClient clientService) {
        this.repository = repository;
        this.validator = validator;
        this.circuitBreaker = circuitBreaker;
        this.clinicService = clinicService;
        this.clientService = clientService;
    }

    @Override
    public List<Registration> findAll() {
        try {
            Supplier<List<Registration>> findAll = repository::findAll;
            List<Registration> registrations = circuitBreaker.decorateSupplier(findAll).get();
            registrations.forEach(this::loadContent);
            return registrations;
        } catch (Exception e) {
            throw new RemoteResourceException("Registration database unavailable", e);
        }
    }

    private void loadContent(Registration registration) {
        registration.setDoctor(loadDoctor(registration.getDoctorId()));
        registration.setClient(loadClient(registration.getClientId()));
    }

    private Doctor loadDoctor(long doctorId) {
        try {
            Supplier<Optional<Doctor>> findDoctor = () -> clinicService.findDoctorById(doctorId);
            return circuitBreaker.decorateSupplier(findDoctor).get().orElseThrow();
        } catch (NoSuchElementException e) {
            logger.error("Doctor not found: " + doctorId);
            return null;
        } catch (Exception e) {
            String errorMsg = "Clinic microservice unavailable: " + e.getMessage();
            logger.error(errorMsg);
            return null;
        }
    }

    private Client loadClient(long clientId) {
        try {
            Supplier<Optional<Client>> findClient = () -> clientService.findClientById(clientId);
            return circuitBreaker.decorateSupplier(findClient).get().orElseThrow();
        } catch (NoSuchElementException e) {
            logger.error("Client not found: " + clientId);
            return null;
        } catch (Exception e) {
            String errorMsg = "Client microservice unavailable: " + e.getMessage();
            logger.error(errorMsg);
            return null;
        }
    }

    @Override
    public List<Registration> findAllByClientId(long clientId) {
        try {
            Supplier<List<Registration>> findAll = () -> repository.findAllByClientId(clientId);
            List<Registration> registrations = circuitBreaker.decorateSupplier(findAll).get();
            registrations.forEach(this::loadContent);
            return registrations;
        } catch (Exception e) {
            throw new RemoteResourceException("Registration database unavailable", e);
        }
    }

    @Override
    public List<Registration> findAllByDoctorId(long doctorId) {
        try {
            Supplier<List<Registration>> findAll = () -> repository.findAllByDoctorId(doctorId);
            List<Registration> registrations = circuitBreaker.decorateSupplier(findAll).get();
            registrations.forEach(this::loadContent);
            return registrations;
        } catch (Exception e) {
            throw new RemoteResourceException("Registration database unavailable", e);
        }
    }

    @Override
    public Optional<Registration> findById(long id) {
        try {
            Supplier<Optional<Registration>> findById = () -> repository.findById(id);
            Optional<Registration> registration = circuitBreaker.decorateSupplier(findById).get();
            registration.ifPresent(this::loadContent);
            return registration;
        } catch (Exception e) {
            throw new RemoteResourceException("Registration database unavailable", e);
        }
    }

    @Override
    public long count() {
        try {
            Supplier<Long> count = repository::count;
            return circuitBreaker.decorateSupplier(count).get();
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
            loadContent(saved);
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
            loadContent(updated);
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
            throw new IllegalModificationException("No registration with id " + id, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Registration database unavailable", e);
        }
    }
}
