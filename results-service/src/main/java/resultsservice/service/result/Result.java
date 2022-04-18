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

package resultsservice.service.result;

import com.fasterxml.jackson.annotation.JsonProperty;

import resultsservice.service.external.client.Client;
import resultsservice.service.external.employee.Doctor;
import resultsservice.service.external.registration.Duty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import java.io.Serializable;
import java.util.Objects;

/**
 * Result domain class.
 */
@Entity
@Table(name = "result")
public class Result implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Data is mandatory")
    private String data;

    @Column(nullable = false)
    @NotNull(message = "Duty ID is mandatory")
    @Positive(message = "Duty ID must be positive")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long dutyId;

    @Column(nullable = false)
    @NotNull(message = "Client ID is mandatory")
    @Positive(message = "Client ID must be positive")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long clientId;

    @Column(nullable = false)
    @NotNull(message = "Doctor ID is mandatory")
    @Positive(message = "Doctor ID must be positive")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long doctorId;

    @Transient
    private Duty duty;

    @Transient
    private Client client;

    @Transient
    private Doctor doctor;

    public Result() {
    }

    /**
     * Constructs a new Result copying data from the passed one.
     *
     * @param other result to copy data from
     */
    public Result(Result other) {
        id = other.id;
        data = other.data;
        dutyId = other.dutyId;
        clientId = other.clientId;
        doctorId = other.doctorId;
        duty = (other.duty == null) ? null : new Duty(other.duty);
        client = (other.client == null) ? null : new Client(other.client);
        doctor = (other.doctor == null) ? null : new Doctor(other.doctor);
    }

    /**
     * Constructs a new Result with the specified data, duty ID, client ID and doctor ID.
     *
     * @param data data to set
     * @param dutyId duty ID to set
     * @param clientId client ID to set
     * @param doctorId doctor ID to set
     */
    public Result(String data, Long dutyId, Long clientId, Long doctorId) {
        this.data = data;
        this.dutyId = dutyId;
        this.clientId = clientId;
        this.doctorId = doctorId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getDutyId() {
        return dutyId;
    }

    public void setDutyId(Long dutyId) {
        this.dutyId = dutyId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Duty getDuty() {
        return duty;
    }

    public void setDuty(Duty duty) {
        this.duty = duty;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Result result = (Result) other;
        return Objects.equals(data, result.data)
                && Objects.equals(dutyId, result.dutyId)
                && Objects.equals(clientId, result.clientId)
                && Objects.equals(doctorId, result.doctorId)
                && Objects.equals(duty, result.duty)
                && Objects.equals(client, result.client)
                && Objects.equals(doctor, result.doctor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, dutyId, clientId, doctorId, doctor, client, duty);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", dutyId=" + dutyId +
                ", clientId=" + clientId +
                ", doctorId=" + doctorId +
                ", duty=" + duty +
                ", client=" + client +
                ", doctor=" + doctor +
                '}';
    }
}
