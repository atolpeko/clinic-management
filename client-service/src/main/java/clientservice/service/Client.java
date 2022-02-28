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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import java.util.Objects;

/**
 * Client domain class.
 */
@Entity
@Table(name = "client")
@JsonIgnoreProperties(value = "password", allowSetters = true)
public class Client {

    /**
     * An enumeration denoting client's sex.
     */
    public enum Sex { MALE, FEMALE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Email is mandatory")
    @Email
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password must be at least 8 characters long")
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

    @Column(nullable = false)
    @NotBlank(message = "Country is mandatory")
    private String country;

    @Column(nullable = false)
    @NotBlank(message = "City is mandatory")
    private String city;

    @Column(nullable = false)
    @NotBlank(message = "Street is mandatory")
    private String street;

    @Column(name = "house_number", nullable = false)
    @Positive(message = "House number must be positive")
    private int houseNumber;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled;

    /**
     * Constructs a new enabled client.
     */
    public Client() {
        isEnabled = true;
    }

    public Client(Client other) {
        id = other.id;
        email = other.email;
        password = other.password;
        name = other.name;
        sex = other.sex;
        phoneNumber = other.phoneNumber;
        country = other.country;
        city = other.city;
        street = other.street;
        houseNumber = other.houseNumber;
        isEnabled = other.isEnabled;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
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
                && Objects.equals(password, client.password)
                && Objects.equals(name, client.name)
                && sex == client.sex
                && Objects.equals(phoneNumber, client.phoneNumber)
                && Objects.equals(country, client.country)
                && Objects.equals(city, client.city)
                && Objects.equals(street, client.street)
                && houseNumber == client.houseNumber
                && isEnabled == client.isEnabled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, name, sex, phoneNumber,
                country, city, street, houseNumber, isEnabled);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber=" + houseNumber +
                ", isEnabled=" + isEnabled +
                '}';
    }
}
