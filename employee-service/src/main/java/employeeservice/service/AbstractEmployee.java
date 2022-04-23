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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import employeeservice.service.external.Department;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Objects;

/**
 * Employee base class.
 */
@Entity(name = "employee")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractEmployee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password is mandatory")
    @Length(min = 8, message = "Password must be at least 8 characters long")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Embedded
    @JsonUnwrapped
    @Valid
    private PersonalData personalData;

    @Embedded
    private Department department;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    /**
     * Constructs a new enabled AbstractEmployee.
     */
    public AbstractEmployee() {
    }

    /**
     * Constructs a new AbstractEmployee copying data from the passed one.
     *
     * @param other employee to copy data from
     */
    public AbstractEmployee(AbstractEmployee other) {
        id = other.id;
        email = other.email;
        password = other.password;
        personalData = other.personalData;
        department = other.department;
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

    public PersonalData getPersonalData() {
        return personalData;
    }

    public void setPersonalData(PersonalData personalData) {
        this.personalData = personalData;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
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

        AbstractEmployee that = (AbstractEmployee) other;
        return Objects.equals(email, that.email)
                && Objects.equals(password, that.password)
                && Objects.equals(personalData, that.personalData)
                && Objects.equals(isEnabled, that.isEnabled);
        // Not using department field to avoid infinite recursion
    }

    @Override
    public int hashCode() {
        // Not using department field to avoid infinite recursion
        return Objects.hash(email, password, personalData, isEnabled);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", personalData=" + personalData +
                ", department=" + department +
                ", isEnabled=" + isEnabled +
                '}';
    }

    /**
     * Employee object base builder.
     */
    protected abstract class Builder {

        protected Builder() {
        }

        public abstract AbstractEmployee build();

        public Builder withId(Long id) {
            AbstractEmployee.this.id = id;
            return this;
        }

        public Builder withEmail(String email) {
            AbstractEmployee.this.email = email;
            return this;
        }

        public Builder withPassword(String password) {
            AbstractEmployee.this.password = password;
            return this;
        }

        public Builder withPersonalData(PersonalData data) {
            AbstractEmployee.this.personalData = data;
            return this;
        }

        public Builder withDepartment(Department department) {
            AbstractEmployee.this.setDepartment(department);
            return this;
        }

        /**
         * Copies not null fields from the specified employee.
         *
         * @param employee employee to copy data from
         *
         * @return this builder
         */
        public Builder copyNonNullFields(AbstractEmployee employee) {
            if (employee.id != null) {
                AbstractEmployee.this.id = employee.getId();
            }
            if (employee.email != null) {
                AbstractEmployee.this.email = employee.getEmail();
            }
            if (employee.password != null) {
                AbstractEmployee.this.password = employee.getPassword();
            }
            if (employee.department != null) {
                AbstractEmployee.this.department = employee.getDepartment();
            }
            if (employee.personalData != null) {
                AbstractEmployee.this.personalData = PersonalData.builder(AbstractEmployee.this.personalData)
                        .copyNonNullFields(employee.personalData)
                        .build();
            }

            return this;
        }
    }
}
