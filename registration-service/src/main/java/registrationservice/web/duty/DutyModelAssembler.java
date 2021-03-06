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

import registrationservice.service.duty.Duty;

import org.springframework.hateoas.server.mvc.BasicLinkBuilder;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Converts a Duty domain class into a RepresentationModel.
 */
@Component
public class DutyModelAssembler implements RepresentationModelAssembler<Duty, EntityModel<Duty>> {

    @Override
    public EntityModel<Duty> toModel(Duty entity) {
        Link doctorsLink = BasicLinkBuilder
                .linkToCurrentMapping()
                .slash("/doctors/?specialty=" + entity.getNeededSpecialty())
                .withRel("doctors");
        EntityModel<Duty> entityModel = EntityModel.of(entity, doctorsLink);
        entityModel.add(linkTo(methodOn(DutyController.class).getById(entity.getId())).withSelfRel(),
                linkTo(methodOn(DutyController.class).getAll()).withRel("all"));
        return entityModel;
    }

    @Override
    public CollectionModel<EntityModel<Duty>> toCollectionModel(Iterable<? extends Duty> entities) {
        CollectionModel<EntityModel<Duty>> collectionModel =
                RepresentationModelAssembler.super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(DutyController.class).getAll()).withSelfRel());
        return collectionModel;
    }
}
