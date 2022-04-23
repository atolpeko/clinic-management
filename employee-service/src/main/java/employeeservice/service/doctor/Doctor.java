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

package employeeservice.service.doctor;

import employeeservice.service.AbstractEmployee;
import employeeservice.service.PersonalData;
import employeeservice.service.external.Department;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Doctor domain class.
 */
@Entity
@DiscriminatorValue("DOCTOR")
public class Doctor extends AbstractEmployee {

    @NotBlank(message = "Specialty is mandatory")
    private String specialty;

    @Column(name = "practice_beginning_date")
    @NotNull(message = "Date of practice beginning is mandatory")
    private LocalDate practiceBeginningDate;

    /**
     * @return Doctor builder
     */
    public static Builder builder() {
        return new Doctor().new Builder();
    }

    /**
     * Returns a Doctor builder with predefined fields copied from the specified doctor.
     *
     * @param data doctor to copy data from
     *
     * @return Doctor builder
     */
    public static Builder builder(Doctor data) {
        return new Doctor(data).new Builder();
    }

    /**
     * Constructs a new enabled Doctor.
     */
    public Doctor() {
        super();
    }

    /**
     * Constructs a new Doctor copying data from the passed one.
     *
     * @param other doctor to copy data from
     */
    public Doctor(Doctor other) {
        super(other);
        specialty = other.specialty;
        practiceBeginningDate = LocalDate.from(other.practiceBeginningDate);
    }

    @Override
    @NotNull(message = "Department is mandatory")
    public Department getDepartment() {
        return super.getDepartment();
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public LocalDate getPracticeBeginningDate() {
        return practiceBeginningDate;
    }

    public void setPracticeBeginningDate(LocalDate practiceBeginningDate) {
        this.practiceBeginningDate = practiceBeginningDate;
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

        Doctor doctor = (Doctor) other;
        return Objects.equals(specialty, doctor.specialty)
                && Objects.equals(practiceBeginningDate, doctor.practiceBeginningDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), specialty, practiceBeginningDate);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "specialty='" + specialty + '\'' +
                ", practiceBeginningDate=" + practiceBeginningDate +
                '}';
    }

    /**
     * Doctor object builder
     */
    public class Builder extends AbstractEmployee.Builder {

        private Builder() {
        }

        @Override
        public Doctor build() {
            return Doctor.this;
        }

        @Override
        public Builder withId(Long id) {
            super.withId(id);
            return this;
        }

        @Override
        public Builder withEmail(String email) {
            super.withEmail(email);
            return this;
        }

        @Override
        public Builder withPassword(String password) {
            super.withPassword(password);
            return this;
        }

        @Override
        public Builder withPersonalData(PersonalData data) {
            super.withPersonalData(data);
            return this;
        }

        @Override
        public Builder withDepartment(Department department) {
            super.withDepartment(department);
            return this;
        }

        @Override
        public Builder copyNonNullFields(AbstractEmployee employee) {
            super.copyNonNullFields(employee);
            return this;
        }

        public Builder withSpecialty(String specialty) {
            Doctor.this.specialty = specialty;
            return this;
        }

        public Builder withPracticeBeginningDate(LocalDate date) {
            Doctor.this.practiceBeginningDate = date;
            return this;
        }

        /**
         * Copies not null fields from the specified doctor.
         *
         * @param doctor doctor to copy data from
         *
         * @return this builder
         */
        public Builder copyNonNullFields(Doctor doctor) {
            super.copyNonNullFields(doctor);
            if (doctor.specialty != null) {
                Doctor.this.specialty = doctor.specialty;
            }
            if (doctor.practiceBeginningDate != null) {
                Doctor.this.practiceBeginningDate = doctor.practiceBeginningDate;
            }

            return this;
        }
    }
}
