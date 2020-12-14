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

import com.mongodb.BasicDBObject;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.NonNull;
import org.bson.Document;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A implement of ReactiveCustomMongoRepository
 *
 * @author LarryKoo (larrykoo@126.com)
 * @slogon 站在巨人的肩膀上
 * @date 2020/11/12 15:55
 * @since 3.0.0
 */
public class CatReactiveMongoRepositoryImpl<T, ID extends Serializable> extends SimpleReactiveMongoRepository<T, ID>
        implements CatReactiveMongoRepository<T, ID> {

    protected final ReactiveMongoOperations operations;
    protected final MongoEntityInformation<T, ID> information;

    public CatReactiveMongoRepositoryImpl(@NonNull MongoEntityInformation<T, ID> metadata,
                                          @NonNull ReactiveMongoOperations operations) {
        super(metadata, operations);
        information = metadata;
        this.operations = operations;
    }

    private Class<T> getEntityClass() {
        return information.getJavaType();
    }

    private String getCollectionName() {
        return operations.getCollectionName(getEntityClass());
    }

    protected Mono<MongoCollection<Document>> getDbCollection() {
        return operations.getCollection(getCollectionName());
    }

    /**
     * Implement of custom repository
     */

    @Override
    public BulkOperations initializeOrderedBulkOp() {
        return null;
    }

    @Override
    public BulkOperations initializeUnorderedBulkOp() {
        return null;
    }

    @Override
    public BasicDBObject generateFieldsObj(String... fields) {
        BasicDBObject fieldsObject = new BasicDBObject();
        for (String field : fields) {
            fieldsObject.put(field, 1);
        }
        return fieldsObject;
    }

    @Override
    public BasicQuery onlyIdQuery(Query query) {
        return new BasicQuery(query.getQueryObject(), new Document("id", 1));
    }

    @Override
    public Update buildBaseUpdate(T t, String... exclude) {
        Update update = new Update();
        List<String> excludeList = Arrays.asList(exclude);
        Field[] fields = t.getClass().getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod) || Modifier.isTransient(mod)) {
                continue;
            }
            if (excludeList.contains(field.getName())) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object value = field.get(t);
                if (value != null) {
                    update.set(field.getName(), value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return update;
    }

    @Override
    public Mono<UpdateResult> upsert(Query query, Update update) {
        return operations.upsert(query, update, getEntityClass());
    }

    @Override
    public Mono<Long> count(Query query) {
        return operations.count(query, getEntityClass());
    }

    @Override
    public Mono<Boolean> exists(Query query) {
        return operations.exists(query, getEntityClass());
    }

    @Override
    public Mono<T> findOne(Query query) {
        return operations.findOne(query, getEntityClass());
    }

    @Override
    public Flux<T> findAll(Query query) {
        return operations.find(query, getEntityClass());
    }

    @Override
    public Flux<String> findAllId(Query query) {
        return Flux.push(emitter -> {
            operations.find(onlyIdQuery(query), getEntityClass()).doOnNext(t -> emitter.next((String) t));
            emitter.complete();
        });
    }

    @Override
    public Mono<UpdateResult> updateFirst(Query query, Update update) {
        return operations.updateFirst(query, update, getEntityClass());
    }

    @Override
    public Mono<UpdateResult> updateMulti(Query query, Update update) {
        return operations.updateMulti(query, update, getEntityClass());
    }

    @Override
    public Long remove(Query query) {
        AtomicReference<Long> count = new AtomicReference<>(0L);
        operations.remove(query, getEntityClass()).doOnSuccess(result -> count.set(result.getDeletedCount()));
        return count.get();
    }

    @Override
    public Mono<T> findAndModify(Query query, Update update) {
        return operations.findAndModify(query, update, getEntityClass());
    }

    @Override
    public Mono<T> findAndRemove(Query query) {
        return operations.findAndRemove(query, getEntityClass());
    }

    @Override
    public Flux<T> findAllAndRemove(Query query) {
        return operations.findAllAndRemove(query, getEntityClass());
    }

    @Override
    public Flux<?> aggregate(Aggregation aggregation, Class<T> inputType, Class<?> outputType) {
        return operations.aggregate(aggregation, inputType, outputType);
    }

    @Override
    public Flux<?> aggregate(TypedAggregation<?> aggregation, Class<?> outputType) {
        return operations.aggregate(aggregation, outputType);
    }
}
