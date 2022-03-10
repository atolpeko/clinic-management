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

package resultsservice.web;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.BasicLinkBuilder;
import org.springframework.stereotype.Component;

import resultsservice.service.result.Result;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Converts a Result domain class into a RepresentationModel.
 */
@Component
public class ResultModelAssembler implements RepresentationModelAssembler<Result, EntityModel<Result>> {

    @Override
    public EntityModel<Result> toModel(Result entity) {
        Link dutyLink = BasicLinkBuilder
                .linkToCurrentMapping()
                .slash("/services/" + entity.getDutyId())
                .withRel("service");
        Link doctorLink = BasicLinkBuilder
                .linkToCurrentMapping()
                .slash("/doctors/" + entity.getDoctorId())
                .withRel("doctor");
        Link clientLink = BasicLinkBuilder
                .linkToCurrentMapping()
                .slash("/clients/" + entity.getClientId())
                .withRel("client");

        EntityModel<Result> entityModel = EntityModel.of(entity);
        entityModel.add(dutyLink, doctorLink, clientLink);
        entityModel.add(linkTo(methodOn(ResultController.class).getById(entity.getId())).withSelfRel(),
                linkTo(methodOn(ResultController.class).getAll()).withRel("all"));
        return entityModel;
    }

    @Override
    public CollectionModel<EntityModel<Result>> toCollectionModel(Iterable<? extends Result> entities) {
        CollectionModel<EntityModel<Result>> collectionModel =
                RepresentationModelAssembler.super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(ResultController.class).getAll()).withSelfRel());
        return collectionModel;
    }
}
