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

package employeeservice.service.doctor;

import employeeservice.data.DoctorRepository;
import employeeservice.service.exception.IllegalModificationException;
import employeeservice.service.exception.RemoteResourceException;
import employeeservice.service.external.ClinicServiceFeignClient;
import employeeservice.service.external.Department;

import feign.FeignException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final ClinicServiceFeignClient clinicFeignClient;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public DoctorServiceImpl(DoctorRepository doctorRepository,
                             ClinicServiceFeignClient clinicFeignClient,
                             PasswordEncoder passwordEncoder,
                             Validator validator,
                             CircuitBreaker circuitBreaker) {
        this.doctorRepository = doctorRepository;
        this.clinicFeignClient = clinicFeignClient;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public List<Doctor> findAll() {
        try {
            Supplier<List<Doctor>> findAll = doctorRepository::findAll;
            return circuitBreaker.decorateSupplier(findAll).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    @Override
    public List<Doctor> findAllByDepartmentId(Long id) {
        try {
            Supplier<List<Doctor>> findByDepartmentId = () -> doctorRepository.findAllByDepartmentId(id);
            return circuitBreaker.decorateSupplier(findByDepartmentId).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    @Override
    public List<Doctor> findAllBySpecialty(String specialty) {
        try {
            Supplier<List<Doctor>> findBySpecialty = () -> doctorRepository.findAllBySpecialty(specialty);
            return circuitBreaker.decorateSupplier(findBySpecialty).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    @Override
    public Optional<Doctor> findById(long id) {
        try {
            Supplier<Optional<Doctor>> findById = () -> doctorRepository.findById(id);
            return circuitBreaker.decorateSupplier(findById).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    @Override
    public Optional<Doctor> findByEmail(String email) {
        try {
            Supplier<Optional<Doctor>> findById = () -> doctorRepository.findByEmail(email);
            return circuitBreaker.decorateSupplier(findById).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    @Override
    public long count() {
        try {
            Supplier<Long> count = doctorRepository::count;
            return circuitBreaker.decorateSupplier(count).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    @Override
    public Doctor save(Doctor doctor) {
        try {
            validate(doctor);
            Doctor doctorToSave = prepareSaveData(doctor);
            Doctor saved = persistDoctor(doctorToSave);
            logger.info("Doctor " + saved.getEmail() + " saved. ID - " + saved.getId());
            return saved;
        } catch (IllegalModificationException | RemoteResourceException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            String msg = "Such an employee already exists: " + doctor.getEmail();
            throw new IllegalModificationException(msg, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    private void validate(Doctor doctor) {
        validateDoctor(doctor);
        validateDepartment(doctor.getDepartment());
    }

    private void validateDoctor(Doctor doctor) {
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

        if (doctor.getDepartment() == null) {
            throw new IllegalModificationException("Department is mandatory");
        }
    }

    private void validateDepartment(Department department) {
        try {
            if (department.getId() == null) {
                throw new IllegalModificationException("Department ID is mandatory");
            }

            long id = department.getId();
            Supplier<Department> findById = () -> clinicFeignClient.findDepartmentById(id);
            circuitBreaker.decorateSupplier(findById).get();
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new IllegalModificationException("No department with id " + department.getId());
            } else {
                logger.error(e.getMessage());
                throw new RemoteResourceException("Clinic service unavailable", e);
            }
        }
    }

    private Doctor prepareSaveData(Doctor doctor) {
        String password = passwordEncoder.encode(doctor.getPassword());
        Doctor doctorToSave = new Doctor(doctor);
        doctorToSave.setId(null);
        doctorToSave.setPassword(password);

        return doctorToSave;
    }

    private Doctor persistDoctor(Doctor doctor) {
        Supplier<Doctor> save = () -> {
            Doctor saved = doctorRepository.save(doctor);
            doctorRepository.flush();
            return saved;
        };

        return circuitBreaker.decorateSupplier(save).get();
    }

    @Override
    public Doctor update(Doctor doctor) {
        try {
            long id = doctor.getId();
            Doctor doctorToUpdate = findById(id)
                    .orElseThrow(() -> new IllegalModificationException("No doctor with id " + id));
            doctorToUpdate = prepareUpdateData(doctorToUpdate, doctor);
            validate(doctorToUpdate);

            Doctor updated = persistDoctor(doctorToUpdate);
            logger.info("Doctor " + updated.getId() + " updated");
            return updated;
        } catch (IllegalModificationException | RemoteResourceException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            String msg = "Such an employee already exists: " + doctor.getEmail();
            throw new IllegalModificationException(msg, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    private Doctor prepareUpdateData(Doctor savedDoctor, Doctor updateData) {
        return Doctor.builder(savedDoctor)
                .copyNonNullFields(updateData)
                .build();
    }

    @Override
    public void deleteById(long id) {
        try {
            deleteDoctor(id);
            logger.info("Doctor " + id + " deleted");
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalModificationException("No doctor with id " + id, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    private void deleteDoctor(long id) {
        Runnable delete = () -> {
            doctorRepository.deleteById(id);
            doctorRepository.flush();
        };

        circuitBreaker.decorateRunnable(delete).run();
    }
}
