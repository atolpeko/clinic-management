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
        EntityModel<Registration> entityModel = EntityModel.of(entity);
        long clientId = entity.getClient().getId();
        Link clientLink = Link.of("clients/" + clientId).withRel("client");

        long doctorId = entity.getDoctor().getId();
        Link doctorLink = Link.of("doctors/" + doctorId).withRel("doctor");

        long dutyId = entity.getDuty().getId();
        Link dutyLink = linkTo(methodOn(DutyController.class).getById(dutyId)).withRel("service");

        entityModel.add(clientLink, doctorLink, dutyLink);
        entityModel.add(linkTo(methodOn(RegistrationController.class).getAll()).withRel("all"));
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
