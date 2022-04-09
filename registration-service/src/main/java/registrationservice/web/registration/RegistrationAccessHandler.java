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

package registrationservice.web.registration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import registrationservice.service.registration.Registration;
import registrationservice.service.registration.RegistrationService;

import java.util.Collection;
import java.util.Optional;

/**
 * Decides if a user has access to registrations.
 */
@Component
public class RegistrationAccessHandler {
    private static final Logger logger = LogManager.getLogger(RegistrationAccessHandler.class);
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationAccessHandler(RegistrationService service) {
        this.registrationService = service;
    }

    /**
     * Decides whether the current user can access the specified registration.
     *
     * @param registration registration being accessed
     *
     * @return true if access is available, false otherwise
     */
    public boolean canGet(Registration registration) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!authentication.isAuthenticated()) {
                return false;
            }

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean isUser = authorities.stream()
                    .anyMatch(authority -> authority.getAuthority().equals("USER"));
            boolean isDoctor = authorities.stream()
                    .anyMatch(authority -> authority.getAuthority().equals("DOCTOR"));
            boolean isTopManager = authorities.stream()
                    .anyMatch(authority -> authority.getAuthority().equals("TOP_MANAGER"));

            if (isUser) {
                String email = registration.getClient().getEmail();
                return email.equals(authentication.getName());
            }

            if (isDoctor) {
                String email = registration.getDoctor().getEmail();
                return email.equals(authentication.getName());
            }

            return isTopManager;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }


    /**
     * Decides whether the current user can access the specified registrations by doctor ID.
     *
     * @param registrations registrations being accessed
     *
     * @return true if access is available, false otherwise
     */
    public boolean canGetAllByDoctorId(Collection<EntityModel<Registration>> registrations) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("USER"));
        if (!authentication.isAuthenticated() || isUser) {
            return false;
        }

        if (registrations.isEmpty()) {
            return false;
        }

        return registrations.stream()
                .map(EntityModel::getContent)
                .allMatch(this::canGet);
    }

    /**
     * Decides whether the current user can access any of the specified registrations.
     *
     * @param registrations registrations being accessed
     *
     * @return true if access is available, false otherwise
     */
    public boolean canGetAnyByClientId(Collection<EntityModel<Registration>> registrations) {
        if (registrations.isEmpty()) {
            return false;
        }

        return registrations.stream()
                .map(EntityModel::getContent)
                .anyMatch(this::canGet);
    }

    /**
     * Decides whether the current user can change a status of the registration with the specified ID.
     *
     * @param registrationId ID if the registration being patched
     *
     * @return true if access is available, false otherwise
     */
    public boolean canPatchStatus(long registrationId) {
        try {
            Optional<Registration> registration = registrationService.findById(registrationId);
            if (registration.isEmpty()) {
                return false;
            } else {
                return canGet(registration.get());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
