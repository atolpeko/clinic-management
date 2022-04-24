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

package clinicservice.service.facility;

import clinicservice.service.department.Department;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Medical facility domain class.
 */
@Entity
@Table(name = "facility")
public class MedicalFacility implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @ManyToMany(mappedBy = "facilities", fetch = FetchType.EAGER)
    @NotEmpty(message = "Departments are mandatory")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Department> departments;

    /**
     * @return MedicalFacility builder
     */
    public static Builder builder() {
        return new MedicalFacility().new Builder();
    }

    /**
     * Returns a MedicalFacility builder with predefined fields copied from the specified facility.
     *
     * @param data facility to copy data from
     *
     * @return MedicalFacility builder
     */
    public static Builder builder(MedicalFacility data) {
        return new MedicalFacility(data).new Builder();
    }

    public MedicalFacility() {
        departments = new HashSet<>();
    }

    /**
     * Constructs a new MedicalFacility copying data from the passed one.
     *
     * @param other facility to copy data from
     */
    public MedicalFacility(MedicalFacility other) {
        id = other.id;
        name = other.name;
        departments = other.departments;
    }
    
    /**
     * Adds the specified department to the list of departments.
     * Synchronized for bidirectional association.
     *
     * @param department department to add
     */
    public void addDepartment(Department department) {
        departments.add(department);
        department.getFacilities().add(this); // Not using addFacility() to avoid infinite recursion
    }

    /**
     * Removes the specified department from the list of departments.
     * Synchronized for bidirectional association.
     *
     * @param department department to remove
     */
    public void removeDepartment(Department department) {
        departments.remove(department);
        department.getFacilities().remove(this); // Not using removeFacility() to avoid infinite recursion
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

    public Set<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<Department> departments) {
        this.departments = departments;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        MedicalFacility that = (MedicalFacility) other;
        return Objects.equals(name, that.name);
               // Not using departments field to avoid infinite recursion
    }

    @Override
    public int hashCode() {
        // Not using departments field to avoid infinite recursion
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", departments=" + departments +
                '}';
    }

    /**
     * MedicalFacility object builder
     */
    public class Builder {

        private Builder() {
        }

        public MedicalFacility build() {
            return MedicalFacility.this;
        }

        public Builder withId(Long id) {
            MedicalFacility.this.id = id;
            return this;
        }

        public Builder withName(String name) {
            MedicalFacility.this.name = name;
            return this;
        }

        public Builder withDepartments(Set<Department> departments) {
            MedicalFacility.this.departments = departments;
            return this;
        }

        /**
         * Copies not null fields from the specified facility.
         *
         * @param facility facility to copy data from
         *
         * @return this builder
         */
        public Builder copyNonNullFields(MedicalFacility facility) {
            if (facility.id != null) {
                MedicalFacility.this.id = facility.id;
            }
            if (facility.name != null) {
                MedicalFacility.this.name = facility.name;
            }
            if (!facility.departments.isEmpty()) {
                MedicalFacility.this.departments = new HashSet<>(facility.departments);
            }

            return this;
        }
    }
}
