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
import registrationservice.service.external.client.Client;
import registrationservice.service.external.employee.Doctor;

import javax.persistence.Column;
import javax.persistence.Embedded;
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
@JsonIgnoreProperties(value = { "clientId", "doctorId" }, allowSetters = true)
public class Registration implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Duty duty;

    @Column(nullable = false)
    @NotNull(message = "Registration date is mandatory")
    private LocalDateTime date;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Embedded
    @NotNull(message = "Doctor is mandatory")
    private Doctor doctor;

    @Embedded
    @NotNull(message = "Client is mandatory")
    private Client client;

    /**
     * @return Registration builder
     */
    public static Builder builder() {
        return new Registration().new Builder();
    }

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
        date = LocalDateTime.from(other.date);
        isActive = other.isActive;
        doctor = (other.doctor == null) ? null : new Doctor(other.doctor);
        client = (other.client == null) ? null :new Client(other.client);
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

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean isActive) {
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
        return Objects.equals(isActive, that.isActive)
                && Objects.equals(duty, that.duty)
                && Objects.equals(date, that.date)
                && Objects.equals(doctor, that.doctor)
                && Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duty, date, isActive, doctor, client);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", duty=" + duty +
                ", date=" + date +
                ", isActive=" + isActive +
                ", doctor=" + doctor +
                ", client=" + client +
                '}';
    }

    /**
     * Registration object builder.
     */
    public class Builder {

        private Builder() {
        }

        public Registration build() {
            return Registration.this;
        }

        public Builder withId(Long id) {
            Registration.this.id = id;
            return this;
        }

        public Builder withDuty(Duty duty) {
            Registration.this.duty = duty;
            return this;
        }

        public Builder withDoctor(Doctor doctor) {
            Registration.this.doctor = doctor;
            return this;
        }

        public Builder withClient(Client client) {
            Registration.this.client = client;
            return this;
        }

        public Builder withDate(LocalDateTime date) {
            Registration.this.date = date;
            return this;
        }

        public Builder isActive(boolean isActive) {
            Registration.this.isActive = isActive;
            return this;
        }
    }
}
