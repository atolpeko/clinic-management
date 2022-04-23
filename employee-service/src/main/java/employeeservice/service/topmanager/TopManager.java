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

package employeeservice.service.topmanager;

import com.fasterxml.jackson.annotation.JsonIgnore;

import employeeservice.service.AbstractEmployee;
import employeeservice.service.PersonalData;
import employeeservice.service.external.Department;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("TOP_MANAGER")
public class TopManager extends AbstractEmployee {

    /**
     * @return TopManager builder
     */
    public static Builder builder() {
        return new TopManager().new Builder();
    }

    /**
     * Returns a TopManager builder with predefined fields copied from the specified manager.
     *
     * @param data manager to copy data from
     *
     * @return TopManager builder
     */
    public static Builder builder(TopManager data) {
        return new TopManager(data).new Builder();
    }

    /**
     * Constructs a new enabled TopManager.
     */
    public TopManager() {
        super();
    }

    /**
     * Constructs a new TopManager copying data from the passed one.
     *
     * @param other manager to copy data from
     */
    public TopManager(TopManager other) {
        super(other);
    }

    @Override
    @JsonIgnore
    public Department getDepartment() {
        return null;
    }

    @Override
    @JsonIgnore
    public void setDepartment(Department department) {
        super.setDepartment(null);
    }

    /**
     * TopManager object builder
     */
    public class Builder extends AbstractEmployee.Builder {

        private Builder() {
        }

        @Override
        public TopManager build() {
            return TopManager.this;
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
    }
}
