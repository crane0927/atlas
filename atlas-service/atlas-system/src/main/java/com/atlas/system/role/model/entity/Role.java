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
 * 角色实体类
 *
 * <p>对应数据库表：sys_role
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>roleId：角色ID，主键，雪花算法生成
 *   <li>roleCode：角色代码，唯一，非空
 *   <li>roleName：角色名称，非空
 *   <li>description：角色描述，可空
 *   <li>status：角色状态，非空，默认 'ACTIVE'
 * </ul>
 *
 * <p>审计与逻辑删除字段来自 {@link BaseEntity}。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity {

  /** 角色ID，主键，雪花算法生成（String） */
  @TableId(type = IdType.ASSIGN_ID)
  private String roleId;

  /** 角色代码，唯一，非空 */
  private String roleCode;

  /** 角色名称，非空 */
  private String roleName;

  /** 角色描述，可空 */
  private String description;

  /** 角色状态，非空，默认 'ACTIVE' */
  private String status;
}
