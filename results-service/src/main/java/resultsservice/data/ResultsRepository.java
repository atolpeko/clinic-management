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

package resultsservice.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import resultsservice.service.result.Result;

import java.util.List;

/**
 * A ResultsRepository abstracts a collection of Result objects.
 */
@Repository
public interface ResultsRepository extends JpaRepository<Result, Long> {

    /**
     * Retrieves all results with the specified client ID.
     *
     * @param clientId client ID of results to get
     *
     * @return all results with the specified client ID
     */
    List<Result> findAllByClientId(long clientId);
}
