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

package cn.lakex.data.mongo.config;

import cn.lakex.data.mongo.MongoLogConstant;
import cn.lakex.data.mongo.service.CatReactiveMongoRepositoryFactoryBean;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * A custom auto configuration base on Spring-Boot
 *
 * @author LarryKoo (larrykoo@126.com)
 * @slogon 站在巨人的肩膀上
 * @date 2020/11/11 17:57
 * @since 3.0.0
 */
@Slf4j
@Configuration
@ConditionalOnClass(MongoClient.class)
@EnableConfigurationProperties({MongoProperties.class})
@EnableReactiveMongoRepositories(repositoryFactoryBeanClass = CatReactiveMongoRepositoryFactoryBean.class)
public class CatMongoAutoConfiguration extends AbstractReactiveMongoConfiguration {
    @Getter
    private final MongoProperties properties;
    @Getter
    private final Environment environment;

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public CatMongoAutoConfiguration(MongoProperties properties, Environment environment,
                                     ReactiveMongoTemplate reactiveMongoTemplate) {
        this.properties = properties;
        this.environment = environment;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @PostConstruct
    public void init() {
        log.info("Init {} CatReactiveMongoRepositoryFactory.", MongoLogConstant.PLUGIN_NAME);
        CatReactiveMongoRepositoryFactoryBean.
                CatReactiveMongoRepositoryFactory.setOperations(reactiveMongoTemplate);
    }

    @Override
    protected String getDatabaseName() {
        return properties.getDatabase();
    }

    @Override
    public ReactiveMongoTemplate reactiveMongoTemplate(ReactiveMongoDatabaseFactory factory, MappingMongoConverter converter) {
        log.info("Init {} a custom reactiveMongoTemplate.", MongoLogConstant.PLUGIN_NAME);
        return new ReactiveMongoTemplate(reactiveMongoClient(), getDatabaseName());
    }

    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create();
    }

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Override
    public MongoCustomConversions customConversions() {
        log.info("Init {} a custom MongoCustomConversions.", MongoLogConstant.PLUGIN_NAME);
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(Jsr310DateConverter.DateToZonedDateTimeConverter.INSTANCE);
        converters.add(Jsr310DateConverter.ZonedDateTimeToDateConverter.INSTANCE);
        converters.add(Jsr310DateConverter.DateToLocalDateConverter.INSTANCE);
        converters.add(Jsr310DateConverter.LocalDateToDateConverter.INSTANCE);
        converters.add(Jsr310DateConverter.DateToLocalDateTimeConverter.INSTANCE);
        converters.add(Jsr310DateConverter.LocalDateTimeToDateConverter.INSTANCE);
        return MongoCustomConversions.create(configurer -> {
            converters.addAll(converters);
        });
    }
}
