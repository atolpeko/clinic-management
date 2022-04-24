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

import resultsservice.service.external.client.Client;
import resultsservice.service.external.employee.Doctor;
import resultsservice.service.external.registration.Duty;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

    @Embedded
    @NotNull(message = "Duty is mandatory")
    private Duty duty;

    @Embedded
    @NotNull(message = "Client is mandatory")
    private Client client;

    @Embedded
    @NotNull(message = "Doctor is mandatory")
    private Doctor doctor;

    /**
     * @return Result builder
     */
    public static Builder builder() {
        return new Result().new Builder();
    }

    /**
     * Returns a Result builder with predefined fields copied from the specified result.
     *
     * @param data result to copy data from
     *
     * @return Result builder
     */
    public static Builder builder(Result data) {
        return new Result(data).new Builder();
    }
    
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
        duty = (other.duty == null) ? null : new Duty(other.duty);
        client = (other.client == null) ? null : new Client(other.client);
        doctor = (other.doctor == null) ? null : new Doctor(other.doctor);
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
                && Objects.equals(duty, result.duty)
                && Objects.equals(client, result.client)
                && Objects.equals(doctor, result.doctor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, doctor, client, duty);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", duty=" + duty +
                ", client=" + client +
                ", doctor=" + doctor +
                '}';
    }

    /**
     * Result object builder.
     */
    public class Builder {

        private Builder() {
        }

        public Result build() {
            return Result.this;
        }

        public Builder withId(Long id) {
            Result.this.id = id;
            return this;
        }

        public Builder withData(String data) {
            Result.this.data = data;
            return this;
        }

        public Builder withDuty(Duty duty) {
            Result.this.duty = duty;
            return this;
        }

        public Builder withClient(Client client) {
            Result.this.client = client;
            return this;
        }

        public Builder withDoctor(Doctor doctor) {
            Result.this.doctor = doctor;
            return this;
        }

        /**
         * Copies not null fields from the specified result.
         *
         * @param result result to copy data from
         *
         * @return this builder
         */
        public Builder copyNonNullFields(Result result) {
            if (result.id != null) {
                Result.this.id = result.id;
            }
            if (result.data != null) {
                Result.this.data = result.data;
            }
            if (result.duty != null) {
                Result.this.duty = result.duty;
            }
            if (result.client != null) {
                Result.this.client = result.client;
            }
            if (result.doctor != null) {
                Result.this.doctor = result.doctor;
            }

            return this;
        }
    }
}
