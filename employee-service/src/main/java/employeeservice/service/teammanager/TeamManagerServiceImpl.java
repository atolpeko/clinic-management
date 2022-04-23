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
            TeamManager managerToSave = prepareSaveData(manager);
            TeamManager saved = persistManger(managerToSave);
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

        if (manager.getDepartment() == null) {
            throw new IllegalModificationException("Department is mandatory");
        }

        validateDepartment(manager.getDepartment());
        validateTeam(manager.getTeam());
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

    private TeamManager prepareSaveData(TeamManager manager) {
        String password = passwordEncoder.encode(manager.getPassword());
        TeamManager managerToSave = new TeamManager(manager);
        managerToSave.setId(null);
        managerToSave.setPassword(password);

        return managerToSave;
    }

    private TeamManager persistManger(TeamManager manager) {
        Supplier<TeamManager> save = () -> {
            TeamManager saved = managerRepository.save(manager);
            managerRepository.flush();
            return saved;
        };

        return circuitBreaker.decorateSupplier(save).get();
    }

    @Override
    public TeamManager update(TeamManager manager) {
        try {
            long id = manager.getId();
            TeamManager managerToUpdate = findById(id)
                    .orElseThrow(() -> new IllegalModificationException("No manager with id " +id));
            managerToUpdate = prepareUpdateData(managerToUpdate, manager);
            validate(managerToUpdate);

            TeamManager updated = persistManger(manager);
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

    private TeamManager prepareUpdateData(TeamManager saved, TeamManager data) {
        return TeamManager.builder(saved)
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
