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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import registrationservice.service.registration.Registration;
import registrationservice.service.registration.RegistrationService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/registrations", produces = "application/json")
@CrossOrigin(origins = "*")
public class RegistrationController {
    private final RegistrationService registrationService;
    private final RegistrationModelAssembler modelAssembler;

    @Autowired
    public RegistrationController(RegistrationService registrationService,
                                  RegistrationModelAssembler modelAssembler) {
        this.registrationService = registrationService;
        this.modelAssembler = modelAssembler;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TOP_MANAGER')")
    public CollectionModel<EntityModel<Registration>> getAll() {
        List<Registration> registrations = registrationService.findAll();
        return modelAssembler.toCollectionModel(registrations);
    }

    @GetMapping(params = "doctorId")
    @PostAuthorize("@registrationAccessHandler.canGetAllByDoctorId(returnObject.content)")
    public CollectionModel<EntityModel<Registration>> getAllByDoctorId(Long doctorId) {
        List<Registration> registrations = registrationService.findAllByDoctorId(doctorId);
        return modelAssembler.toCollectionModel(registrations);
    }

    @GetMapping(params = "clientId")
    @PostAuthorize("@registrationAccessHandler.canGetAnyByClientId(returnObject.content)")
    public CollectionModel<EntityModel<Registration>> getAllByClientId(Long clientId) {
        List<Registration> registrations = registrationService.findAllByClientId(clientId);
        filter(registrations);
        return modelAssembler.toCollectionModel(registrations);
    }

    // Cannot use @PostFilter on CollectionModel :(
    private void filter(List<Registration> registrations) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (hasRole(authentication, "USER")) {
            registrations.removeIf(registration -> !clientIsOwner(authentication, registration));
        } else if (hasRole(authentication, "DOCTOR")) {
            registrations.removeIf(registration -> !doctorIsOwner(authentication, registration));
        }
    }

    private boolean hasRole(Authentication authentication, String role) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    private boolean clientIsOwner(Authentication authentication, Registration registration) {
        String email = registration.getClient().getEmail();
        return authentication.getName().equals(email);
    }

    private boolean doctorIsOwner(Authentication authentication, Registration registration) {
        String email = registration.getDoctor().getEmail();
        return authentication.getName().equals(email);
    }

    @GetMapping("/{id}")
    @PostAuthorize("@registrationAccessHandler.canGet(returnObject.content)")
    public EntityModel<Registration> getById(@PathVariable Long id) {
        Registration registration = registrationService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Registration not found: " + id));
        return modelAssembler.toModel(registration);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Registration> save(@RequestBody @Valid Registration registration) {
        Registration saved = registrationService.save(registration);
        return modelAssembler.toModel(saved);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("@registrationAccessHandler.canPatchStatus(#id)")
    public EntityModel<Registration> changeStatus(@PathVariable Long id,
                                                  @RequestParam boolean isActive) {
        Registration registration = registrationService.setActive(id, isActive);
        return modelAssembler.toModel(registration);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        registrationService.deleteById(id);
    }
}
