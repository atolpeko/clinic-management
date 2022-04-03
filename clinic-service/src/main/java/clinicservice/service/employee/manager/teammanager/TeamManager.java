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

package clinicservice.service.employee.manager.teammanager;

import clinicservice.service.department.Department;
import clinicservice.service.employee.AbstractEmployee;
import clinicservice.service.employee.PersonalData;
import clinicservice.service.employee.doctor.Doctor;

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "manager_team",
            joinColumns = @JoinColumn(name = "manager_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "employee_id", nullable = false))
    private Set<Doctor> team;

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

    /**
     * Constructs a new TeamManager with the specified email,
     * password, personal data, department and team.
     *
     * @param email email to set
     * @param password password to set
     * @param data personal data to set
     * @param department department to set
     * @param team team to set
     */
    public TeamManager(String email, String password, PersonalData data,
                       Department department, Set<Doctor> team) {
        super(email, password, data, department);
        this.team = team;
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
}
