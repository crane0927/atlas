/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.permission.model.entity;

import com.atlas.common.infra.db.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 *
 * <p>对应数据库表：sys_permission
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>permissionId：权限ID，主键，雪花算法生成
 *   <li>permissionCode：权限代码，唯一，非空
 *   <li>permissionName：权限名称，非空
 *   <li>description：权限描述，可空
 *   <li>status：权限状态，非空，默认 'ACTIVE'
 * </ul>
 *
 * <p>审计与逻辑删除字段来自 {@link BaseEntity}。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class Permission extends BaseEntity {

  /** 权限ID，主键，雪花算法生成（String） */
  @TableId(type = IdType.ASSIGN_ID)
  private String permissionId;

  /** 权限代码，唯一，非空 */
  private String permissionCode;

  /** 权限名称，非空 */
  private String permissionName;

  /** 权限描述，可空 */
  private String description;

  /** 权限状态，非空，默认 'ACTIVE' */
  private String status;
}
