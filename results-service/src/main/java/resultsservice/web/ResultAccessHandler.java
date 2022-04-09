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

package resultsservice.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import resultsservice.service.result.Result;
import resultsservice.service.result.ResultService;

import java.util.Collection;
import java.util.Optional;

/**
 * Decides if a user has access to results.
 */
@Component
public class ResultAccessHandler {
    private static final Logger logger = LogManager.getLogger(ResultAccessHandler.class);
    private final ResultService resultService;

    @Autowired
    public ResultAccessHandler(ResultService resultService) {
        this.resultService = resultService;
    }

    /**
     * Decides whether the current user can access the specified result.
     *
     * @param result result being accessed
     *
     * @return true if access is available, false otherwise
     */
    public boolean canGet(Result result) {
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
                String email = result.getClient().getEmail();
                return email.equals(authentication.getName());
            }

            if (isDoctor) {
                String email = result.getDoctor().getEmail();
                return email.equals(authentication.getName());
            }

            return isTopManager;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    /**
     * Decides whether the current user can access any of the specified results.
     *
     * @param results results being accessed
     *
     * @return true if access is available, false otherwise
     */
    public boolean canGetAnyByClientId(Collection<EntityModel<Result>> results) {
        if (results.isEmpty()) {
            return false;
        }

        return results.stream()
                .map(EntityModel::getContent)
                .anyMatch(this::canGet);
    }

    /**
     * Decides whether the current user can save the specified result.
     *
     * @param result result being saved
     *
     * @return true if access is available, false otherwise
     */
    public boolean canPost(Result result) {
        return canGet(result);
    }

    /**
     * Decides whether the current user can patch the result with the specified ID.
     *
     * @param resultId ID if the result being patched
     *
     * @return true if access is available, false otherwise
     */
    public boolean canPatch(long resultId) {
        try {
            Optional<Result> registration = resultService.findById(resultId);
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

    /**
     * Decides whether the current user can delete the result with the specified ID.
     *
     * @param resultId ID if the result being deleted
     *
     * @return true if access is available, false otherwise
     */
    public boolean canDelete(long resultId) {
        return canPatch(resultId);
    }
}
