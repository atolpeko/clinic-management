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

package employeeservice.web.doctor;

import employeeservice.service.doctor.Doctor;
import employeeservice.service.doctor.DoctorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Decides if a user has access to doctors.
 */
@Component
public class DoctorAccessHandler {
    private final DoctorService doctorService;

    @Autowired
    public DoctorAccessHandler(DoctorService doctorService) {
        this.doctorService = doctorService;
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

        return isManager(authentication) || isOwner(id, authentication);
    }

    private boolean isManager(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> {
                    String auth = authority.getAuthority();
                    return auth.equals("TOP_MANAGER") || auth.equals("TEAM_MANAGER");
                });
    }

    private boolean isOwner(long ownerId, Authentication authentication) {
        Optional<Doctor> doctor = doctorService.findById(ownerId);
        if (doctor.isEmpty()) {
            return false;
        }

        String ownerEmail = doctor.get().getEmail();
        return authentication.getName().equals(ownerEmail);
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
