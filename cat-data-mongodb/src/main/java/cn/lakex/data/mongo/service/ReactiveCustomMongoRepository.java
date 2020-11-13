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
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * Base repository for mongodb
 *
 * @author LarryKoo (larrykoo@126.com)
 * @slogon 站在巨人的肩膀上
 * @date 2020/11/12 14:16
 * @since 3.0.0
 */
public interface ReactiveCustomMongoRepository<T, ID extends Serializable> extends ReactiveSortingRepository<T, ID> {
    /**
     * 有序执行，若Bulk中的某个操作写入失败，不再执行后面的操作
     *
     * @return {@link BulkOperations}
     */
    BulkOperations initializeOrderedBulkOp();

    /**
     * 无序执行，若Bulk中的某个操作写入失败，不影响其它的操作
     *
     * @return {@link BulkOperations}
     */
    BulkOperations initializeUnorderedBulkOp();

    /**
     * 生成自定义的返回字段的查询对象
     *
     * @param fields 自定义返回字段
     * @return {@link BasicDBObject}
     */
    BasicDBObject generateFieldsObj(String... fields);

    /**
     * 生成仅返回 ID 的 Query 对象
     *
     * @param query 条件构造器
     * @return BasicQuery
     */
    BasicQuery onlyIdQuery(Query query);

    /**
     * Build 更新对象
     *
     * @param t       T
     * @param exclude 需要排除更新的属性
     * @return {@link Update}
     */
    Update buildBaseUpdate(T t, String... exclude);

    /**
     * 条件更新对象
     *
     * @param query  条件
     * @param update Update 更新对象
     * @return UpdateResult
     */
    Mono<UpdateResult> upsert(Query query, Update update);

    /**
     * Count
     *
     * @param query 条件
     * @return Long
     */
    Mono<Long> count(Query query);

    /**
     * 条件存在查询
     *
     * @param query 条件
     * @return Boolean
     */
    Mono<Boolean> exists(Query query);

    /**
     * 条件查询Top-1记录
     *
     * @param query 条件
     * @return T
     */
    Mono<T> findOne(Query query);

    /**
     * 条件查询集合
     *
     * @param query 条件
     * @return Array
     */
    Flux<T> findAll(Query query);

    /**
     * 条件查询集合，只返回 Id
     *
     * @param query 条件
     * @return Array
     */
    Flux<String> findAllId(Query query);

    /**
     * 带条件更新Top1 记录
     *
     * @param query  条件
     * @param update 更新对象
     * @return UpdateResult
     */
    Mono<UpdateResult> updateFirst(Query query, Update update);

    /**
     * 带条件批量更新
     *
     * @param query  条件
     * @param update 更新对象
     * @return UpdateResult
     */
    Mono<UpdateResult> updateMulti(Query query, Update update);

    /**
     * 条件删除
     *
     * @param query 条件
     * @return Integer
     */
    Long remove(Query query);

    /**
     * 更新并返回查询结果
     *
     * @param query  条件
     * @param update 更新对象
     * @return T
     */
    Mono<T> findAndModify(Query query, Update update);

    /**
     * 条件删除，并返回结果
     *
     * @param query 条件
     * @return T
     */
    Mono<T> findAndRemove(Query query);

    /**
     * 条件删除并返回查询批量结果
     *
     * @param query
     * @return
     */
    Flux<T> findAllAndRemove(Query query);

    /**
     * 聚合查询
     *
     * @param aggregation 聚合条件
     * @param inputType   输入对象
     * @param outputType  输出对象
     * @return (?)
     */
    Flux<?> aggregate(Aggregation aggregation, Class<T> inputType, Class<?> outputType);

    /**
     * 聚合查询
     *
     * @param aggregation 聚合条件
     * @param outputType  输出对象
     * @return （?）
     */
    Flux<?> aggregate(TypedAggregation<?> aggregation, Class<?> outputType);
}
