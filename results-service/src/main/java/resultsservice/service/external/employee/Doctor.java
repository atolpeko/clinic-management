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

package resultsservice.service.external.employee;

import java.io.Serializable;
import java.util.Objects;

/**
 * Doctor domain class.
 */
public class Doctor implements Serializable {
    private Long id;
    private String email;
    private String name;
    private String specialty;

    public Doctor() {
    }

    /**
     * Constructs a new Doctor copying data from the passed one.
     *
     * @param other doctor to copy data from
     */
    public Doctor(Doctor other) {
        id = other.id;
        email = other.email;
        name = other.name;
        specialty = other.specialty;
    }

    /**
     * Constructs a new Doctor with the specified id, email, name and specialty.
     *
     * @param id id to set
     * @param email email to set
     * @param name name to set
     * @param specialty specialty to set
     */
    public Doctor(Long id, String email, String name, String specialty) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.specialty = specialty;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Doctor doctor = (Doctor) other;
        return Objects.equals(id, doctor.id)
                && Objects.equals(email, doctor.email)
                && Objects.equals(name, doctor.name)
                && Objects.equals(specialty, doctor.specialty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, name, specialty);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", specialty='" + specialty + '\'' +
                '}';
    }
}
