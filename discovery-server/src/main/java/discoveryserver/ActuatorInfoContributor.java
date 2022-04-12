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

package discoveryserver;

import com.netflix.discovery.shared.Application;
import com.netflix.eureka.EurekaServerContextHolder;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ActuatorInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        List<Application> applications = EurekaServerContextHolder
                .getInstance()
                .getServerContext()
                .getRegistry()
                .getApplications()
                .getRegisteredApplications();

        Map<String, Object> details = new HashMap<>();
        if (applications.isEmpty()) {
            details.put("Services registered: ", 0);
        } else {
            applications.forEach(app ->
                    details.put(app.getName(), "Instances: " + app.getInstances().size()));
        }

        builder.withDetails(details);
    }
}
