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

package registrationservice.service.registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import registrationservice.service.duty.Duty;
import registrationservice.service.external.Client;
import registrationservice.service.external.Doctor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Registration domain class.
 */
@Entity
@Table(name = "registration")
@JsonIgnoreProperties(value = { "duty", "doctor", "client" }, allowSetters = true)
public class Registration implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Duty duty;

    @Column(nullable = false)
    @NotNull(message = "Registration date is mandatory")
    private LocalDateTime date;

    @OneToOne
    @NotNull(message = "Doctor is mandatory")
    private Doctor doctor;

    @OneToOne
    @NotNull(message = "Client is mandatory")
    private Client client;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    /**
     * Constructs a new active Registration.
     */
    public Registration() {
        isActive = true;
    }

    /**
     * Constructs a new Registration copying data from the passed one.
     *
     * @param other registration to copy data from
     */
    public Registration(Registration other) {
        id = other.id;
        duty = other.duty;
        date = other.date;
        doctor = other.doctor;
        client = other.client;
        isActive = other.isActive;
    }

    /**
     * Construct a new Registration with the specified duty, date, client, doctor and status.
     *
     * @param duty duty to set
     * @param date date to set
     * @param client client to set
     * @param doctor doctor to set
     * @param isActive registration status to set
     */
    public Registration(Duty duty, Doctor doctor, LocalDateTime date,
                        Client client, boolean isActive) {
        this.duty = duty;
        this.date = date;
        this.doctor = doctor;
        this.client = client;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Duty getDuty() {
        return duty;
    }

    public void setDuty(Duty duty) {
        this.duty = duty;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Registration that = (Registration) other;
        return Objects.equals(duty, that.duty)
                && Objects.equals(date, that.date)
                && Objects.equals(doctor, that.doctor)
                && Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duty, date, doctor, client, isActive);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", duty=" + duty +
                ", date=" + date +
                ", doctor=" + doctor +
                ", client=" + client +
                ", isActive=" + isActive +
                '}';
    }
}
