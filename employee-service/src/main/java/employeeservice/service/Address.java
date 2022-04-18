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

package employeeservice.service;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents address.
 */
@Embeddable
public class Address implements Serializable {

    @Column(nullable = false)
    @NotBlank(message = "Country is mandatory")
    private String country;

    @Column(nullable = false)
    @NotBlank(message = "State is mandatory")
    private String state;

    @Column(nullable = false)
    @NotBlank(message = "City is mandatory")
    private String city;

    @Column(nullable = false)
    @NotBlank(message = "Street is mandatory")
    private String street;

    @Column(name = "house_number", nullable = false)
    @NotNull(message = "House number is mandatory")
    @Positive(message = "House number must be positive")
    private Integer houseNumber;

    public Address() {
    }

    /**
     * Constructs a new Address copying data from the passed one.
     *
     * @param other address to copy data from
     */
    public Address(Address other) {
        country = other.country;
        city = other.city;
        state = other.state;
        street = other.street;
        houseNumber = other.houseNumber;
    }

    /**
     * Constructs a new Address with the specified country, state, city, street and house number.
     *
     * @param country country to set
     * @param state state to set
     * @param city city to set
     * @param street street to set
     * @param houseNumber house number to set
     */
    public Address(String country, String state, String city, String street, Integer houseNumber) {
        this.country = country;
        this.state = state;
        this.city = city;
        this.street = street;
        this.houseNumber = houseNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public Integer getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(Integer houseNumber) {
        this.houseNumber = houseNumber;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Address address = (Address) other;
        return Objects.equals(country, address.country)
                && Objects.equals(state, address.state)
                && Objects.equals(city, address.city)
                && Objects.equals(street, address.street)
                && Objects.equals(houseNumber, address.houseNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, state, city, street, houseNumber);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber=" + houseNumber +
                '}';
    }
}
