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

package clientservice.web;

import clientservice.service.Client;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Converts a Client domain class into a RepresentationModel.
 */
@Component
public class ClientModelAssembler implements RepresentationModelAssembler<Client, EntityModel<Client>> {

    @Override
    public EntityModel<Client> toModel(Client entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(ClientController.class).getById(entity.getId())).withSelfRel(),
                linkTo(methodOn(ClientController.class).getAll()).withRel("all"));
    }

    @Override
    public CollectionModel<EntityModel<Client>> toCollectionModel(Iterable<? extends Client> entities) {
        CollectionModel<EntityModel<Client>> collectionModel =
                RepresentationModelAssembler.super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(ClientController.class).getAll()).withSelfRel());
        collectionModel.forEach(entityModel -> {
            long clientId = entityModel.getContent().getId();
            entityModel.add(linkTo(methodOn(ClientController.class).getById(clientId)).withSelfRel());
        });

        return collectionModel;
    }
}
