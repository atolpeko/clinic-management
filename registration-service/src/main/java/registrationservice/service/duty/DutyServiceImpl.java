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

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import registrationservice.data.DutyRepository;
import registrationservice.data.RegistrationRepository;
import registrationservice.service.exception.IllegalModificationException;
import registrationservice.service.exception.RemoteResourceException;
import registrationservice.service.external.clinic.ClinicServiceFeignClient;
import registrationservice.service.external.clinic.Doctor;
import registrationservice.service.registration.Registration;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Transactional
public class DutyServiceImpl implements DutyService {
    private static final Logger logger = LogManager.getLogger(DutyServiceImpl.class);

    private final DutyRepository dutyRepository;
    private final RegistrationRepository registrationRepository;
    private final Validator validator;
    private final ClinicServiceFeignClient feignClient;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public DutyServiceImpl(DutyRepository dutyRepository,
                           RegistrationRepository registrationRepository,
                           Validator validator,
                           ClinicServiceFeignClient clinicService,
                           CircuitBreaker circuitBreaker) {
        this.dutyRepository = dutyRepository;
        this.registrationRepository = registrationRepository;
        this.validator = validator;
        this.feignClient = clinicService;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public List<Duty> findAll() {
        try {
            Supplier<List<Duty>> findAll = dutyRepository::findAll;
            List<Duty> duties = circuitBreaker.decorateSupplier(findAll).get();
            duties.forEach(this::loadDoctors);
            return duties;
        } catch (RemoteResourceException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteResourceException("Duty database unavailable", e);
        }
    }

    private void loadDoctors(Duty duty) {
        try {
            Supplier<CollectionModel<EntityModel<Doctor>>> find = () ->
                    feignClient.findAllDoctorsBySpecialty(duty.getNeededSpecialty());
            Collection<Doctor> doctors = circuitBreaker.decorateSupplier(find)
                    .get()
                    .getContent()
                    .stream()
                    .map(EntityModel::getContent)
                    .collect(Collectors.toSet());
            duty.setDoctors(doctors);
        } catch (Exception e) {
            logger.error("Clinic microservice unavailable: " + e.getMessage());
        }
    }

    @Override
    public Optional<Duty> findById(long id) {
        try {
            Supplier<Optional<Duty>> findById = () -> dutyRepository.findById(id);
            Optional<Duty> duty = circuitBreaker.decorateSupplier(findById).get();
            duty.ifPresent(this::loadDoctors);
            return duty;
        } catch (RemoteResourceException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteResourceException("Duty database unavailable", e);
        }
    }

    @Override
    public Optional<Duty> findByName(String name) {
        try {
            Supplier<Optional<Duty>> findByName = () -> dutyRepository.findByName(name);
            Optional<Duty> duty = circuitBreaker.decorateSupplier(findByName).get();
            duty.ifPresent(this::loadDoctors);
            return duty;
        } catch (RemoteResourceException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteResourceException("Duty database unavailable", e);
        }
    }

    @Override
    public long count() {
        try {
            Supplier<Long> count = dutyRepository::count;
            return circuitBreaker.decorateSupplier(count).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Duty database unavailable", e);
        }
    }

    @Override
    public Duty save(Duty duty) {
        try {
            validate(duty);
            Duty dutyToSave = new Duty(duty);
            dutyToSave.setId(null);

            Supplier<Duty> save = () -> {
                Duty saved = dutyRepository.save(dutyToSave);
                dutyRepository.flush();
                return saved;
            };

            Duty saved = circuitBreaker.decorateSupplier(save).get();
            loadDoctors(saved);
            logger.info("Duty " + saved.getName() + " saved. ID - " + saved.getId());
            return saved;
        } catch (IllegalModificationException | RemoteResourceException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteResourceException("Duty database unavailable", e);
        }
    }

    private void validate(Duty duty) {
        Set<ConstraintViolation<Duty>> violations = validator.validate(duty);
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<Duty> violation : violations) {
                builder.append(violation.getMessage()).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length() - 1);
            String msg = builder.toString().toLowerCase(Locale.ROOT);
            throw new IllegalModificationException(msg);
        }
    }

    @Override
    public Duty update(Duty duty) {
        try {
            Supplier<Optional<Duty>> findById = () -> dutyRepository.findById(duty.getId());
            Duty dutyToUpdate = circuitBreaker.decorateSupplier(findById)
                    .get()
                    .orElseThrow(() -> new IllegalModificationException("No duty with id " + duty.getId()));
            prepareUpdateData(dutyToUpdate, duty);
            validate(dutyToUpdate);

            Supplier<Duty> update = () -> {
                Duty updated = dutyRepository.save(dutyToUpdate);
                dutyRepository.flush();
                return updated;
            };

            Duty updated = circuitBreaker.decorateSupplier(update).get();
            loadDoctors(updated);
            logger.info("Duty " + updated.getId() + " updated");
            return updated;
        } catch (IllegalModificationException | RemoteResourceException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteResourceException("Duty database unavailable", e);
        }
    }

    private void prepareUpdateData(Duty duty, Duty updateData) {
        if (updateData.getName() != null) {
            duty.setName(updateData.getName());
        }
        if (updateData.getDescription() != null) {
            duty.setDescription(updateData.getDescription());
        }
        if (updateData.getPrice() != null) {
            duty.setPrice(updateData.getPrice());
        }
        if (updateData.getNeededSpecialty() != null) {
            duty.setNeededSpecialty(updateData.getNeededSpecialty());
        }
    }

    @Override
    public void deleteById(long id) {
        try {
            Supplier<List<Registration>> findAllByDuty = () -> registrationRepository.findAllByDutyId(id);
            List<Registration> registrations = circuitBreaker.decorateSupplier(findAllByDuty).get();
            registrations.forEach(registration -> registration.setDuty(null));

            Runnable updateRegistrations = () -> {
                registrationRepository.saveAll(registrations);
                registrationRepository.flush();
            };
            Runnable deleteDuty = () -> {
                dutyRepository.deleteById(id);
                dutyRepository.flush();
            };

            circuitBreaker.decorateRunnable(updateRegistrations).run();
            circuitBreaker.decorateRunnable(deleteDuty).run();
            logger.info("Duty " + id + " deleted");
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalModificationException("No duty with id " + id, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Duty database unavailable", e);
        }
    }
}
