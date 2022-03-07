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

package clinicservice.web.facility;

import clinicservice.service.facility.FacilityService;
import clinicservice.service.facility.MedicalFacility;

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
@RequestMapping(path = "/facilities", produces = "application/json")
@CrossOrigin(origins = "*")
public class FacilityController {
    private final FacilityService facilityService;
    private final FacilityModelAssembler modelAssembler;

    @Autowired
    public FacilityController(FacilityService facilityService,
                              FacilityModelAssembler modelAssembler) {
        this.facilityService = facilityService;
        this.modelAssembler = modelAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<MedicalFacility>> getAll() {
        List<MedicalFacility> facilities = facilityService.findAll();
        return modelAssembler.toCollectionModel(facilities);
    }

    @GetMapping(params = "departmentId")
    public CollectionModel<EntityModel<MedicalFacility>> getAllByDepartmentId(@RequestParam Long departmentId) {
        List<MedicalFacility> facilities = facilityService.findAllByDepartmentId(departmentId);
        return modelAssembler.toCollectionModel(facilities);
    }

    @GetMapping("/{id}")
    public EntityModel<MedicalFacility> getById(@PathVariable Long id) {
        MedicalFacility facility = facilityService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No medical facility with id " + id));
        return modelAssembler.toModel(facility);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<MedicalFacility> save(@RequestBody @Valid MedicalFacility facility) {
        MedicalFacility saved = facilityService.save(facility);
        return modelAssembler.toModel(saved);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public EntityModel<MedicalFacility> patchById(@PathVariable Long id,
                                                  @RequestBody MedicalFacility facility) {
        facility.setId(id);
        MedicalFacility updated = facilityService.update(facility);
        return modelAssembler.toModel(updated);
    }

    @DeleteMapping(params = "departmentId")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllFromDepartment(@RequestParam Long departmentId) {
        facilityService.deleteAllByDepartmentId(departmentId);
    }

    @DeleteMapping(params = { "departmentId", "facilityId" })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFromDepartmentById(@RequestParam Long departmentId,
                                         @RequestParam Long facilityId) {
        facilityService.deleteFromDepartmentById(departmentId, facilityId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        facilityService.deleteById(id);
    }
}
