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

import clinicservice.service.Address;
import clinicservice.service.employee.AbstractEmployee;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private Set<AbstractEmployee> employees;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "department_facility",
            joinColumns = @JoinColumn(name = "department_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "facility_id", nullable = false))
    @JsonIgnore
    private Set<MedicalFacility> facilities;

    public Department() {
        employees = new HashSet<>();
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
        employees = new HashSet<>(other.employees);
        facilities = new HashSet<>(other.facilities);
    }

    /**
     * Constructs a new Department with the specified address, medical facilities and employees.
     *
     * @param address address to set
     * @param facilities facilities to set
     * @param employees employees to set
     */
    public Department(Address address, Set<MedicalFacility> facilities,
                      Set<AbstractEmployee> employees) {
        this.address = address;
        this.employees = employees;
        this.facilities = facilities;
    }

    /**
     * Adds the specified employee to the department.
     * Synchronized for bidirectional association.
     *
     * @param employee employee to add
     */
    public void addEmployee(AbstractEmployee employee) {
        employees.add(employee);
        employee.setDepartment(this);
    }

    /**
     * Removes the specified employee from the department.
     * Synchronized for bidirectional association.
     *
     * @param employee employee to remove
     */
    public void removeEmployee(AbstractEmployee employee) {
        employees.remove(employee);
        employee.setDepartment(null);
    }

    /**
     * Adds the specified facility to the department.
     * Synchronized for bidirectional association.
     *
     * @param facility facility to add
     */
    public void addFacility(MedicalFacility facility) {
        facilities.add(facility);
        facility.getDepartments().add(this); // Not using addDepartment() to avoid infinite recursion
    }

    /**
     * Removes the specified facility from the department.
     * Synchronized for bidirectional association.
     *
     * @param facility facility to remove
     */
    public void removeFacility(MedicalFacility facility) {
        facilities.add(facility);
        facility.getDepartments().remove(null); // Not using removeDepartment() to avoid infinite recursion
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

    public Set<AbstractEmployee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<AbstractEmployee> employees) {
        this.employees = employees;
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
        return address.equals(that.address)
                && facilities.equals(that.facilities)
                && employees.equals(that.employees);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, facilities, employees);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", address=" + address +
                ", facilities=" + facilities +
                ", employees=" + employees +
                '}';
    }
}
