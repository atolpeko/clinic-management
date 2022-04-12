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

package resultsservice.web.result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import resultsservice.service.result.Result;
import resultsservice.service.result.ResultService;

import javax.validation.Valid;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/results", produces = "application/json")
@CrossOrigin(origins = "*")
public class ResultController {
    private final ResultService resultService;
    private final ResultModelAssembler modelAssembler;

    @Autowired
    public ResultController(ResultService resultService,
                            ResultModelAssembler modelAssembler) {
        this.resultService = resultService;
        this.modelAssembler = modelAssembler;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TOP_MANAGER')")
    public CollectionModel<EntityModel<Result>> getAll() {
        List<Result> results = resultService.findAll();
        return modelAssembler.toCollectionModel(results);
    }

    @GetMapping(params = "clientId")
    @PostAuthorize("@resultAccessHandler.canGetAnyByClientId(returnObject.content)")
    public CollectionModel<EntityModel<Result>> getAllByClientId(Long clientId) {
        List<Result> results = resultService.findAllByClientId(clientId);
        filter(results);
        return modelAssembler.toCollectionModel(results);
    }

    // Cannot use @PostFilter on CollectionModel :(
    private void filter(List<Result> results) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("USER"));
        boolean isDoctor = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("DOCTOR"));

        if (isUser) {
            results.removeIf(registration -> {
                String email = registration.getClient().getEmail();
                return !authentication.getName().equals(email);
            });
        } else if (isDoctor) {
            results.removeIf(registration -> {
                String email = registration.getDoctor().getEmail();
                return !authentication.getName().equals(email);
            });
        }
    }

    @GetMapping("/{id}")
    @PostAuthorize("@resultAccessHandler.canGet(returnObject.content)")
    public EntityModel<Result> getById(@PathVariable Long id) {
        Result result = resultService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Result not found: " + id));
        return modelAssembler.toModel(result);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@resultAccessHandler.canPost(#result)")
    public EntityModel<Result> save(@RequestBody @Valid Result result) {
        Result saved = resultService.save(result);
        return modelAssembler.toModel(saved);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    @PreAuthorize("@resultAccessHandler.canPatch(#id)")
    public EntityModel<Result> patchById(@PathVariable Long id,
                                         @RequestBody Result result) {
        result.setId(id);
        Result updated = resultService.update(result);
        return modelAssembler.toModel(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@resultAccessHandler.canDelete(#id)")
    public void deleteById(@PathVariable Long id) {
        resultService.deleteById(id);
    }
}
