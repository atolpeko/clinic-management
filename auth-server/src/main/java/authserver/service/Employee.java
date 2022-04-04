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

import javax.persistence.Column;
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
    @Column(nullable = false)
    private Role role;

    public Employee() {
    }

    /**
     * Constructs a new Employee with the specified login, password and role.
     *
     * @param login login to set
     * @param password password to set
     * @param role role to set
     */
    public Employee(String login, String password, Role role) {
        super(login, password);
        this.role = role;
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
}
