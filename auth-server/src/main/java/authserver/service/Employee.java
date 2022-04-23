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

package authserver.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import javax.persistence.Table;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Employee domain class.
 */
@Entity
@Table(name = "employee", catalog = "clinic")
public class Employee extends AbstractUser {

    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * @return Employee builder
     */
    public static Builder builder() {
        return new Employee().new Builder();
    }

    public Employee() {
    }

    /**
     * Constructs a new Employee copying data from the passed one.
     *
     * @param other employee to copy data from
     */
    public Employee(Employee other) {
        super(other);
        role = other.role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        GrantedAuthority authority = new SimpleGrantedAuthority(role.toString());
        return List.of(authority);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        if (!super.equals(other)) {
            return false;
        }

        Employee employee = (Employee) other;
        return role == employee.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), role);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "role=" + role +
                '}';
    }

    /**
     * Employee object builder.
     */
    public class Builder {

        private Builder() {
        }

        public Employee build() {
            return Employee.this;
        }

        public Builder withId(Long id) {
            Employee.this.setId(id);
            return this;
        }

        public Builder withLogin(String login) {
            Employee.this.setLogin(login);
            return this;
        }

        public Builder withPassword(String password) {
            Employee.this.setPassword(password);
            return this;
        }

        public Builder withRole(Role role) {
            Employee.this.role = role;
            return this;
        }

        public Builder isEnabled(Boolean isEnabled) {
            Employee.this.setEnabled(isEnabled);
            return this;
        }
    }
}
