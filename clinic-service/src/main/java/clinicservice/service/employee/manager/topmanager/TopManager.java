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

package clinicservice.service.employee.manager.topmanager;

import clinicservice.service.department.Department;
import clinicservice.service.employee.AbstractEmployee;
import clinicservice.service.employee.PersonalData;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("TOP_MANAGER")
public class TopManager extends AbstractEmployee {

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

    /**
     * Constructs a new TopManager with the specified email, password and personal data.
     *
     * @param email email to set
     * @param password password to set
     * @param data personal data to set
     */
    public TopManager(String email, String password, PersonalData data) {
        super(email, password, data, null);
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
}
