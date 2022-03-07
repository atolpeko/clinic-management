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

import clinicservice.web.department.DepartmentController;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Converts a Doctor domain class into a RepresentationModel.
 */
@Component
public class DoctorModelAssembler implements RepresentationModelAssembler<Doctor, EntityModel<Doctor>> {

    @Override
    public EntityModel<Doctor> toModel(Doctor entity) {
        long id = entity.getId();
        long departmentId = entity.getDepartment().getId();
        return EntityModel.of(entity,
                linkTo(methodOn(DoctorController.class).getById(id)).withSelfRel(),
                linkTo(methodOn(DoctorController.class).getAll()).withRel("all"),
                linkTo(methodOn(DepartmentController.class).getById(departmentId)).withRel("department"));
    }

    @Override
    public CollectionModel<EntityModel<Doctor>> toCollectionModel(Iterable<? extends Doctor> entities) {
        CollectionModel<EntityModel<Doctor>> collectionModel =
                RepresentationModelAssembler.super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(DoctorController.class).getAll()).withSelfRel());
        return collectionModel;
    }
}
