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

package clinicservice.service.doctor;

import clinicservice.data.DepartmentRepository;
import clinicservice.data.DoctorRepository;
import clinicservice.service.department.Department;
import clinicservice.service.exception.IllegalModificationException;
import clinicservice.service.exception.RemoteResourceException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
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
public class DoctorServiceImpl implements DoctorService {
    private static final Logger logger = LogManager.getLogger(DoctorServiceImpl.class);

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final Validator validator;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public DoctorServiceImpl(DoctorRepository doctorRepository,
                             DepartmentRepository departmentRepository,
                             Validator validator,
                             CircuitBreaker circuitBreaker) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
        this.validator = validator;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public List<Doctor> findAll() {
        try {
            Supplier<List<Doctor>> findAll = doctorRepository::findAll;
            return circuitBreaker.decorateSupplier(findAll).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Doctor database unavailable", e);
        }
    }

    @Override
    public List<Doctor> findAllByDepartmentId(Long id) {
        try {
            Supplier<List<Doctor>> findById = () -> doctorRepository.findAllByDepartmentId(id);
            return circuitBreaker.decorateSupplier(findById).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Doctor database unavailable", e);
        }
    }

    @Override
    public Optional<Doctor> findById(long id) {
        try {
            Supplier<Optional<Doctor>> findById = () -> doctorRepository.findById(id);
            return circuitBreaker.decorateSupplier(findById).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Doctor database unavailable", e);
        }
    }

    @Override
    public Doctor save(Doctor doctor) {
        try {
            validate(doctor);
            Doctor doctorToSave = new Doctor(doctor);
            doctorToSave.setId(null);
            loadDepartment(doctorToSave);

            Supplier<Doctor> save = () -> doctorRepository.save(doctorToSave);
            Runnable flush = doctorRepository::flush;

            Doctor saved = circuitBreaker.decorateSupplier(save).get();
            circuitBreaker.decorateRunnable(flush).run();
            logger.info("Doctor " + saved.getName() + " saved. ID - " + saved.getId());
            return saved;
        } catch (IllegalModificationException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteResourceException("Doctor database unavailable", e);
        }
    }

    private void loadDepartment(Doctor doctor) {
        if (doctor.getDepartment() == null) {
            return;
        }

        long departmentId = doctor.getDepartment().getId();
        Supplier<Optional<Department>> findById = () -> departmentRepository.findById(departmentId);
        Department department = circuitBreaker.decorateSupplier(findById)
                .get()
                .orElseThrow(() -> new IllegalModificationException("No department with id " + departmentId));
        department.addDoctor(doctor);
    }

    private void validate(Doctor doctor) {
        Set<ConstraintViolation<Doctor>> violations = validator.validate(doctor);
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<Doctor> violation : violations) {
                builder.append(violation.getMessage()).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length() - 1);
            String msg = builder.toString().toLowerCase(Locale.ROOT);
            throw new IllegalModificationException(msg);
        }
    }

    @Override
    public Doctor update(Doctor doctor) {
        try {
            Supplier<Optional<Doctor>> findById = () -> doctorRepository.findById(doctor.getId());
            Doctor doctorToUpdate = circuitBreaker.decorateSupplier(findById)
                    .get()
                    .orElseThrow(() -> new IllegalModificationException("No doctor with id " + doctor.getId()));
            prepareUpdateData(doctorToUpdate, doctor);
            validate(doctorToUpdate);

            Supplier<Doctor> save = () -> doctorRepository.save(doctorToUpdate);
            Runnable flush = doctorRepository::flush;

            Doctor updated = circuitBreaker.decorateSupplier(save).get();
            circuitBreaker.decorateRunnable(flush).run();
            logger.info("Doctor " + updated.getId() + " updated");
            return updated;
        } catch (IllegalModificationException e) {
            throw e;
        } catch (Exception e) {
            throw new RemoteResourceException("Doctor database unavailable", e);
        }
    }

    private void prepareUpdateData(Doctor doctor, Doctor updateData) {
        if (updateData.getName() != null) {
            doctor.setName(updateData.getName());
        }
        if (updateData.getSpecialty() != null) {
            doctor.setSpecialty(updateData.getSpecialty());
        }
        if (updateData.getPracticeBeginningDate() != null) {
            doctor.setPracticeBeginningDate(updateData.getPracticeBeginningDate());
        }
        if (updateData.getDepartment() != null) {
            doctor.setDepartment(updateData.getDepartment());
            loadDepartment(doctor);
        }
    }

    @Override
    public void deleteById(long id) {
        try {
            Runnable delete = () -> doctorRepository.deleteById(id);
            Runnable flush = doctorRepository::flush;
            circuitBreaker.decorateRunnable(delete).run();
            circuitBreaker.decorateRunnable(flush).run();
            logger.info("Doctor " + id + " deleted");
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalModificationException("No doctor with id " + id);
        } catch (Exception e) {
            throw new RemoteResourceException("Doctor database unavailable", e);
        }
    }
}
