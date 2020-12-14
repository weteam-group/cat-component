/*
 * Copyright (c) 2020, WeTeam Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.lakex.data.mongo.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;

/**
 * {@link MongoEntityInformation} implementation wrapping an existing {@link MongoEntityInformation} considering
 * {@link Persistable} types by delegating {@link #isNew(Object)} and {@link #getId(Object)} to the corresponding
 * {@link Persistable#isNew()} and {@link Persistable#getId()} implementations.
 *
 * @author Christoph Strobl
 * @author Oliver Gierke
 * @since 1.10
 */
@RequiredArgsConstructor
public class PersistableMongoEntityInformation<T, ID> implements MongoEntityInformation<T, ID> {
    private final @NonNull MongoEntityInformation<T, ID> delegate;

    @Override
    public String getCollectionName() {
        return delegate.getCollectionName();
    }

    @Override
    public String getIdAttribute() {
        return delegate.getIdAttribute();
    }

    @Override
    public Collation getCollation() {
        return delegate.getCollation();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isNew(T t) {

        if (t instanceof Persistable) {
            return ((Persistable<ID>) t).isNew();
        }

        return delegate.isNew(t);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ID getId(T t) {

        if (t instanceof Persistable) {
            return ((Persistable<ID>) t).getId();
        }

        return delegate.getId(t);
    }

    @Override
    public Class<ID> getIdType() {
        return delegate.getIdType();
    }

    @Override
    public Class<T> getJavaType() {
        return delegate.getJavaType();
    }
}
