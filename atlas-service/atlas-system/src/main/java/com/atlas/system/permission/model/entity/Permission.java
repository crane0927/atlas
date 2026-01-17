/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.permission.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 权限实体类
 *
 * <p>对应数据库表：sys_permission
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>permissionId：权限ID，主键，自增
 *   <li>permissionCode：权限代码，唯一，非空
 *   <li>permissionName：权限名称，非空
 *   <li>description：权限描述，可空
 *   <li>status：权限状态，非空，默认 'ACTIVE'
 *   <li>createdAt：创建时间，非空，默认 CURRENT_TIMESTAMP
 *   <li>updatedAt：更新时间，非空，默认 CURRENT_TIMESTAMP
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@TableName("sys_permission")
public class Permission {

  /** 权限ID，主键，自增 */
  @TableId(type = IdType.AUTO)
  private Long permissionId;

  /** 权限代码，唯一，非空 */
  private String permissionCode;

  /** 权限名称，非空 */
  private String permissionName;

  /** 权限描述，可空 */
  private String description;

  /** 权限状态，非空，默认 'ACTIVE' */
  private String status;

  /** 创建时间，非空，默认 CURRENT_TIMESTAMP */
  private LocalDateTime createdAt;

  /** 更新时间，非空，默认 CURRENT_TIMESTAMP */
  private LocalDateTime updatedAt;
}
