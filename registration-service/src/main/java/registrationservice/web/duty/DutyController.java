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

package registrationservice.web.duty;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import registrationservice.service.duty.Duty;
import registrationservice.service.duty.DutyService;

import javax.validation.Valid;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/services", produces = "application/json")
@CrossOrigin(origins = "*")
public class DutyController {
    private final DutyService dutyService;
    private final DutyModelAssembler modelAssembler;

    @Autowired
    public DutyController(DutyService dutyService,
                          DutyModelAssembler modelAssembler) {
        this.dutyService = dutyService;
        this.modelAssembler = modelAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Duty>> getAll() {
        List<Duty> duties = dutyService.findAll();
        return modelAssembler.toCollectionModel(duties);
    }

    @GetMapping(params = "name")
    public EntityModel<Duty> getByName(@RequestParam String name) {
        Duty duty = dutyService.findByName(name)
                .orElseThrow(() -> new NoSuchElementException("Service not found: " + name));
        return modelAssembler.toModel(duty);
    }

    @GetMapping("/{id}")
    public EntityModel<Duty> getById(@PathVariable Long id) {
        Duty duty = dutyService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Service not found: " + id));
        return modelAssembler.toModel(duty);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Duty> save(@RequestBody @Valid Duty duty) {
        Duty saved = dutyService.save(duty);
        return modelAssembler.toModel(saved);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public EntityModel<Duty> patchById(@PathVariable Long id,
                                       @RequestBody Duty duty) {
        duty.setId(id);
        Duty updated = dutyService.update(duty);
        return modelAssembler.toModel(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        dutyService.deleteById(id);
    }
}
