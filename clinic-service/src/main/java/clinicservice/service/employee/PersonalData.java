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

package clinicservice.service.employee;

import clinicservice.service.Address;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents employee's personal data.
 */
@Embeddable
public class PersonalData implements Serializable {

    /**
     * An enumeration denoting employee's sex.
     */
    public enum Sex { MALE, FEMALE, OTHER }

    @Column(nullable = false)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "Phone is mandatory")
    private String phone;

    @Column(name = "date_of_birth", nullable = false)
    @NotNull(message = "Date of birth is mandatory")
    private LocalDate dateOfBirth;

    @Embedded
    @NotNull(message = "Address is mandatory")
    @Valid
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Sex is mandatory")
    private Sex sex;

    @Column(name = "hire_date", nullable = false)
    @NotNull(message = "Hire date is mandatory")
    private LocalDate hireDate;

    @Column(nullable = false)
    @NotNull(message = "Salary is mandatory")
    @Positive(message = "Salary must be positive")
    private BigDecimal salary;

    public PersonalData() {
    }

    /**
     * Constructs a new PersonalData copying data from the passed one.
     *
     * @param other personal data to copy data from
     */
    public PersonalData(PersonalData other) {
        name = other.name;
        phone = other.phone;
        dateOfBirth = other.dateOfBirth;
        address = other.address;
        sex = other.sex;
        hireDate = other.hireDate;
        salary = other.salary;
    }

    /**
     * Constructs a new PersonalData with the specified name, phone, date of birth,
     * address, sex, hire date and salary.
     *
     * @param name name to set
     * @param phone phone to set
     * @param dateOfBirth date of birth to set
     * @param address address to set
     * @param sex sex to set
     * @param hireDate hire date to set
     * @param salary salary to set
     */
    public PersonalData(String name, String phone, LocalDate dateOfBirth,
                        Address address, Sex sex, LocalDate hireDate, BigDecimal salary) {
        this.name = name;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.sex = sex;
        this.hireDate = hireDate;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
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
                && Objects.equals(phone, that.phone)
                && Objects.equals(dateOfBirth, that.dateOfBirth)
                && Objects.equals(address, that.address)
                && Objects.equals(hireDate, that.hireDate)
                && Objects.equals(salary, that.salary)
                && sex == that.sex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone, dateOfBirth,
                address, sex, hireDate, salary);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", address=" + address +
                ", sex=" + sex +
                ", hireDate=" + hireDate +
                ", salary=" + salary +
                '}';
    }
}
