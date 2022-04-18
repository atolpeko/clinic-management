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

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import resultsservice.data.ResultsRepository;
import resultsservice.service.exception.IllegalModificationException;
import resultsservice.service.exception.RemoteResourceException;
import resultsservice.service.external.client.Client;
import resultsservice.service.external.client.ClientServiceFeignClient;
import resultsservice.service.external.employee.EmployeeServiceFeignClient;
import resultsservice.service.external.employee.Doctor;
import resultsservice.service.external.registration.Duty;
import resultsservice.service.external.registration.RegistrationServiceFeignClient;

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
public class ResultServiceImpl implements ResultService {
    private static final Logger logger = LogManager.getLogger(ResultServiceImpl.class);

    private final ResultsRepository repository;
    private final Validator validator;
    private final CircuitBreaker circuitBreaker;

    private final ClientServiceFeignClient clientService;
    private final EmployeeServiceFeignClient employeeService;
    private final RegistrationServiceFeignClient registrationService;

    @Autowired
    public ResultServiceImpl(ResultsRepository repository,
                             Validator validator,
                             CircuitBreaker circuitBreaker,
                             ClientServiceFeignClient clientService,
                             EmployeeServiceFeignClient employeeService,
                             RegistrationServiceFeignClient registrationService) {
        this.repository = repository;
        this.validator = validator;
        this.circuitBreaker = circuitBreaker;
        this.clientService = clientService;
        this.employeeService = employeeService;
        this.registrationService = registrationService;
    }

    @Override
    public List<Result> findAll() {
        try {
            Supplier<List<Result>> findAll = repository::findAll;
            List<Result> results = circuitBreaker.decorateSupplier(findAll).get();
            results.forEach(this::loadContent);
            return results;
        } catch (Exception e) {
            throw new RemoteResourceException("Result database unavailable", e);
        }
    }

    private void loadContent(Result result) {
        result.setDuty(loadDuty(result.getDutyId()));
        result.setDoctor(loadDoctor(result.getDoctorId()));
        result.setClient(loadClient(result.getClientId()));
    }

    private Duty loadDuty(long dutyId) {
        try {
            Supplier<Optional<Duty>> findDuty = () -> registrationService.findDutyById(dutyId);
            return circuitBreaker.decorateSupplier(findDuty).get().orElseThrow();
        } catch (NoSuchElementException e) {
            logger.error("Duty not found: " + dutyId);
            return null;
        } catch (Exception e) {
            String errorMsg = "Registration microservice unavailable: " + e.getMessage();
            logger.error(errorMsg);
            return null;
        }
    }
    
    private Doctor loadDoctor(long doctorId) {
        try {
            Supplier<Optional<Doctor>> findDoctor = () -> employeeService.findDoctorById(doctorId);
            return circuitBreaker.decorateSupplier(findDoctor).get().orElseThrow();
        } catch (NoSuchElementException e) {
            logger.error("Doctor not found: " + doctorId);
            return null;
        } catch (Exception e) {
            String errorMsg = "Employee microservice unavailable: " + e.getMessage();
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
    public List<Result> findAllByClientId(long clientId) {
        try {
            Supplier<List<Result>> findAll = () -> repository.findAllByClientId(clientId);
            List<Result> results = circuitBreaker.decorateSupplier(findAll).get();
            results.forEach(this::loadContent);
            return results;
        } catch (Exception e) {
            throw new RemoteResourceException("Result database unavailable", e);
        }
    }

    @Override
    public Optional<Result> findById(long id) {
        try {
            Supplier<Optional<Result>> findById = () -> repository.findById(id);
            Optional<Result> result = circuitBreaker.decorateSupplier(findById).get();
            result.ifPresent(this::loadContent);
            return result;
        } catch (Exception e) {
            throw new RemoteResourceException("Result database unavailable", e);
        }
    }

    @Override
    public long count() {
        try {
            Supplier<Long> count = repository::count;;
            return circuitBreaker.decorateSupplier(count).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Result database unavailable", e);
        }
    }

    @Override
    public Result save(Result result) {
        try {
            validate(result);
            Result resultToSave = new Result(result);
            resultToSave.setId(null);

            Supplier<Result> save = () -> {
                Result saved = repository.save(resultToSave);
                repository.flush();
                return saved;
            };

            Result saved = circuitBreaker.decorateSupplier(save).get();
            loadContent(saved);
            logger.info("Result saved. ID - " + saved.getId());
            return saved;
        } catch (IllegalModificationException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteResourceException("Result database unavailable", e);
        }
    }

    private void validate(Result result) {
        Set<ConstraintViolation<Result>> violations = validator.validate(result);
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<Result> violation : violations) {
                builder.append(violation.getMessage()).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length() - 1);
            String msg = builder.toString().toLowerCase(Locale.ROOT);
            throw new IllegalModificationException(msg);
        }
    }

    @Override
    public Result update(Result result) {
        try {
            Supplier<Optional<Result>> findById = () -> repository.findById(result.getId());
            Result resultToUpdate = circuitBreaker.decorateSupplier(findById)
                    .get()
                    .orElseThrow(() -> new IllegalModificationException("No result with id " + result.getId()));
            prepareUpdateData(resultToUpdate, result);
            validate(resultToUpdate);

            Supplier<Result> update = () -> {
                Result updated = repository.save(resultToUpdate);
                repository.flush();
                return updated;
            };

            Result updated = circuitBreaker.decorateSupplier(update).get();
            loadContent(updated);
            logger.info("Result " + updated.getId() + " updated");
            return updated;
        } catch (IllegalModificationException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteResourceException("Result database unavailable", e);
        }
    }

    private void prepareUpdateData(Result result, Result updateData) {
        if (updateData.getData() != null) {
            result.setData(updateData.getData());
        }
        if (updateData.getDutyId() != null) {
            result.setDutyId(updateData.getDutyId());
        }
        if (updateData.getDoctorId() != null) {
            result.setDoctorId(updateData.getDoctorId());
        }
        if (updateData.getClientId() != null) {
            result.setClientId(updateData.getClientId());
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
            logger.info("Duty " + id + " deleted");
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalModificationException("No result with id " + id, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Result database unavailable", e);
        }
    }
}
