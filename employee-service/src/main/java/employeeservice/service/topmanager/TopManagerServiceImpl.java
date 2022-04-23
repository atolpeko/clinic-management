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

package employeeservice.service.topmanager;

import employeeservice.data.TopManagerRepository;
import employeeservice.service.exception.IllegalModificationException;
import employeeservice.service.exception.RemoteResourceException;

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
    public long count() {
        try {
            Supplier<Long> count = managerRepository::count;
            return circuitBreaker.decorateSupplier(count).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    @Override
    public TopManager save(TopManager manager) {
        try {
            validate(manager);
            TopManager managerToSave = prepareSaveData(manager);
            TopManager saved = persistManager(managerToSave);
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

    private TopManager prepareSaveData(TopManager manager) {
        String password = passwordEncoder.encode(manager.getPassword());
        TopManager managerToSave = new TopManager(manager);
        managerToSave.setId(null);
        managerToSave.setPassword(password);

        return managerToSave;
    }

    private TopManager persistManager(TopManager manager) {
        Supplier<TopManager> save = () -> {
            TopManager saved = managerRepository.save(manager);
            managerRepository.flush();
            return saved;
        };

        return circuitBreaker.decorateSupplier(save).get();
    }

    @Override
    public TopManager update(TopManager manager) {
        try {
            long id = manager.getId();
            TopManager managerToUpdate = findById(id)
                    .orElseThrow(() -> new IllegalModificationException("No manager with id " + id));
            managerToUpdate = prepareUpdateData(managerToUpdate, manager);
            validate(managerToUpdate);

            TopManager updated = persistManager(managerToUpdate);
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

    private TopManager prepareUpdateData(TopManager savedManager, TopManager data) {
        return TopManager.builder(savedManager)
                .copyNonNullFields(data)
                .build();
    }

    @Override
    public void deleteById(long id) {
        try {
            deleteManager(id);
            logger.info("Manager " + id + " deleted");
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalModificationException("No manager with id " + id, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    private void deleteManager(long id) {
        Runnable delete = () -> {
            managerRepository.deleteById(id);
            managerRepository.flush();
        };

        circuitBreaker.decorateRunnable(delete).run();
    }
}
