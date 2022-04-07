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

package clinicservice.web.doctor;

import clinicservice.data.DoctorRepository;
import clinicservice.service.employee.doctor.Doctor;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Decides if a user has access to doctors.
 */
@Component
public class DoctorAccessHandler {
    private final DoctorRepository repository;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public DoctorAccessHandler(DoctorRepository repository,
                               CircuitBreaker circuitBreaker) {
        this.repository = repository;
        this.circuitBreaker = circuitBreaker;
    }

    /**
     * Decides whether the current user can patch the doctor with the specified ID.
     *
     * @param id of the doctor being patched
     *
     * @return true if patch is available, false otherwise
     */
    public boolean canPatch(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            return false;
        }

        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(authority -> {
                    String auth = authority.getAuthority();
                    return auth.equals("TOP_MANAGER") || auth.equals("TEAM_MANAGER");
                });

        return isManager || isOwner(id, authentication);
    }

    private boolean isOwner(long ownerId, Authentication currAuth) {
        Supplier<Optional<Doctor>> findById = () -> repository.findById(ownerId);
        Optional<Doctor> doctor = circuitBreaker.decorateSupplier(findById).get();
        if (doctor.isEmpty()) {
            return false;
        }

        String ownerEmail = doctor.get().getEmail();
        return ownerEmail.equals(currAuth.getName());
    }

    /**
     * Decides whether the current user can delete the doctor with the specified ID.
     *
     * @param id of the doctor being deleted
     *
     * @return true if delete is available, false otherwise
     */
    public boolean canDelete(Long id) {
        return canPatch(id);
    }
}
