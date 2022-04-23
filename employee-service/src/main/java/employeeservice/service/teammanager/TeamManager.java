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

package employeeservice.service.teammanager;

import employeeservice.service.AbstractEmployee;
import employeeservice.service.PersonalData;
import employeeservice.service.doctor.Doctor;
import employeeservice.service.external.Department;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Team manager domain class.
 */
@Entity
@DiscriminatorValue("TEAM_MANAGER")
public class TeamManager extends AbstractEmployee {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "manager_team",
            joinColumns = @JoinColumn(name = "manager_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "employee_id", nullable = false))
    private Set<Doctor> team;

    /**
     * @return TeamManager builder
     */
    public static Builder builder() {
        return new TeamManager().new Builder();
    }

    /**
     * Returns a TeamManager builder with predefined fields copied from the specified manager.
     *
     * @param data manager to copy data from
     *
     * @return TeamManager builder
     */
    public static Builder builder(TeamManager data) {
        return new TeamManager(data).new Builder();
    }
    /**
     * Constructs a new enabled TeamManager.
     */
    public TeamManager() {
        super();
        team = new HashSet<>();
    }

    /**
     * Constructs a new TeamManager copying data from the passed one.
     *
     * @param other manager to copy data from
     */
    public TeamManager(TeamManager other) {
        super(other);
        team = new HashSet<>(other.team);
    }

    @Override
    @NotNull(message = "Department is mandatory")
    public Department getDepartment() {
        return super.getDepartment();
    }

    public Set<Doctor> getTeam() {
        return team;
    }

    public void setTeam(Set<Doctor> team) {
        this.team = team;
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

        TeamManager manager = (TeamManager) other;
        return Objects.equals(team, manager.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), team);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "team=" + team +
                '}';
    }

    /**
     * TeamManager object builder
     */
    public class Builder extends AbstractEmployee.Builder {

        private Builder() {
        }

        @Override
        public TeamManager build() {
            return TeamManager.this;
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
        
        public Builder withTeam(Set<Doctor> team) {
            TeamManager.this.team = team;
            return this;
        }

        /**
         * Copies not null fields from the specified manager.
         *
         * @param manager manager to copy data from
         *
         * @return this builder
         */
        public Builder copyNonNullFields(TeamManager manager) {
            super.copyNonNullFields(manager);
            if (!manager.team.isEmpty()) {
                TeamManager.this.team = new HashSet<>(manager.team);
            }

            return this;
        }
    }
}
