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

package clinicservice.service.employee.doctor;

import clinicservice.data.DepartmentRepository;
import clinicservice.data.DoctorRepository;
import clinicservice.service.Address;
import clinicservice.service.department.Department;
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
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public DoctorServiceImpl(DoctorRepository doctorRepository,
                             DepartmentRepository departmentRepository,
                             PasswordEncoder passwordEncoder,
                             Validator validator,
                             CircuitBreaker circuitBreaker) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
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
            Doctor doctorToSave = new Doctor(doctor);
            doctorToSave.setId(null);
            doctorToSave.setPassword(passwordEncoder.encode(doctor.getPassword()));

            Supplier<Doctor> save = () -> {
                Doctor saved = doctorRepository.save(doctorToSave);
                doctorRepository.flush();
                return saved;
            };

            Doctor saved = circuitBreaker.decorateSupplier(save).get();
            logger.info("Doctor " + saved.getEmail() + " saved. ID - " + saved.getId());
            return saved;
        } catch (IllegalModificationException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            String msg = "Such an employee already exists: " + doctor.getEmail();
            throw new IllegalModificationException(msg, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
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

        if (doctor.getDepartment().getId() == null) {
            throw new IllegalModificationException("Department id is mandatory");
        }

        if (!departmentExists(doctor.getDepartment().getId())) {
            String msg = "No department with id: " + doctor.getDepartment().getId();
            throw new IllegalModificationException(msg);
        }
    }

    private boolean departmentExists(long id) {
        Supplier<Optional<Department>> findById = () -> departmentRepository.findById(id);
        Optional<Department> department = circuitBreaker.decorateSupplier(findById).get();
        return department.isPresent();
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

            Supplier<Doctor> update = () -> {
                Doctor updated = doctorRepository.save(doctorToUpdate);
                doctorRepository.flush();
                return updated;
            };

            Doctor updated = circuitBreaker.decorateSupplier(update).get();
            logger.info("Doctor " + updated.getId() + " updated");
            return updated;
        } catch (IllegalModificationException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            String msg = "Such an employee already exists: " + doctor.getEmail();
            throw new IllegalModificationException(msg, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    private void prepareUpdateData(Doctor doctor, Doctor updateData) {
        if (updateData.getEmail() != null) {
            doctor.setEmail(updateData.getEmail());
        }
        if (updateData.getPassword() != null) {
            doctor.setPassword(passwordEncoder.encode(updateData.getPassword()));
        }
        if (updateData.getDepartment() != null) {
            doctor.setDepartment(updateData.getDepartment());
        }
        if (updateData.getSpecialty() != null) {
            doctor.setSpecialty(updateData.getSpecialty());
        }
        if (updateData.getPracticeBeginningDate() != null) {
            doctor.setPracticeBeginningDate(updateData.getPracticeBeginningDate());
        }
        if (updateData.getPersonalData() != null) {
            preparePersonalData(updateData.getPersonalData(), doctor.getPersonalData());
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
                doctorRepository.deleteById(id);
                doctorRepository.flush();
            };

            circuitBreaker.decorateRunnable(delete).run();
            logger.info("Doctor " + id + " deleted");
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalModificationException("No doctor with id " + id, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }
}
