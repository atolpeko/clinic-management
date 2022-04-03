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

package clinicservice.service.employee.doctor;

import clinicservice.service.department.Department;
import clinicservice.service.employee.AbstractEmployee;
import clinicservice.service.employee.PersonalData;

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

    /**
     * Constructs a new Doctor with the specified email, password, personal data, department,
     * specialty and date of practice beginning.
     *
     * @param email email to set
     * @param password password to set
     * @param data personal data to set
     * @param specialty specialty to set
     * @param department department to set
     * @param practiceBeginningDate date of practice beginning to set
     */
    public Doctor(String email, String password, PersonalData data, String specialty,
                  Department department, LocalDate practiceBeginningDate) {
        super(email, password, data, department);
        this.specialty = specialty;
        this.practiceBeginningDate = practiceBeginningDate;
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
}
