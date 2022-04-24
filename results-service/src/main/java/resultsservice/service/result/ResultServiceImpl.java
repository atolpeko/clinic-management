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

import feign.FeignException;

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
                             ClientServiceFeignClient clientService,
                             EmployeeServiceFeignClient employeeService,
                             RegistrationServiceFeignClient registrationService,
                             Validator validator,
                             CircuitBreaker circuitBreaker) {
        this.repository = repository;
        this.clientService = clientService;
        this.employeeService = employeeService;
        this.registrationService = registrationService;
        this.validator = validator;
        this.circuitBreaker = circuitBreaker;
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
        result.setDuty(loadDuty(result.getDuty().getId()));
        result.setDoctor(loadDoctor(result.getDoctor().getId()));
        result.setClient(loadClient(result.getClient().getId()));
    }

    private Duty loadDuty(long dutyId) {
        try {
            Supplier<Duty> findDuty = () -> registrationService.findDutyById(dutyId);
            return circuitBreaker.decorateSupplier(findDuty).get();
        } catch (FeignException e) {
            if (e.status() == 404) {
                logger.error("Duty not found: " + dutyId);
            } else {
                logger.error("Registration microservice unavailable: " + e.getMessage());
            }

            return null;
        }
    }
    
    private Doctor loadDoctor(long doctorId) {
        try {
            Supplier<Doctor> findDoctor = () -> employeeService.findDoctorById(doctorId);
            return circuitBreaker.decorateSupplier(findDoctor).get();
        } catch (FeignException e) {
            if (e.status() == 404) {
                logger.error("Doctor not found: " + doctorId);
            } else {
                logger.error("Employee microservice unavailable: " + e.getMessage());
            }

            return null;
        }
    }

    private Client loadClient(long clientId) {
        try {
            Supplier<Client> findClient = () -> clientService.findClientById(clientId);
            return circuitBreaker.decorateSupplier(findClient).get();
        } catch (FeignException e) {
            if (e.status() == 404) {
                logger.error("Client not found: " + clientId);
            } else {
                logger.error("Client microservice unavailable: " + e.getMessage());
            }

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
            Supplier<Long> count = repository::count;
            return circuitBreaker.decorateSupplier(count).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Result database unavailable", e);
        }
    }

    @Override
    public Result save(Result result) {
        try {
            validate(result);
            Result resultToSave = prepareSaveData(result);

            Result saved = persistResult(resultToSave);
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
        validateResult(result);
        validateDuty(result.getDuty());
        validateDoctor(result.getDoctor());
        validateClient(result.getClient());
    }

    private void validateResult(Result result) {
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

    private void validateDuty(Duty duty) {
        try {
            if (duty.getId() == null) {
                throw new IllegalModificationException("Duty ID is mandatory");
            }

            Supplier<Duty> findById = () -> registrationService.findDutyById(duty.getId());
            circuitBreaker.decorateSupplier(findById).get();
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new IllegalModificationException("No duty with id " + duty.getId());
            } else {
                logger.error(e.getMessage());
                throw new RemoteResourceException("Registration service unavailable", e);
            }
        }
    }

    private void validateDoctor(Doctor doctor) {
        try {
            if (doctor.getId() == null) {
                throw new IllegalModificationException("Doctor ID is mandatory");
            }

            Supplier<Doctor> findById = () -> employeeService.findDoctorById(doctor.getId());
            circuitBreaker.decorateSupplier(findById).get();
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new IllegalModificationException("No doctor with id " + doctor.getId());
            } else {
                logger.error(e.getMessage());
                throw new RemoteResourceException("Employee service unavailable", e);
            }
        }
    }

    private void validateClient(Client client) {
        try {
            if (client.getId() == null) {
                throw new IllegalModificationException("Client ID is mandatory");
            }

            Supplier<Client> findById = () -> clientService.findClientById(client.getId());
            circuitBreaker.decorateSupplier(findById).get();
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new IllegalModificationException("No client with id " + client.getId());
            } else {
                logger.error(e.getMessage());
                throw new RemoteResourceException("Client service unavailable", e);
            }
        }
    }

    private Result prepareSaveData(Result result) {
        Result resultToSave = new Result(result);
        resultToSave.setId(null);

        return resultToSave;
    }

    private Result persistResult(Result result) {
        Supplier<Result> save = () -> {
            Result saved = repository.save(result);
            repository.flush();
            return saved;
        };

        return circuitBreaker.decorateSupplier(save).get();
    }

    @Override
    public Result update(Result result) {
        try {
            long id = result.getId();
            Result resultToUpdate = findById(id)
                    .orElseThrow(() -> new IllegalModificationException("No result with id " + id));
            resultToUpdate = prepareUpdateData(resultToUpdate, result);
            validate(resultToUpdate);

            Result updated = persistResult(resultToUpdate);
            loadContent(updated);
            logger.info("Result " + updated.getId() + " updated");
            return updated;
        } catch (IllegalModificationException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteResourceException("Result database unavailable", e);
        }
    }

    private Result prepareUpdateData(Result savedResult, Result data) {
        return Result.builder(savedResult)
                .copyNonNullFields(data)
                .build();
    }

    @Override
    public void deleteById(long id) {
        try {
            deleteResult(id);
            logger.info("Duty " + id + " deleted");
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalModificationException("No result with id " + id, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Result database unavailable", e);
        }
    }

    private void deleteResult(long id) {
        Runnable delete = () -> {
            repository.deleteById(id);
            repository.flush();
        };

        circuitBreaker.decorateRunnable(delete).run();
    }
}
