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

package employeeservice.web.teammanager;

import employeeservice.service.teammanager.TeamManager;
import employeeservice.service.teammanager.TeamManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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

import javax.validation.Valid;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/team-managers", produces = "application/json")
@CrossOrigin(origins = "*")
public class TeamManagerController {
    private final TeamManagerService managerService;
    private final TeamManagerModelAssembler modelAssembler;

    @Autowired
    public TeamManagerController(TeamManagerService managerService,
                                 TeamManagerModelAssembler modelAssembler) {
        this.managerService = managerService;
        this.modelAssembler = modelAssembler;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TOP_MANAGER')")
    public CollectionModel<EntityModel<TeamManager>> getAll() {
        List<TeamManager> managers = managerService.findAll();
        return modelAssembler.toCollectionModel(managers);
    }

    @GetMapping(params = "email")
    @PreAuthorize("#email == authentication.name or hasAuthority('TOP_MANAGER')")
    public EntityModel<TeamManager> getByEmail(@RequestParam String email) {
        TeamManager manager = managerService.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("No manager with email " + email));
        return modelAssembler.toModel(manager);
    }

    @GetMapping(params = "departmentId")
    @PreAuthorize("hasAuthority('TOP_MANAGER')")
    public CollectionModel<EntityModel<TeamManager>> getAllByDepartmentId(@RequestParam Long departmentId) {
        List<TeamManager> managers = managerService.findAllByDepartmentId(departmentId);
        return modelAssembler.toCollectionModel(managers);
    }

    @GetMapping("/{id}")
    @PostAuthorize("returnObject.content.email == authentication.name or hasAuthority('TOP_MANAGER')")
    public EntityModel<TeamManager> getById(@PathVariable Long id) {
        TeamManager manager = managerService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No manager with id " + id));
        return modelAssembler.toModel(manager);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<TeamManager> save(@RequestBody @Valid TeamManager manager) {
        TeamManager saved = managerService.save(manager);
        return modelAssembler.toModel(saved);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@teamManagerAccessHandler.canPatch(#id)")
    public EntityModel<TeamManager> patchById(@PathVariable Long id,
                                              @RequestBody TeamManager manager) {
        manager.setId(id);
        TeamManager updated = managerService.update(manager);
        return modelAssembler.toModel(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@teamManagerAccessHandler.canDelete(#id)")
    public void deleteById(@PathVariable Long id) {
        managerService.deleteById(id);
    }
}
