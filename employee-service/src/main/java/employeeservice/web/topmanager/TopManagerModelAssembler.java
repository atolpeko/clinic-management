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

package employeeservice.web.topmanager;

import employeeservice.service.topmanager.TopManager;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TopManagerModelAssembler
        implements RepresentationModelAssembler<TopManager, EntityModel<TopManager>> {

    @Override
    public EntityModel<TopManager> toModel(TopManager entity) {
        long id = entity.getId();
        return EntityModel.of(entity,
                linkTo(methodOn(TopManagerController.class).getById(id)).withSelfRel(),
                linkTo(methodOn(TopManagerController.class).getAll()).withRel("all"));
    }

    @Override
    public CollectionModel<EntityModel<TopManager>> toCollectionModel(Iterable<? extends TopManager> entities) {
        CollectionModel<EntityModel<TopManager>> collectionModel =
                RepresentationModelAssembler.super.toCollectionModel(entities);
        collectionModel.add(linkTo(methodOn(TopManagerController.class).getAll()).withSelfRel());
        return collectionModel;
    }
}
