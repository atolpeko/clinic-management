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

package employeeservice.service.teammanager;

import employeeservice.data.DoctorRepository;
import employeeservice.data.TeamManagerRepository;
import employeeservice.service.Address;
import employeeservice.service.PersonalData;
import employeeservice.service.doctor.Doctor;
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
public class TeamManagerServiceImpl implements TeamManagerService {
    private static final Logger logger = LogManager.getLogger(TeamManagerServiceImpl.class);

    private final TeamManagerRepository managerRepository;
    private final DoctorRepository doctorRepository;
    private final ClinicServiceFeignClient clinicFeignClient;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public TeamManagerServiceImpl(TeamManagerRepository managerRepository,
                                  DoctorRepository doctorRepository,
                                  ClinicServiceFeignClient clinicFeignClient,
                                  PasswordEncoder passwordEncoder,
                                  Validator validator,
                                  CircuitBreaker circuitBreaker) {
        this.managerRepository = managerRepository;
        this.doctorRepository = doctorRepository;
        this.clinicFeignClient = clinicFeignClient;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public List<TeamManager> findAll() {
        try {
            Supplier<List<TeamManager>> findAll = managerRepository::findAll;
            return circuitBreaker.decorateSupplier(findAll).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    @Override
    public List<TeamManager> findAllByDepartmentId(Long id) {
        try {
            Supplier<List<TeamManager>> findByDepartmentId = () -> managerRepository.findAllByDepartmentId(id);
            return circuitBreaker.decorateSupplier(findByDepartmentId).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    @Override
    public Optional<TeamManager> findById(long id) {
        try {
            Supplier<Optional<TeamManager>> findById = () -> managerRepository.findById(id);
            return circuitBreaker.decorateSupplier(findById).get();
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    @Override
    public Optional<TeamManager> findByEmail(String email) {
        try {
            Supplier<Optional<TeamManager>> findById = () -> managerRepository.findByEmail(email);
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
    public TeamManager save(TeamManager manager) {
        try {
            validate(manager);
            TeamManager managerToSave = new TeamManager(manager);
            managerToSave.setId(null);
            managerToSave.setPassword(passwordEncoder.encode(manager.getPassword()));

            Supplier<TeamManager> save = () -> {
                TeamManager saved = managerRepository.save(managerToSave);
                managerRepository.flush();
                return saved;
            };

            TeamManager saved = circuitBreaker.decorateSupplier(save).get();
            logger.info("Manager " + saved.getEmail() + " saved. ID - " + saved.getId());
            return saved;
        } catch (IllegalModificationException | RemoteResourceException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            String msg = "Such an employee already exists: " + manager.getEmail();
            throw new IllegalModificationException(msg, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    private void validate(TeamManager manager) {
        Set<ConstraintViolation<TeamManager>> violations = validator.validate(manager);
        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (ConstraintViolation<TeamManager> violation : violations) {
                builder.append(violation.getMessage()).append(", ");
            }

            builder.delete(builder.length() - 2, builder.length() - 1);
            String msg = builder.toString().toLowerCase(Locale.ROOT);
            throw new IllegalModificationException(msg);
        }

        if (manager.getDepartment() == null || manager.getDepartment().getId() == null) {
            throw new IllegalModificationException("Department is mandatory");
        }

        if (!departmentExists(manager.getDepartment().getId())) {
            String msg = "No department with id: " + manager.getDepartment().getId();
            throw new IllegalModificationException(msg);
        }

        validateTeam(manager.getTeam());
    }

    private boolean departmentExists(long id) {
        try {
            Supplier<Optional<Department>> findById = () -> clinicFeignClient.findDepartmentById(id);
            Optional<Department> department = circuitBreaker.decorateSupplier(findById).get();
            return department.isPresent();
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new IllegalModificationException("No department with id " + id);
            } else {
                logger.error(e.getMessage());
                throw new RemoteResourceException("Clinic service unavailable", e);
            }
        }
    }

    private void validateTeam(Set<Doctor> team) {
        for (Doctor doctor : team) {
            long id = doctor.getId();
            Supplier<Optional<Doctor>> findById = () -> doctorRepository.findById(id);
            Optional<Doctor> saved = circuitBreaker.decorateSupplier(findById).get();
            if (saved.isEmpty()) {
                throw new IllegalModificationException("Such a doctor does not exist: " + id);
            }
        }
    }

    @Override
    public TeamManager update(TeamManager manager) {
        try {
            Supplier<Optional<TeamManager>> findById = () -> managerRepository.findById(manager.getId());
            TeamManager managerToUpdate = circuitBreaker.decorateSupplier(findById)
                    .get()
                    .orElseThrow(() -> new IllegalModificationException("No manager with id " + manager.getId()));
            prepareUpdateData(managerToUpdate, manager);
            validate(managerToUpdate);

            Supplier<TeamManager> update = () -> {
                TeamManager updated = managerRepository.save(managerToUpdate);
                managerRepository.flush();
                return updated;
            };

            TeamManager updated = circuitBreaker.decorateSupplier(update).get();
            logger.info("Manager " + updated.getId() + " updated");
            return updated;
        } catch (IllegalModificationException | RemoteResourceException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            String msg = "Such an employee already exists: " + manager.getEmail();
            throw new IllegalModificationException(msg, e);
        } catch (Exception e) {
            throw new RemoteResourceException("Employee database unavailable", e);
        }
    }

    private void prepareUpdateData(TeamManager manager, TeamManager updateData) {
        if (updateData.getEmail() != null) {
            manager.setEmail(updateData.getEmail());
        }
        if (updateData.getPassword() != null) {
            manager.setPassword(passwordEncoder.encode(updateData.getPassword()));
        }
        if (updateData.getDepartment() != null) {
            manager.setDepartment(updateData.getDepartment());
        }
        if (!updateData.getTeam().isEmpty()) {
            manager.setTeam(updateData.getTeam());
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
