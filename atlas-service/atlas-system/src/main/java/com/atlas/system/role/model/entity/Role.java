/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.role.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 角色实体类
 *
 * <p>对应数据库表：sys_role
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>roleId：角色ID，主键，自增
 *   <li>roleCode：角色代码，唯一，非空
 *   <li>roleName：角色名称，非空
 *   <li>description：角色描述，可空
 *   <li>status：角色状态，非空，默认 'ACTIVE'
 *   <li>createdAt：创建时间，非空，默认 CURRENT_TIMESTAMP
 *   <li>updatedAt：更新时间，非空，默认 CURRENT_TIMESTAMP
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@TableName("sys_role")
public class Role {

  /** 角色ID，主键，自增 */
  @TableId(type = IdType.AUTO)
  private Long roleId;

  /** 角色代码，唯一，非空 */
  private String roleCode;

  /** 角色名称，非空 */
  private String roleName;

  /** 角色描述，可空 */
  private String description;

  /** 角色状态，非空，默认 'ACTIVE' */
  private String status;

  /** 创建时间，非空，默认 CURRENT_TIMESTAMP */
  private LocalDateTime createdAt;

  /** 更新时间，非空，默认 CURRENT_TIMESTAMP */
  private LocalDateTime updatedAt;
}
