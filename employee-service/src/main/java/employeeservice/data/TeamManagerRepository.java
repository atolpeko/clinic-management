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

package employeeservice.data;

import employeeservice.service.teammanager.TeamManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * A TeamManagerRepository abstracts a collection of TeamManager objects.
 */
@Repository
public interface TeamManagerRepository extends JpaRepository<TeamManager, Long> {

    /**
     * Retrieves a manager by its email.
     *
     * @param email email of the manager to get
     *
     * @return the manager with the given id or Optional#empty() if none found.
     */
    Optional<TeamManager> findByEmail(String email);

    /**
     * Retrieves all managers with the specified department ID.
     *
     * @param departmentId department ID of the managers to get
     *
     * @return all managers with the given department ID
     */
    List<TeamManager> findAllByDepartmentId(long departmentId);

    // The default implementation does not work for an unknown reason
    @Override
    @Query("SELECT m FROM TeamManager m WHERE m.id = ?1")
    Optional<TeamManager> findById(Long id);

    // The default implementation does not work for an unknown reason
    @Override
    @Modifying
    @Query("DELETE FROM TeamManager m WHERE m.id = ?1")
    void deleteById(Long id);
}
