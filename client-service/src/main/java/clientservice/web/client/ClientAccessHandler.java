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

package clientservice.web.client;

import clientservice.service.Client;
import clientservice.service.ClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Decides if a user has access to clients.
 */
@Component
public class ClientAccessHandler {
    private final ClientService clientService;

    @Autowired
    public ClientAccessHandler(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Decides whether the current user can patch the client with the specified ID.
     *
     * @param id of the client being patched
     *
     * @return true if patch is available, false otherwise
     */
    public boolean canPatch(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.isAuthenticated()) {
            return false;
        }

        return isTopManager(auth) || isOwner(id, auth);
    }

    private boolean isTopManager(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("TOP_MANAGER"));
    }

    private boolean isOwner(long ownerId, Authentication authentication) {
        Optional<Client> client = clientService.findById(ownerId);
        if (client.isEmpty()) {
            return false;
        }

        String ownerEmail = client.get().getEmail();
        return authentication.getName().equals(ownerEmail);
    }

    /**
     * Decides whether the current user can delete the client with the specified ID.
     *
     * @param id of the client being deleted
     *
     * @return true if delete is available, false otherwise
     */
    public boolean canDelete(Long id) {
        return canPatch(id);
    }
}
