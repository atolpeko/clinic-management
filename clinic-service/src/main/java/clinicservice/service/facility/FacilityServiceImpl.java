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

package clinicservice.service.facility;

import clinicservice.data.DepartmentRepository;
import clinicservice.data.FacilityRepository;
import clinicservice.service.department.Department;
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
public class FacilityServiceImpl implements FacilityService {
    private static final Logger logger = LogManager.getLogger(FacilityServiceImpl.class);

    private final FacilityRepository facilityRepository;
    private final DepartmentRepository departmentRepository;
    private final Validator validator;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public FacilityServiceImpl(FacilityRepository facilityRepository,
                               DepartmentRepository departmentRepository,
                               Validator validator,
                               CircuitBreaker circuitBreaker) {
        this.facilityRepository = facilityRepository;
        this.departmentRepository = departmentRepository;
        this.validator = validator;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public List<MedicalFacility> findAll() {
        try {
            Supplier<List<MedicalFacility>> findAll = facilityRepository::findAll;
            return circuitBreaker.decorateSupplier(findAll).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Facility database unavailable", e);
        }
    }

    @Override
    public List<MedicalFacility> findAllByDepartmentId(Long id) {
        try {
            Supplier<List<MedicalFacility>> findById = () -> facilityRepository.findAllByDepartmentId(id);
            return circuitBreaker.decorateSupplier(findById).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Facility database unavailable", e);
        }
    }

    @Override
    public Optional<MedicalFacility> findById(long id) {
        try {
            Supplier<Optional<MedicalFacility>> findById = () -> facilityRepository.findById(id);
            return circuitBreaker.decorateSupplier(findById).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Facility database unavailable", e);
        }
    }

    @Override
    public long count() {
        try {
            Supplier<Long> count = facilityRepository::count;
            return circuitBreaker.decorateSupplier(count).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Facility database unavailable", e);
        }
    }

    @Override
    public MedicalFacility save(MedicalFacility facility) {
        try {
            validate(facility);
            MedicalFacility facilityToSave = prepareSaveData(facility);
            MedicalFacility saved = persistFacility(facilityToSave);
            logger.info("Medical facility " + saved.getName() + " saved. ID - " + saved.getId());
            return saved;
        } catch (IllegalModificationException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalModificationException("Such a facility already exists", e);
        } catch (Exception e) {
            throw new RemoteResourceException("Facility database unavailable", e);
        }
    }

    private void validate(MedicalFacility facility) {
        Set<ConstraintViolation<MedicalFacility>> violations = validator.validate(facility);
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<MedicalFacility> violation : violations) {
                builder.append(violation.getMessage()).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length() - 1);
            String msg = builder.toString().toLowerCase(Locale.ROOT);
            throw new IllegalModificationException(msg);
        }

        validateDepartments(facility.getDepartments());
    }

    private void validateDepartments(Set<Department> departments) {
        for (Department department : departments) {
            if (!departmentExists(department.getId())) {
                String msg = "No department with id: " + department.getId();
                throw new IllegalModificationException(msg);
            }
        }
    }

    private boolean departmentExists(long id) {
        try {
            Supplier<Optional<Department>> findById = () -> departmentRepository.findById(id);
            Optional<Department> department = circuitBreaker.decorateSupplier(findById).get();
            return department.isPresent();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    private MedicalFacility prepareSaveData(MedicalFacility facility) {
        MedicalFacility facilityToSave = new MedicalFacility(facility);
        facilityToSave.setId(null);
        return facilityToSave;
    }

    private MedicalFacility persistFacility(MedicalFacility facility) {
        Supplier<MedicalFacility> save = () -> {
            MedicalFacility saved = facilityRepository.save(facility);
            facilityRepository.flush();
            return saved;
        };

        return circuitBreaker.decorateSupplier(save).get();
    }

    @Override
    public MedicalFacility update(MedicalFacility facility) {
        try {
            long id = facility.getId();
            MedicalFacility facilityToUpdate = findById(id)
                    .orElseThrow(() -> new IllegalModificationException("No medical facility with id " + id));
            facilityToUpdate = prepareUpdateData(facilityToUpdate, facility);
            validate(facilityToUpdate);

            MedicalFacility updated = persistFacility(facilityToUpdate);
            logger.info("Medical facility " + updated.getId() + " updated");
            return updated;
        } catch (IllegalModificationException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalModificationException("Such a facility already exists", e);
        } catch (Exception e) {
            throw new RemoteResourceException("Facility database unavailable", e);
        }
    }

    private MedicalFacility prepareUpdateData(MedicalFacility savedFacility, MedicalFacility data) {
        return MedicalFacility.builder(savedFacility)
                .copyNonNullFields(data)
                .build();
    }

    @Override
    public void deleteAllByDepartmentId(long departmentId) {
        try {
            deleteAllFromDepartment(departmentId);
            logger.info("All medical facilities deleted from department " + departmentId);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalModificationException("No department with id " + departmentId, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Facility database unavailable", e);
        }
    }

    private void deleteAllFromDepartment(long departmentId) {
        Runnable delete = () -> {
            facilityRepository.deleteAllByDepartmentId(departmentId);
            facilityRepository.flush();
        };

        circuitBreaker.decorateRunnable(delete).run();
    }

    @Override
    public void deleteFromDepartmentById(long departmentId, long facilityId) {
        try {
            deleteFromDepartment(departmentId, facilityId);
            logger.info("Medical facility " + facilityId + " deleted from department " + departmentId);
        } catch (EmptyResultDataAccessException e) {
            String errorMsg = "No department with id " + departmentId +
                    " or facility with id " + facilityId;
            throw new IllegalModificationException(errorMsg, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Facility database unavailable", e);
        }
    }

    private void deleteFromDepartment(long departmentId, long facilityId) {
        Runnable delete = () -> {
            facilityRepository.deleteFromDepartment(departmentId, facilityId);
            facilityRepository.flush();
        };

        circuitBreaker.decorateRunnable(delete).run();
    }

    @Override
    public void deleteById(long id) {
        try {
            deleteFacilityById(id);
            logger.info("Medical facility " + id + " deleted");
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalModificationException("No facility with id " + id, e);
        } catch (DataIntegrityViolationException e) {
            String errorMsg = "Delete all departments related to this facility first";
            throw new IllegalModificationException(errorMsg, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Facility database unavailable", e);
        }
    }

    private void deleteFacilityById(long id) {
        Runnable delete = () -> {
            facilityRepository.deleteById(id);
            facilityRepository.flush();
        };

        circuitBreaker.decorateRunnable(delete).run();
    }
}
