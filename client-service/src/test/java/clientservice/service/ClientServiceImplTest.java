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

package clientservice.service;

import clientservice.data.ClientRepository;
import clientservice.service.exception.ClientsModificationException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.Validator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("category.UnitTest")
public class ClientServiceImplTest {
    private static ClientRepository repository;
    private static PasswordEncoder encoder;
    private static Validator validator;
    private static CircuitBreaker circuitBreaker;

    private static Client client;
    private static Client updatedClient;

    private ClientServiceImpl clientService;

    @BeforeAll
    public static void setUpMocks() {
        repository = mock(ClientRepository.class);
        validator = mock(Validator.class);

        encoder = mock(PasswordEncoder.class);
        when(encoder.encode(anyString())).then(returnsFirstArg());
        when(encoder.matches(anyString(), anyString())).then(invocation -> {
            String rawPassword = invocation.getArgument(0);
            String encodedPassword = invocation.getArgument(1);
            return rawPassword.equals(encodedPassword);
        });

        circuitBreaker = mock(CircuitBreaker.class);
        when(circuitBreaker.decorateSupplier(any())).then(returnsFirstArg());
        when(circuitBreaker.decorateRunnable(any())).then(returnsFirstArg());
    }

    @BeforeAll
    public static void createClient() {
        Address address = Address.builder()
                .withCountry("USA")
                .withState("NY")
                .withCity("NYC")
                .withStreet("23")
                .withHouseNumber(1)
                .build();

        PersonalData data = PersonalData.builder()
                .withAddress(address)
                .withName("Alexander")
                .withPhoneNumber("+375334558876")
                .withSex(PersonalData.Sex.MALE)
                .build();

        client = Client.builder()
                .withId(1L)
                .withEmail("alex@gmail.com")
                .withPassword("12345678")
                .withPersonalData(data)
                .isEnabled(true)
                .build();
    }

    @BeforeAll
    public static void createUpdatedClient() {
        Address address = Address.builder()
                .withCountry("USA")
                .withState("California")
                .withCity("LA")
                .withStreet("36")
                .withHouseNumber(10)
                .build();

        PersonalData data = PersonalData.builder()
                .withName("Alex")
                .withPhoneNumber("+375345567090")
                .withSex(PersonalData.Sex.MALE)
                .withAddress(address)
                .build();

        updatedClient = Client.builder()
                .withId(1L)
                .withEmail("alexander@gmail.com")
                .withPassword("87654321")
                .withPersonalData(data)
                .isEnabled(true)
                .build();
    }

    @BeforeEach
    public void beforeEach() {
        Mockito.reset(repository, validator);
        clientService = new ClientServiceImpl(repository, encoder, validator, circuitBreaker);
    }

    @Test
    public void shouldReturnClientByIdWhenContainsIt() {
        when(repository.findById(1L)).thenReturn(Optional.of(client));

        Client saved = clientService.findById(1).orElseThrow();
        assertThat(saved, is(equalTo(client)));
    }

    @Test
    public void shouldReturnClientByEmailWhenContainsIt() {
        when(repository.findByEmail(client.getEmail())).thenReturn(Optional.of(client));

        Client saved = clientService.findByEmail("alex@gmail.com").orElseThrow();
        assertThat(saved, is(equalTo(client)));
    }

    @Test
    public void shouldReturnListOfClientsWhenContainsMultipleClients() {
        List<Client> clients = List.of(client, client, client);
        when(repository.findAll()).thenReturn(clients);

        List<Client> saved = clientService.findAll();
        assertThat(saved, is(equalTo(clients)));
    }

    @Test
    public void shouldCount5ClientsWhenContains5Clients() {
        when(repository.count()).thenReturn(5L);

        long count = clientService.count();
        assertThat(count, is(equalTo(5L)));
    }

    @Test
    public void shouldRegisterClientWhenClientIsValid() {
        when(repository.save(any(Client.class))).thenReturn(client);
        when(validator.validate(any(Client.class))).thenReturn(Collections.emptySet());

        Client registered = clientService.save(client);
        assertThat(registered, equalTo(client));
    }

    @Test
    public void shouldThrowExceptionWhenClientIsInvalid() {
        when(validator.validate(any(Client.class))).thenThrow(ClientsModificationException.class);
        assertThrows(ClientsModificationException.class, () -> clientService.save(new Client()));
    }

    @Test
    public void shouldUpdateClientWhenClientIsValid() {
        when(repository.findById(1L)).thenReturn(Optional.of(client));
        when(repository.save(any(Client.class))).thenReturn(updatedClient);
        when(validator.validate(any(Client.class))).thenReturn(Collections.emptySet());

        Client updated = clientService.update(updatedClient);
        assertThat(updated, equalTo(updatedClient));
    }

    @Test
    public void shouldUpdateClientStatus() {
        Client toUpdate = new Client(client);
        when(repository.findById(1L)).thenReturn(Optional.of(client));
        when(repository.save(any(Client.class))).thenReturn(toUpdate);
        when(validator.validate(any(Client.class))).thenReturn(Collections.emptySet());

        Client updated = clientService.setEnabled(1, !client.isEnabled());
        assertThat(client.isEnabled(), not(equalTo(updated.isEnabled())));
    }

    @Test
    public void shouldNotContainClientWhenDeletesThisClient() {
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(client));
        doAnswer(invocation -> when(repository.findById(1L)).thenReturn(Optional.empty()))
                .when(repository).deleteById(1L);

        clientService.deleteById(1);

        Optional<Client> deleted = clientService.findById(1);
        assertThat(deleted, is(Optional.empty()));
    }
}
