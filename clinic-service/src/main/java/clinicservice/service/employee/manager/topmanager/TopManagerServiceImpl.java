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

package clinicservice.service.employee.manager.topmanager;

import clinicservice.data.TopManagerRepository;
import clinicservice.service.Address;
import clinicservice.service.employee.PersonalData;
import clinicservice.service.exception.IllegalModificationException;
import clinicservice.service.exception.RemoteResourceException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@Service
@Transactional
public class TopManagerServiceImpl implements TopManagerService {
    private static final Logger logger = LogManager.getLogger(TopManagerServiceImpl.class);

    private final TopManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public TopManagerServiceImpl(TopManagerRepository managerRepository,
                                 PasswordEncoder passwordEncoder,
                                 Validator validator,
                                 CircuitBreaker circuitBreaker) {
        this.managerRepository = managerRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public List<TopManager> findAll() {
        try {
            Supplier<List<TopManager>> findAll = managerRepository::findAll;
            return circuitBreaker.decorateSupplier(findAll).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    @Override
    public Optional<TopManager> findById(long id) {
        try {
            Supplier<Optional<TopManager>> findById = () -> managerRepository.findById(id);
            return circuitBreaker.decorateSupplier(findById).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    @Override
    public Optional<TopManager> findByEmail(String email) {
        try {
            Supplier<Optional<TopManager>> findById = () -> managerRepository.findByEmail(email);
            return circuitBreaker.decorateSupplier(findById).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    @Override
    public TopManager save(TopManager manager) {
        try {
            validate(manager);
            TopManager managerToSave = new TopManager(manager);
            managerToSave.setId(null);
            managerToSave.setPassword(passwordEncoder.encode(manager.getPassword()));

            Supplier<TopManager> save = () -> {
                TopManager saved = managerRepository.save(managerToSave);
                managerRepository.flush();
                return saved;
            };

            TopManager saved = circuitBreaker.decorateSupplier(save).get();
            logger.info("Manager " + saved.getEmail() + " saved. ID - " + saved.getId());
            return saved;
        } catch (IllegalModificationException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            String msg = "Such an employee already exists: " + manager.getEmail();
            throw new IllegalModificationException(msg, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    private void validate(TopManager manager) {
        Set<ConstraintViolation<TopManager>> violations = validator.validate(manager);
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<TopManager> violation : violations) {
                builder.append(violation.getMessage()).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length() - 1);
            String msg = builder.toString().toLowerCase(Locale.ROOT);
            throw new IllegalModificationException(msg);
        }
    }

    @Override
    public TopManager update(TopManager manager) {
        try {
            Supplier<Optional<TopManager>> findById = () -> managerRepository.findById(manager.getId());
            TopManager managerToUpdate = circuitBreaker.decorateSupplier(findById)
                    .get()
                    .orElseThrow(() -> new IllegalModificationException("No manager with id " + manager.getId()));
            prepareUpdateData(managerToUpdate, manager);
            validate(managerToUpdate);

            Supplier<TopManager> update = () -> {
                TopManager updated = managerRepository.save(managerToUpdate);
                managerRepository.flush();
                return updated;
            };

            TopManager updated = circuitBreaker.decorateSupplier(update).get();
            logger.info("Manager " + updated.getId() + " updated");
            return updated;
        } catch (IllegalModificationException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            String msg = "Such an employee already exists: " + manager.getEmail();
            throw new IllegalModificationException(msg, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    private void prepareUpdateData(TopManager manager, TopManager updateData) {
        if (updateData.getEmail() != null) {
            manager.setEmail(updateData.getEmail());
        }
        if (updateData.getPassword() != null) {
            manager.setPassword(passwordEncoder.encode(updateData.getPassword()));
        }
        if (updateData.getPersonalData() != null) {
            preparePersonalData(updateData.getPersonalData(), manager.getPersonalData());
        }
    }

    private void preparePersonalData(PersonalData source, PersonalData target) {
        if (source.getName() != null) {
            target.setName(source.getName());
        }
        if (source.getPhone() != null) {
            target.setPhone(source.getPhone());
        }
        if (source.getSalary() != null) {
            target.setSalary(source.getSalary());
        }
        if (source.getHireDate() != null) {
            target.setHireDate(source.getHireDate());
        }
        if (source.getDateOfBirth() != null) {
            target.setDateOfBirth(source.getDateOfBirth());
        }
        if (source.getSex() != null) {
            target.setSex(source.getSex());
        }
        if (source.getAddress() != null) {
            prepareAddress(source.getAddress(), target.getAddress());
        }
    }

    private void prepareAddress(Address source, Address target) {
        if (source.getCountry() != null) {
            target.setCountry(source.getCountry());
        }
        if (source.getState() != null) {
            target.setState(source.getState());
        }
        if (source.getCity() != null) {
            target.setCity(source.getCity());
        }
        if (source.getStreet() != null) {
            target.setStreet(source.getStreet());
        }
        if (source.getHouseNumber() != null) {
            target.setHouseNumber(source.getHouseNumber());
        }
    }

    @Override
    public void deleteById(long id) {
        try {
            Runnable delete = () -> {
                managerRepository.deleteById(id);
                managerRepository.flush();
            };

            circuitBreaker.decorateRunnable(delete).run();
            logger.info("Manager " + id + " deleted");
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalModificationException("No manager with id " + id, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }
}
