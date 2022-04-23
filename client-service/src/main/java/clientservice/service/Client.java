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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

/**
 * Client domain class.
 */
@Entity
@Table(name = "client")
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    @Embedded
    @JsonUnwrapped
    @Valid
    private PersonalData personalData;

    /**
     * @return Client builder
     */
    public static Builder builder() {
        return new Client().new Builder();
    }

    /**
     * Returns a Client builder with predefined fields copied from the specified client.
     *
     * @param data client to copy data from
     *
     * @return Client builder
     */
    public static Builder builder(Client data) {
        return new Client(data).new Builder();
    }

    /**
     * Constructs a new enabled client.
     */
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
        password = other.password;
        isEnabled = other.isEnabled;
        personalData = (other.personalData == null) ? null : other.personalData;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isEnabled() {
        return isEnabled;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public PersonalData getPersonalData() {
        return personalData;
    }

    public void setPersonalData(PersonalData personalData) {
        this.personalData = personalData;
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
        return Objects.equals(email, client.email)
                && Objects.equals(password, client.password)
                && Objects.equals(isEnabled, client.isEnabled)
                && Objects.equals(personalData, client.personalData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, isEnabled, personalData);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isEnabled=" + isEnabled +
                ", personalData=" + personalData +
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

        public Builder withPassword(String password) {
            Client.this.password = password;
            return this;
        }

        public Builder withPersonalData(PersonalData data) {
            Client.this.personalData = data;
            return this;
        }

        public Builder isEnabled(Boolean enabled) {
            Client.this.isEnabled = enabled;
            return this;
        }

        /**
         * Copies not null fields from the specified client.
         *
         * @param client client to copy data from
         *
         * @return this builder
         */
        public Builder copyNonNullFields(Client client) {
            if (client.id != null) {
                Client.this.id = client.id;
            }
            if (client.email != null) {
                Client.this.email = client.email;
            }
            if (client.password != null) {
                Client.this.password = client.password;
            }
            if (client.isEnabled != null) {
                Client.this.isEnabled = client.isEnabled;
            }
            if (client.personalData != null) {
                Client.this.personalData = PersonalData.builder(Client.this.personalData)
                        .copyNonNullFields(client.personalData)
                        .build();
            }

            return this;
        }
    }
}
