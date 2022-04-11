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

package adminserver.config;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;

import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;

public class BearerAuthHeaderProvider implements HttpHeadersProvider {
    private final OAuth2RestTemplate template;

    public BearerAuthHeaderProvider(OAuth2RestTemplate template) {
        this.template = template;
    }

    public HttpHeaders getHeaders(Instance ignored) {
        HttpHeaders headers = new HttpHeaders();
        String token = template.getAccessToken().getTokenType() + " "
                + template.getAccessToken().getValue();
        headers.set("Authorization", token);
        return headers;
    }
}
