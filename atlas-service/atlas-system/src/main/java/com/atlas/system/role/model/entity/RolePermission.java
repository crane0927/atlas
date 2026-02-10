/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.role.model.entity;

import com.atlas.common.infra.db.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色权限关联实体类
 *
 * <p>对应数据库表：sys_role_permission
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>id：关联ID，主键，雪花算法生成
 *   <li>roleId：角色ID，外键，非空
 *   <li>permissionId：权限ID，外键，非空
 * </ul>
 *
 * <p>审计与逻辑删除字段来自 {@link BaseEntity}。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_permission")
public class RolePermission extends BaseEntity {

  /** 关联ID，主键，雪花算法生成 */
  @TableId(type = IdType.ASSIGN_ID)
  private Long id;

  /** 角色ID，外键，非空 */
  private Long roleId;

  /** 权限ID，外键，非空 */
  private Long permissionId;
}
