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

package clinicservice.service.department;

import clinicservice.data.DepartmentRepository;
import clinicservice.service.exception.IllegalModificationException;
import clinicservice.service.exception.RemoteResourceException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
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
public class DepartmentServiceImpl implements DepartmentService {
    private static final Logger logger = LogManager.getLogger(DepartmentServiceImpl.class);

    private final DepartmentRepository repository;
    private final Validator validator;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository repository,
                                 Validator validator,
                                 CircuitBreaker circuitBreaker) {
        this.repository = repository;
        this.validator = validator;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public List<Department> findAll() {
        try {
            Supplier<List<Department>> findAll = repository::findAll;
            return circuitBreaker.decorateSupplier(findAll).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Department database unavailable", e);
        }
    }

    @Override
    public List<Department> findAllByFacilityId(Long id) {
        try {
            Supplier<List<Department>> findAll = () -> repository.findAllByFacilityId(id);
            return circuitBreaker.decorateSupplier(findAll).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Department database unavailable", e);
        }
    }

    @Override
    public Optional<Department> findById(long id) {
        try {
            Supplier<Optional<Department>> findById = () -> repository.findById(id);
            return circuitBreaker.decorateSupplier(findById).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Department database unavailable", e);
        }
    }

    @Override
    public long count() {
        try {
            Supplier<Long> count = repository::count;
            return circuitBreaker.decorateSupplier(count).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Department database unavailable", e);
        }
    }

    @Override
    public Department save(Department department) {
        try {
            validate(department);
            Department departmentToSave = prepareSaveData(department);
            Department saved = persistDepartment(departmentToSave);
            logger.info("Department saved. ID - " + saved.getId());
            return saved;
        } catch (IllegalModificationException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteResourceException("Department database unavailable", e);
        }
    }

    private void validate(Department department) {
        Set<ConstraintViolation<Department>> violations = validator.validate(department);
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<Department> violation : violations) {
                builder.append(violation.getMessage()).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length() - 1);
            String msg = builder.toString().toLowerCase(Locale.ROOT);
            throw new IllegalModificationException(msg);
        }
    }

    private Department prepareSaveData(Department department) {
        Department departmentToSave = new Department(department);
        departmentToSave.setId(null);

        return departmentToSave;
    }

    private Department persistDepartment(Department department) {
        Supplier<Department> save = () -> {
            Department saved = repository.save(department);
            repository.flush();
            return saved;
        };

        return circuitBreaker.decorateSupplier(save).get();
    }

    @Override
    public Department update(Department department) {
        try {
            long id = department.getId();
            Department departmentToUpdate = findById(id)
                    .orElseThrow(() -> new IllegalModificationException("No department with id " + id));
            departmentToUpdate = prepareUpdateData(departmentToUpdate, department);
            validate(departmentToUpdate);

            Department updated = persistDepartment(departmentToUpdate);
            logger.info("Department " + updated.getId() + " updated");
            return updated;
        } catch (IllegalModificationException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteResourceException("Department database unavailable", e);
        }
    }

    private Department prepareUpdateData(Department savedDepartment, Department data) {
        return Department.builder(savedDepartment)
                .copyNonNullFields(data)
                .build();
    }

    @Override
    public void deleteById(long id) {
        try {
            deleteDepartment(id);
            logger.info("Department " + id + " deleted");
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalModificationException("No department with id " + id);
        } catch (DataIntegrityViolationException e) {
            String errorMsg = "Delete all doctors and facilities related to this department first";
            throw new IllegalModificationException(errorMsg, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Department database unavailable", e);
        }
    }

    private void deleteDepartment(long id) {
        Runnable delete = () -> {
            repository.deleteById(id);
            repository.flush();
        };

        circuitBreaker.decorateRunnable(delete).run();
    }
}
