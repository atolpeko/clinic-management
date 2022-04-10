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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;
import java.util.Objects;

/**
 * Client domain class.
 */
@Entity
@Table(name = "client")
public class Client implements Serializable {

    /**
     * An enumeration denoting client's sex.
     */
    public enum Sex { MALE, FEMALE }

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

    @Column(nullable = false)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @NotNull(message = "Sex is mandatory")
    private Sex sex;

    @Column(name = "phone_number", nullable = false)
    @NotBlank(message = "Phone number is mandatory")
    private String phoneNumber;

    @Embedded
    @NotNull(message = "Address in mandatory")
    @Valid
    private Address address;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

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
        name = other.name;
        sex = other.sex;
        phoneNumber = other.phoneNumber;
        address = new Address(other.address);
        isEnabled = other.isEnabled;
    }

    /**
     * Constructs a new enabled Client with the specified email, password,
     * name, sex, phone number and address.
     *
     * @param email email to set
     * @param password password to set
     * @param name name to set
     * @param sex sex to set
     * @param phoneNumber phone number to set
     * @param address address to set
     */
    public Client(String email, String password, String name, Sex sex,
                  String phoneNumber, Address address) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
        this.address = address;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
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
                && Objects.equals(name, client.name)
                && sex == client.sex
                && Objects.equals(phoneNumber, client.phoneNumber)
                && Objects.equals(address, client.address)
                && Objects.equals(isEnabled, client.isEnabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, name, sex, phoneNumber, address, isEnabled);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", address=" + address +
                ", phoneNumber='" + phoneNumber  +
                '}';
    }
}
