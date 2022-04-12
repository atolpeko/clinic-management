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

package registrationservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import registrationservice.service.duty.DutyService;
import registrationservice.service.registration.RegistrationService;

import java.util.HashMap;
import java.util.Map;

@Component
public class ActuatorInfoContributor implements InfoContributor {
    private final DutyService dutyService;
    private final RegistrationService registrationService;

    @Autowired
    public ActuatorInfoContributor(DutyService dutyService,
                                   RegistrationService registrationService) {
        this.dutyService = dutyService;
        this.registrationService = registrationService;
    }

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> details = new HashMap<>();
        details.put("The number of services: ", dutyService.count());
        details.put("The number of registrations: ", registrationService.count());

        builder.withDetails(details);
    }
}
