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

package registrationservice.service.duty;

import registrationservice.service.external.clinic.Doctor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Duty domain class.
 */
@Entity
@Table(name = "duty")
public class Duty implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Description is mandatory")
    private String description;

    @Column(name = "needs_specialty", nullable = false)
    @NotBlank(message = "Specialty is mandatory")
    private String neededSpecialty;

    @Column(nullable = false)
    @NotNull(message = "Price is mandatory")
    private BigDecimal price;

    @Transient
    private Collection<Doctor> doctors;

    public Duty() {
        doctors = new ArrayList<>();
    }

    /**
     * Constructs a new Duty copying data from the passed one.
     *
     * @param other duty to copy data from
     */
    public Duty(Duty other) {
        id = other.id;
        name = other.name;
        description = other.description;
        neededSpecialty = other.neededSpecialty;
        price = other.price;
        doctors = new ArrayList<>(other.doctors);
    }

    /**
     * Construct a new Duty with the specified name, description, specialty and price.
     *
     * @param name name to set
     * @param description description to set
     * @param needsSpecialty specialty to set
     * @param price price to set
     */
    public Duty(String name, String description, String needsSpecialty, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.neededSpecialty = needsSpecialty;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNeededSpecialty() {
        return neededSpecialty;
    }

    public void setNeededSpecialty(String needsSpecialty) {
        this.neededSpecialty = needsSpecialty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Collection<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(Collection<Doctor> doctors) {
        this.doctors = doctors;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Duty duty = (Duty) other;
        return Objects.equals(name, duty.getName())
                && Objects.equals(description, duty.description)
                && Objects.equals(neededSpecialty, duty.neededSpecialty)
                && Objects.equals(price, duty.price)
                && Objects.equals(doctors, duty.doctors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, neededSpecialty, price, doctors);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", needsSpecialty='" + neededSpecialty + '\'' +
                ", price=" + price +
                ", doctorIds=" + doctors +
                '}';
    }
}
