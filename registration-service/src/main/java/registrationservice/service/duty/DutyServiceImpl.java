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
import registrationservice.service.external.employee.EmployeeServiceFeignClient;
import registrationservice.service.external.employee.Doctor;
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
    private final EmployeeServiceFeignClient employeeService;
    private final Validator validator;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public DutyServiceImpl(DutyRepository dutyRepository,
                           RegistrationRepository registrationRepository,
                           EmployeeServiceFeignClient employeeService,
                           Validator validator,
                           CircuitBreaker circuitBreaker) {
        this.dutyRepository = dutyRepository;
        this.registrationRepository = registrationRepository;
        this.employeeService = employeeService;
        this.validator = validator;
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
                    employeeService.findAllDoctorsBySpecialty(duty.getNeededSpecialty());
            Collection<Doctor> doctors = circuitBreaker.decorateSupplier(find)
                    .get()
                    .getContent()
                    .stream()
                    .map(EntityModel::getContent)
                    .collect(Collectors.toSet());
            duty.setDoctors(doctors);
        } catch (Exception e) {
            logger.error("Employee microservice unavailable: " + e.getMessage());
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
            Duty dutyToSave = prepareSaveData(duty);
            Duty saved = persistDuty(dutyToSave);
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

    private Duty prepareSaveData(Duty duty) {
        Duty dutyToSave = new Duty(duty);
        dutyToSave.setId(null);

        return dutyToSave;
    }

    private Duty persistDuty(Duty duty) {
        Supplier<Duty> save = () -> {
            Duty saved = dutyRepository.save(duty);
            dutyRepository.flush();
            return saved;
        };

        return circuitBreaker.decorateSupplier(save).get();
    }

    @Override
    public Duty update(Duty duty) {
        try {
            long id = duty.getId();
            Duty dutyToUpdate = findById(id)
                    .orElseThrow(() -> new IllegalModificationException("No duty with id " + id));
            dutyToUpdate = prepareUpdateData(dutyToUpdate, duty);
            validate(dutyToUpdate);

            Duty updated = persistDuty(dutyToUpdate);
            loadDoctors(updated);
            logger.info("Duty " + updated.getId() + " updated");
            return updated;
        } catch (IllegalModificationException | RemoteResourceException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteResourceException("Duty database unavailable", e);
        }
    }

    private Duty prepareUpdateData(Duty savedDuty, Duty data) {
        return Duty.builder(savedDuty)
                .copyNonNullFields(data)
                .build();
    }

    @Override
    public void deleteById(long id) {
        try {
            deleteDutyFromRegistrations(id);
            deleteDuty(id);
            logger.info("Duty " + id + " deleted");
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalModificationException("No duty with id " + id, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Duty database unavailable", e);
        }
    }

    private void deleteDutyFromRegistrations(long dutyId) {
        Supplier<List<Registration>> findAllByDuty = () -> registrationRepository.findAllByDutyId(dutyId);
        List<Registration> registrations = circuitBreaker.decorateSupplier(findAllByDuty).get();
        registrations.forEach(registration -> registration.setDuty(null));
        persistRegistrations(registrations);
    }

    private void persistRegistrations(List<Registration> registrations) {
        Runnable save = () -> {
            registrationRepository.saveAll(registrations);
            registrationRepository.flush();
        };

        circuitBreaker.decorateRunnable(save).run();
    }

    private void deleteDuty(long id) {
        Runnable deleteDuty = () -> {
            dutyRepository.deleteById(id);
            dutyRepository.flush();
        };

        circuitBreaker.decorateRunnable(deleteDuty).run();
    }
}
