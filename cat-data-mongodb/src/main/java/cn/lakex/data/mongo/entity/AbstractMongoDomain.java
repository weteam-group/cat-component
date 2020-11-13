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

package cn.lakex.data.mongo.entity;

import cn.lakex.framework.core.entity.AbstractDomain;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * A abstract domain for MongoDB
 *
 * @author LarryKoo (larrykoo@126.com)
 * @slogon 站在巨人的肩膀上
 * @date 2020/11/11 16:51
 * @since 3.0.0
 */
@Getter
@Setter
public abstract class AbstractMongoDomain extends AbstractDomain {
    private static final long serialVersionUID = -203869230800013790L;

    @Schema(title = "An ObjectId of Mongo")
    String id;

    @Field(value = "created_by")
    @Schema(title = "Created user")
    String createdBy;

    @Field(value = "created_date")
    @Schema(title = "Created date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdDate;

    @Field(value = "last_modified_by")
    @Schema(title = "Last modified user")
    String lastModifiedBy;

    @Field(value = "last_modified_date")
    @Schema(title = "Last modified date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime lastModifiedDate;

    @Field(value = "del_flag")
    @Indexed(name = "idx_del_flag")
    @Schema(title = "Delete flag，the default is false.")
    Boolean delFlag;
}
