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

package clinicservice.web.department;

import clinicservice.service.department.Department;
import clinicservice.service.department.DepartmentService;

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

import javax.validation.Valid;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/departments", produces = "application/json")
@CrossOrigin(origins = "*")
public class DepartmentController {
    private final DepartmentService departmentService;
    private final DepartmentModelAssembler modelAssembler;

    @Autowired
    public DepartmentController(DepartmentService departmentService,
                                DepartmentModelAssembler modelAssembler) {
        this.departmentService = departmentService;
        this.modelAssembler = modelAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Department>> getAll() {
        List<Department> departments = departmentService.findAll();
        return modelAssembler.toCollectionModel(departments);
    }

    @GetMapping(params = "facilityId")
    public CollectionModel<EntityModel<Department>> getAllByFacilityId(@RequestParam Long facilityId) {
        List<Department> departments = departmentService.findAllByFacilityId(facilityId);
        return modelAssembler.toCollectionModel(departments);
    }

    @GetMapping("/{id}")
    public EntityModel<Department> getById(@PathVariable Long id) {
        Department department = departmentService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Department not found: " + id));
        return modelAssembler.toModel(department);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Department> save(@RequestBody @Valid Department department) {
        Department saved = departmentService.save(department);
        return modelAssembler.toModel(saved);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public EntityModel<Department> patchById(@PathVariable Long id,
                                             @RequestBody Department department) {
        department.setId(id);
        Department updated = departmentService.update(department);
        return modelAssembler.toModel(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        departmentService.deleteById(id);
    }
}
