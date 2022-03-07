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

package clinicservice.service.doctor;

import clinicservice.service.department.Department;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Doctor domain class.
 */
@Entity
@Table(name = "doctor")
@JsonIgnoreProperties(value = "department", allowSetters = true)
public class Doctor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Column(nullable = false)
    @NotNull(message = "Specialty is mandatory")
    private String specialty;

    @Column(name = "practice_beginning_date", nullable = false)
    @NotNull(message = "Date of practice beginning is mandatory")
    private LocalDate practiceBeginningDate;

    @ManyToOne
    @NotNull(message = "Department is mandatory")
    private Department department;

    public Doctor() {
    }

    /**
     * Constructs a new Doctor copying data from the passed one.
     *
     * @param other doctor to copy data from
     */
    public Doctor(Doctor other) {
        id = other.id;
        name = other.name;
        specialty = other.specialty;
        practiceBeginningDate = LocalDate.from(other.practiceBeginningDate);
        department = other.department;
    }

    /**
     * Constructs a new Doctor with the specified name, specialty,
     * date of practice beginning and department.
     *
     * @param name name to set
     * @param specialty specialty to set
     * @param practiceBeginningDate date of practice beginning to set
     * @param department department to set
     */
    public Doctor(String name, String specialty,
                  LocalDate practiceBeginningDate, Department department) {
        this.name = name;
        this.specialty = specialty;
        this.practiceBeginningDate = practiceBeginningDate;
        this.department = department;
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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
        return Objects.equals(name, doctor.name)
                && Objects.equals(specialty, doctor.specialty)
                && Objects.equals(practiceBeginningDate, doctor.practiceBeginningDate);
    }

    @Override
    public int hashCode() {
        // Not using department field to avoid infinite recursion
        return Objects.hash(name, specialty, practiceBeginningDate);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", specialty='" + specialty + '\'' +
                ", practiceBeginningDate=" + practiceBeginningDate +
                ", department=" + department +
                '}';
    }
}
