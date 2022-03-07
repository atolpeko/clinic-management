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
import clinicservice.web.doctor.DoctorController;
import clinicservice.web.facility.FacilityController;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Converts a Department domain class into a RepresentationModel.
 */
@Component
public class DepartmentModelAssembler
        implements RepresentationModelAssembler<Department, EntityModel<Department>> {

    @Override
    public EntityModel<Department> toModel(Department entity) {
        long id = entity.getId();
        return EntityModel.of(entity,
                linkTo(methodOn(DoctorController.class).getAllByDepartmentId(id)).withRel("doctors"),
                linkTo(methodOn(FacilityController.class).getAllByDepartmentId(id)).withRel("facilities"),
                linkTo(methodOn(DepartmentController.class).getById(id)).withSelfRel(),
                linkTo(methodOn(DepartmentController.class).getAll()).withRel("all"));
    }

    @Override
    public CollectionModel<EntityModel<Department>> toCollectionModel(Iterable<? extends Department> entities) {
        CollectionModel<EntityModel<Department>> collectionModel =
                RepresentationModelAssembler.super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(DepartmentController.class).getAll()).withSelfRel());
        return collectionModel;
    }
}
