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

package employeeservice.web.topmanager;

import employeeservice.service.topmanager.TopManager;
import employeeservice.service.topmanager.TopManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Decides if a user has access to top managers.
 */
@Component
public class TopManagerAccessHandler {
    private final TopManagerService managerService;

    @Autowired
    public TopManagerAccessHandler(TopManagerService managerService) {
        this.managerService = managerService;
    }

    /**
     * Decides whether the current user can patch the top manager with the specified ID.
     *
     * @param id of the top manager being patched
     *
     * @return true if patch is available, false otherwise
     */
    public boolean canPatch(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated()) {
            return false;
        }

        Optional<TopManager> manager = managerService.findById(id);
        if (manager.isEmpty()) {
            return false;
        }

        String ownerEmail = manager.get().getEmail();
        return auth.getName().equals(ownerEmail);
    }

    /**
     * Decides whether the current user can delete the top manager with the specified ID.
     *
     * @param id of the top manager being deleted
     *
     * @return true if delete is available, false otherwise
     */
    public boolean canDelete(Long id) {
        return canPatch(id);
    }
}
