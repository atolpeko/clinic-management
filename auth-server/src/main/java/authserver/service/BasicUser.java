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

package authserver.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Collection;
import java.util.List;

/**
 * Clinic client domain class.
 */
@Entity
@Table(name = "client", catalog = "client")
public class BasicUser extends AbstractUser {
    private static final GrantedAuthority authority = new SimpleGrantedAuthority(Role.USER.toString());

    /**
     * @return BasicUser builder
     */
    public static Builder builder() {
        return new BasicUser().new Builder();
    }

    public BasicUser() {
    }

    /**
     * Constructs a new BasicUser copying data from the passed one.
     *
     * @param other user to copy data from
     */
    public BasicUser(BasicUser other) {
        super(other);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(authority);
    }

    /**
     * BasicUser object builder.
     */
    public class Builder {

        private Builder() {
        }

        public BasicUser build() {
            return BasicUser.this;
        }

        public Builder withId(Long id) {
            BasicUser.this.setId(id);
            return this;
        }

        public Builder withLogin(String login) {
            BasicUser.this.setLogin(login);
            return this;
        }

        public Builder withPassword(String password) {
            BasicUser.this.setPassword(password);
            return this;
        }

        public Builder isEnabled(Boolean isEnabled) {
            BasicUser.this.setEnabled(isEnabled);
            return this;
        }
    }
}
