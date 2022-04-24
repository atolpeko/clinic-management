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

package clinicservice.service.department;

import clinicservice.service.facility.MedicalFacility;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Department domain class.
 */
@Entity
@Table(name = "department")
public class Department implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @NotNull(message = "Address is mandatory")
    @Valid
    private Address address;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "department_facility",
            joinColumns = @JoinColumn(name = "department_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "facility_id", nullable = false))
    @JsonIgnore
    private Set<MedicalFacility> facilities;

    /**
     * @return Department builder
     */
    public static Builder builder() {
        return new Department().new Builder();
    }

    /**
     * Returns a Department builder with predefined fields copied from the specified department.
     *
     * @param data department to copy data from
     *
     * @return Department builder
     */
    public static Builder builder(Department data) {
        return new Department(data).new Builder();
    }

    public Department() {
        facilities = new HashSet<>();
    }

    /**
     * Constructs a new Department copying data from the passed one.
     *
     * @param other department to copy data from
     */
    public Department(Department other) {
        id = other.id;
        address = (other.address == null) ? null : new Address(other.address);
        facilities = new HashSet<>(other.facilities);
    }

    /**
     * Adds the specified facility to the department.
     * Synchronized for bidirectional association.
     *
     * @param facility facility to add
     */
    public void addFacility(MedicalFacility facility) {
        facilities.add(facility);

        // Not using addDepartment() to avoid infinite recursion
        facility.getDepartments().add(this);
    }

    /**
     * Removes the specified facility from the department.
     * Synchronized for bidirectional association.
     *
     * @param facility facility to remove
     */
    public void removeFacility(MedicalFacility facility) {
        facilities.add(facility);

        // Not using removeDepartment() to avoid infinite recursion
        facility.getDepartments().remove(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<MedicalFacility> getFacilities() {
        return facilities;
    }

    public void setFacilities(Set<MedicalFacility> facilities) {
        this.facilities = facilities;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Department that = (Department) other;
        return Objects.equals(address, that.address)
                && Objects.equals(facilities, that.facilities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, facilities);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", address=" + address +
                ", facilities=" + facilities +
                '}';
    }

    /**
     * Department object builder.
     */
    public class Builder {

        private Builder() {
        }

        public Department build() {
            return Department.this;
        }

        public Builder withId(Long id) {
            Department.this.id = id;
            return this;
        }

        public Builder withAdress(Address address) {
            Department.this.address = address;
            return this;
        }

        public Builder withFacilities(Set<MedicalFacility> facilities) {
            Department.this.facilities = facilities;
            return this;
        }

        /**
         * Copies not null fields from the specified department.
         *
         * @param department department to copy data from
         *
         * @return this builder
         */
        public Builder copyNonNullFields(Department department) {
            if (department.id != null) {
                Department.this.id = department.id;
            }
            if (department.address != null) {
                Department.this.address = Address.builder(Department.this.address)
                        .copyNonNullFields(department.address)
                        .build();
            }
            if (!department.facilities.isEmpty()) {
                Department.this.facilities = new HashSet<>(department.facilities);
            }

            return this;
        }
    }
}
