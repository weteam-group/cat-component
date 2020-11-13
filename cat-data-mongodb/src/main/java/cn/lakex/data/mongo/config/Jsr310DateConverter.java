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

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.Jsr310Converters;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * 兼容JSR310的时间格式Converters
 *
 * @author LarryKoo (larrykoo@126.com)
 * @slogon 站在巨人的肩膀上
 * @date 2020/11/11 17:52
 * @since 3.0.0
 */
public final class Jsr310DateConverter extends Jsr310Converters {
    private Jsr310DateConverter() {
    }

    /**
     * ZoneDateTime To DateTime
     */
    public static enum ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date> {
        /**
         * INSTANCE
         */
        INSTANCE;

        ZonedDateTimeToDateConverter() {
        }

        @Override
        public Date convert(ZonedDateTime source) {
            return Date.from(source.toInstant());
        }
    }

    /**
     * DateTime to ZonedDateTime
     */
    public static enum DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {
        /**
         * INSTANCE
         */
        INSTANCE;

        DateToZonedDateTimeConverter() {
        }

        @Override
        public ZonedDateTime convert(Date source) {
            return ZonedDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
        }
    }
}
