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

import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.QuerydslMongoPredicateExecutor;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactoryBean;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.lang.Nullable;

import java.io.Serializable;

import static org.springframework.data.querydsl.QuerydslUtils.QUERY_DSL_PRESENT;

/**
 * Base Mongo Repository Factory Bean
 *
 * @author LarryKoo (larrykoo@126.com)
 * @slogon 站在巨人的肩膀上
 * @date 2020/11/13 15:01
 * @since 3.0.0
 */
public class BaseReactiveMongoRepositoryFactoryBean<T extends ReactiveMongoRepository<S, ID>, S, ID extends Serializable>
        extends ReactiveMongoRepositoryFactoryBean<T, S, ID> {

    public BaseReactiveMongoRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport getFactoryInstance(ReactiveMongoOperations operations) {
        return new BaseReactiveMongoRepositoryFactory(operations);
    }

    private static class BaseReactiveMongoRepositoryFactory<T, ID extends Serializable> extends ReactiveMongoRepositoryFactory {
        private static ReactiveMongoOperations operations;
        private final MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext;

        /**
         * Creates a new {@link ReactiveMongoRepositoryFactoryBean} for the given repository interface.
         *
         * @param operations ReactiveMongoOperations
         */
        public BaseReactiveMongoRepositoryFactory(ReactiveMongoOperations operations) {
            super(operations);
            this.mappingContext = operations.getConverter().getMappingContext();
        }

        /**
         * Must set ReactiveMongoTemplate as the ReactiveMongoOperations
         *
         * @param operations ReactiveMongoOperations
         */
        public static void setOperations(ReactiveMongoOperations operations) {
            BaseReactiveMongoRepositoryFactory.operations = operations;
        }

        private static boolean isQueryDslRepository(Class<?> repositoryInterface) {
            return QUERY_DSL_PRESENT && QuerydslPredicateExecutor.class.isAssignableFrom(repositoryInterface);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return BaseReactiveMongoRepositoryImpl.class;
        }

        /**
         * QueryDsl 支持
         *
         * @param metadata 元数据
         * @return RepositoryFragments
         */
        @Override
        protected RepositoryComposition.RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
            RepositoryComposition.RepositoryFragments fragments = RepositoryComposition.RepositoryFragments.empty();
            if (isQueryDslRepository(metadata.getRepositoryInterface())) {
                MongoEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType(), metadata);
                fragments = fragments.append(RepositoryFragment.implemented(
                        getTargetRepositoryViaReflection(QuerydslMongoPredicateExecutor.class, entityInformation, operations)));
            }

            return fragments;
        }

        /**
         * 获取 entity information and metadata
         *
         * @param domainClass Domain javaType
         * @param metadata    metadata 数据
         * @param <T>         Persistence object 泛型
         * @param <ID>        主键类型
         * @return {@link MongoEntityInformation}
         */
        private <T, ID> MongoEntityInformation<T, ID> getEntityInformation(Class<T> domainClass, @Nullable RepositoryMetadata metadata) {

            MongoPersistentEntity<?> entity = mappingContext.getRequiredPersistentEntity(domainClass);
            return MongoEntityInformationSupport.<T, ID>entityInformationFor(entity,
                    metadata != null ? metadata.getIdType() : null);
        }
    }


}
