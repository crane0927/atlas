/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.db.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 基础实体类
 *
 * <p>提供通用的审计字段定义，业务实体类可以继承此类以使用审计功能。
 *
 * <p>包含的字段：
 * <ul>
 *   <li>id：主键 ID，使用自增策略</li>
 *   <li>createTime：创建时间，插入时自动填充</li>
 *   <li>updateTime：更新时间，插入和更新时自动填充</li>
 *   <li>createBy：创建人，插入时自动填充</li>
 *   <li>updateBy：更新人，更新时自动填充</li>
 * </ul>
 *
 * <p>使用示例：
 *
 * <pre>{@code
 * @Data
 * @EqualsAndHashCode(callSuper = true)
 * @TableName("sys_user")
 * public class User extends BaseEntity {
 *     private String username;
 *     private String email;
 *     // 其他业务字段...
 *     // 审计字段（createTime、updateTime、createBy、updateBy）已从 BaseEntity 继承
 * }
 * }</pre>
 *
 * <p>注意：
 * <ul>
 *   <li>审计字段的自动填充由 {@link com.atlas.common.infra.db.handler.AuditMetaObjectHandler} 处理</li>
 *   <li>如果业务实体类不需要审计功能，可以不继承此类，自行定义字段</li>
 *   <li>如果业务实体类需要自定义审计字段名称，可以不继承此类，自行定义字段并使用 {@link TableField} 注解</li>
 * </ul>
 *
 * @author Atlas
 */
@Data
public class BaseEntity {

  /** 主键 ID，使用自增策略 */
  @TableId(type = IdType.AUTO)
  private Long id;

  /** 创建时间，插入时自动填充 */
  @TableField(fill = FieldFill.INSERT)
  private LocalDateTime createTime;

  /** 更新时间，插入和更新时自动填充 */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private LocalDateTime updateTime;

  /** 创建人，插入时自动填充 */
  @TableField(fill = FieldFill.INSERT)
  private String createBy;

  /** 更新人，更新时自动填充 */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private String updateBy;
}

