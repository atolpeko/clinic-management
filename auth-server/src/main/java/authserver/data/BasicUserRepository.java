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

package authserver.data;

import authserver.service.BasicUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * BasicUserRepository abstracts a collection of BasicUser object.
 */
@Repository
public interface BasicUserRepository extends JpaRepository<BasicUser, Long> {

    /**
     * Retrieves the user with the specified login.
     *
     * @param login login of user to get
     *
     * @return user with the specified login or Optional#empty() if none found
     */
    Optional<BasicUser> findByLogin(String login);
}
