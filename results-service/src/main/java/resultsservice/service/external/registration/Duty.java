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

package resultsservice.service.external.registration;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Duty domain class.
 */
public class Duty implements Serializable {
    private Long id;
    private String name;
    private BigDecimal price;

    public Duty() {
    }

    /**
     * Constructs a new Duty copying data from the passed one.
     *
     * @param other duty to copy data from
     */
    public Duty(Duty other) {
        id = other.id;
        name = other.name;
        price = other.price;
    }

    /**
     * Construct a new Duty with the specified id, name and price.
     *
     * @param id id to set
     * @param name name to set
     * @param price price to set
     */
    public Duty(Long id, String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Duty duty = (Duty) other;
        return Objects.equals(id, duty.id)
                && Objects.equals(name, duty.name)
                && Objects.equals(price, duty.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
