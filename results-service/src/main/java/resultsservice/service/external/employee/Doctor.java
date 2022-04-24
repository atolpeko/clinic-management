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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;

/**
 * Doctor domain class.
 */
@Embeddable
public class Doctor implements Serializable {

    @Column(name = "doctor_id", nullable = false)
    private Long id;

    @Transient
    private String email;

    @Transient
    private String name;

    @Transient
    private String specialty;

    /**
     * @return Doctor builder
     */
    public static Builder builder() {
        return new Doctor().new Builder();
    }

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

    /**
     * Doctor object builder.
     */
    public class Builder {

        private Builder() {
        }

        public Doctor build() {
            return Doctor.this;
        }

        public Builder withId(Long id) {
            Doctor.this.id = id;
            return this;
        }

        public Builder withEmail(String email) {
            Doctor.this.email = email;
            return this;
        }

        public Builder withName(String name) {
            Doctor.this.name = name;
            return this;
        }

        public Builder withSpecialty(String specialty) {
            Doctor.this.specialty = specialty;
            return this;
        }
    }
}
