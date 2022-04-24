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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents client's personal data.
 */
@Embeddable
public class PersonalData implements Serializable {

    /**
     * An enumeration denoting client's sex.
     */
    public enum Sex { MALE, FEMALE }

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

    /**
     * @return PersonalData builder
     */
    public static Builder builder() {
        return new PersonalData().new Builder();
    }

    /**
     * Returns a PersonalData builder with predefined fields copied from the specified personal data.
     *
     * @param data personal data to copy data from
     *
     * @return PersonalData builder
     */
    public static Builder builder(PersonalData data) {
        return new PersonalData(data).new Builder();
    }

    public PersonalData() {
    }

    /**
     * Constructs a new PersonalData copying data from the passed one.
     *
     * @param other personal data to copy data from
     */
    public PersonalData(PersonalData other) {
        name = other.name;
        sex = other.sex;
        phoneNumber = other.phoneNumber;
        address = (other.address == null) ? null : new Address(other.address);
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        PersonalData that = (PersonalData) other;
        return Objects.equals(name, that.name)
                && Objects.equals(phoneNumber, that.phoneNumber)
                && Objects.equals(address, that.address)
                && getSex() == that.getSex();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, sex, phoneNumber, address);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "name='" + name + '\'' +
                ", sex=" + sex +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address=" + address +
                '}';
    }

    /**
     * Personal data object builder.
     */
    public class Builder {

        private Builder() {
        }

        public PersonalData build() {
            return PersonalData.this;
        }

        public Builder withName(String name) {
            PersonalData.this.name = name;
            return this;
        }

        public Builder withSex(Sex sex) {
            PersonalData.this.sex = sex;
            return this;
        }

        public Builder withPhoneNumber(String phoneNumber) {
            PersonalData.this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder withAddress(Address address) {
            PersonalData.this.address = address;
            return this;
        }

        /**
         * Copies not null fields from the specified personal data.
         *
         * @param data personal data to copy data from
         *
         * @return this builder
         */
        public Builder copyNonNullFields(PersonalData data) {
            if (data.name != null) {
                PersonalData.this.name = data.name;
            }
            if (data.sex != null) {
                PersonalData.this.sex = data.sex;
            }
            if (data.phoneNumber != null) {
                PersonalData.this.phoneNumber = data.phoneNumber;
            }
            if (data.address != null) {
                PersonalData.this.address = Address.builder(PersonalData.this.address)
                                .copyNonNullFields(data.address)
                                .build();
            }

            return this;
        }
    }
}
