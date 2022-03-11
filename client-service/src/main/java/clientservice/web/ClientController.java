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
import clientservice.service.ClientService;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/clients", produces = "application/json")
@CrossOrigin(origins = "*")
public class ClientController {
    private final ClientService clientService;
    private final ClientModelAssembler modelAssembler;

    @Autowired
    public ClientController(ClientService clientService,
                            ClientModelAssembler modelAssembler) {
        this.clientService = clientService;
        this.modelAssembler = modelAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Client>> getAll() {
        List<Client> clients = clientService.findAll();
        return modelAssembler.toCollectionModel(clients);
    }

    @GetMapping(params = "email")
    public EntityModel<Client> getByEmail(@RequestParam String email) {
        Client client = clientService.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Client not found: " + email));
        return modelAssembler.toModel(client);
    }

    @GetMapping("/{id}")
    public EntityModel<Client> getById(@PathVariable Long id) {
        Client client = clientService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Client not found: " + id));
        return modelAssembler.toModel(client);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Client> register(@RequestBody @Valid Client client) {
        Client saved = clientService.register(client);
        return modelAssembler.toModel(saved);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public EntityModel<Client> patchById(@PathVariable Long id,
                                         @RequestBody Client client) {
        client.setId(id);
        Client updated = clientService.update(client);
        return modelAssembler.toModel(updated);
    }

    @PatchMapping("/{id}/status")
    public EntityModel<Client> changeStatusById(@PathVariable Long id,
                                                @RequestParam boolean isActive) {
        Client updated = clientService.setEnabled(id, isActive);
        return modelAssembler.toModel(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        clientService.deleteById(id);
    }
}
