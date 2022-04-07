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

package clinicservice.web.manager.teammanager;

import clinicservice.data.TeamManagerRepository;
import clinicservice.service.employee.manager.teammanager.TeamManager;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Decides if a user has access to team managers.
 */
@Component
public class TeamManagerAccessHandler {
    private final TeamManagerRepository repository;
    private final CircuitBreaker circuitBreaker;

    @Autowired
    public TeamManagerAccessHandler(TeamManagerRepository repository,
                                    CircuitBreaker circuitBreaker) {
        this.repository = repository;
        this.circuitBreaker = circuitBreaker;
    }

    /**
     * Decides whether the current user can patch the team manager with the specified ID.
     *
     * @param id of the team manager being patched
     *
     * @return true if patch is available, false otherwise
     */
    public boolean canPatch(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated()) {
            return false;
        }

        boolean isTopManager = auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("TOP_MANAGER"));
        return isTopManager || isOwner(id, auth);
    }

    private boolean isOwner(long ownerId, Authentication currAuth) {
        Supplier<Optional<TeamManager>> findById = () -> repository.findById(ownerId);
        Optional<TeamManager> manager = circuitBreaker.decorateSupplier(findById).get();
        if (manager.isEmpty()) {
            return false;
        }

        String ownerEmail = manager.get().getEmail();
        return ownerEmail.equals(currAuth.getName());
    }

    /**
     * Decides whether the current user can delete the team manager with the specified ID.
     *
     * @param id of the team manager being deleted
     *
     * @return true if delete is available, false otherwise
     */
    public boolean canDelete(Long id) {
        return canPatch(id);
    }
}
