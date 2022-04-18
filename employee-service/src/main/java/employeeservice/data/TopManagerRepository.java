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

import employeeservice.service.topmanager.TopManager;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * A TopManagerRepository abstracts a collection of TopManager objects.
 */
@Repository
public interface TopManagerRepository extends JpaRepository<TopManager, Long> {

    /**
     * Retrieves a manager by its email.
     *
     * @param email email of the manager to get
     *
     * @return the manager with the given id or Optional#empty() if none found.
     */
    Optional<TopManager> findByEmail(String email);

    // The default implementation does not work for an unknown reason
    @Override
    @Query("SELECT m FROM TopManager m WHERE m.id = ?1")
    Optional<TopManager> findById(Long id);

    // The default implementation does not work for an unknown reason
    @Override
    @Modifying
    @Query("DELETE FROM TopManager m WHERE m.id = ?1")
    void deleteById(Long id);
}
