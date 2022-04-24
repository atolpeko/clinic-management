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

package registrationservice.service.external.client;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import java.io.Serializable;
import java.util.Objects;

/**
 * Client domain class.
 */
@Embeddable
public class Client implements Serializable {

    @Column(name = "client_id", nullable = false)
    private Long id;

    @Transient
    private String email;

    @Transient
    private String name;

    /**
     * @return Client builder
     */
    public static Builder builder() {
        return new Client().new Builder();
    }

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

    /**
     * Client object builder.
     */
    public class Builder {

        private Builder() {
        }

        public Client build() {
            return Client.this;
        }

        public Builder withId(Long id) {
            Client.this.id = id;
            return this;
        }

        public Builder withEmail(String email) {
            Client.this.email = email;
            return this;
        }

        public Builder withName(String name) {
            Client.this.name = name;
            return this;
        }
    }
}
