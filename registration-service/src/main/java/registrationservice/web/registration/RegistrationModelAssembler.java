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

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.BasicLinkBuilder;
import org.springframework.stereotype.Component;

import registrationservice.service.registration.Registration;
import registrationservice.web.duty.DutyController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Converts a Registration domain class into a RepresentationModel.
 */
@Component
public class RegistrationModelAssembler
        implements RepresentationModelAssembler<Registration, EntityModel<Registration>> {

    @Override
    public EntityModel<Registration> toModel(Registration entity) {
        Link doctorLink = BasicLinkBuilder
                .linkToCurrentMapping()
                .slash("/doctors/" + entity.getDoctor().getId())
                .withRel("doctor");
        Link clientLink = BasicLinkBuilder
                .linkToCurrentMapping()
                .slash("/clients/" + entity.getClient().getId())
                .withRel("client");

        EntityModel<Registration> entityModel = EntityModel.of(entity, doctorLink, clientLink);
        entityModel.add(linkTo(methodOn(DutyController.class).getById(entity.getId())).withRel("service"),
                linkTo(methodOn(RegistrationController.class).getById(entity.getId())).withSelfRel(),
                linkTo(methodOn(RegistrationController.class).getAll()).withRel("all"));
        return entityModel;
    }

    @Override
    public CollectionModel<EntityModel<Registration>>
        toCollectionModel(Iterable<? extends Registration> entities) {
        CollectionModel<EntityModel<Registration>> collectionModel =
                RepresentationModelAssembler.super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(RegistrationController.class).getAll()).withSelfRel());
        return collectionModel;
    }
}
