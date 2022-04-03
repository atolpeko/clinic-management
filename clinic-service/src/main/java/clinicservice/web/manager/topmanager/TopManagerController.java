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

package clinicservice.web.manager.topmanager;

import clinicservice.service.employee.manager.topmanager.TopManager;
import clinicservice.service.employee.manager.topmanager.TopManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/top-managers", produces = "application/json")
@CrossOrigin(origins = "*")
public class TopManagerController {
    private final TopManagerService managerService;
    private final TopManagerModelAssembler modelAssembler;

    @Autowired
    public TopManagerController(TopManagerService managerService,
                                TopManagerModelAssembler modelAssembler) {
        this.managerService = managerService;
        this.modelAssembler = modelAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<TopManager>> getAll() {
        List<TopManager> managers = managerService.findAll();
        return modelAssembler.toCollectionModel(managers);
    }

    @GetMapping("/{id}")
    public EntityModel<TopManager> getById(@PathVariable Long id) {
        TopManager manager = managerService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No manager with id " + id));
        return modelAssembler.toModel(manager);
    }

    @GetMapping(value = "/{email}", params = "email")
    public EntityModel<TopManager> getByEmail(@PathVariable String email) {
        TopManager manager = managerService.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("No manager with email " + email));
        return modelAssembler.toModel(manager);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<TopManager> save(@RequestBody @Valid TopManager manager) {
        TopManager saved = managerService.save(manager);
        return modelAssembler.toModel(saved);
    }

    @PatchMapping("/{id}")
    public EntityModel<TopManager> patchById(@PathVariable Long id,
                                              @RequestBody TopManager manager) {
        manager.setId(id);
        TopManager updated = managerService.update(manager);
        return modelAssembler.toModel(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        managerService.deleteById(id);
    }
}
