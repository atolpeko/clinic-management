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

package resultsservice.service.external.client;

import java.io.Serializable;
import java.util.Objects;

/**
 * Client domain class.
 */
public class Client implements Serializable {
    private Long id;
    private String email;
    private String name;

    public Client() {
    }

    /**
     * Constructs a new Client copying data from the passed one.
     *
     * @param other client to copy data from
     */
    public Client(Client other) {
        id = other.id;
        email = other.email;
        name = other.name;
    }

    /**
     * Constructs a new Client with the specified id, email and name.
     *
     * @param id id to set
     * @param email email to set
     * @param name name to set
     */
    public Client(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Client client = (Client) other;
        return Objects.equals(id, client.id)
                && Objects.equals(email, client.email)
                && Objects.equals(name, client.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
