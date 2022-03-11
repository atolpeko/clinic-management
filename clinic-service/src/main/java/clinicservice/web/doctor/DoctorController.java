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

package clinicservice.web.doctor;

import clinicservice.service.doctor.Doctor;
import clinicservice.service.doctor.DoctorService;

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
@RequestMapping(path = "/doctors", produces = "application/json")
@CrossOrigin(origins = "*")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorModelAssembler modelAssembler;

    @Autowired
    public DoctorController(DoctorService doctorService,
                            DoctorModelAssembler modelAssembler) {
        this.doctorService = doctorService;
        this.modelAssembler = modelAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Doctor>> getAll() {
        List<Doctor> doctors = doctorService.findAll();
        return modelAssembler.toCollectionModel(doctors);
    }

    @GetMapping(params = "departmentId")
    public CollectionModel<EntityModel<Doctor>> getAllByDepartmentId(@RequestParam Long departmentId) {
        List<Doctor> doctors = doctorService.findAllByDepartmentId(departmentId);
        return modelAssembler.toCollectionModel(doctors);
    }

    @GetMapping(params = "specialty")
    public CollectionModel<EntityModel<Doctor>> getAllBySpecialty(@RequestParam String specialty) {
        List<Doctor> doctors = doctorService.findAllBySpecialty(specialty);
        return modelAssembler.toCollectionModel(doctors);
    }

    @GetMapping("/{id}")
    public EntityModel<Doctor> getById(@PathVariable Long id) {
        Doctor doctor = doctorService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No doctor with id " + id));
        return modelAssembler.toModel(doctor);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Doctor> save(@RequestBody @Valid Doctor doctor) {
        Doctor saved = doctorService.save(doctor);
        return modelAssembler.toModel(saved);
    }

    @PatchMapping("/{id}")
    public EntityModel<Doctor> patchById(@PathVariable Long id,
                                         @RequestBody Doctor doctor) {
        doctor.setId(id);
        Doctor updated = doctorService.update(doctor);
        return modelAssembler.toModel(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        doctorService.deleteById(id);
    }
}
