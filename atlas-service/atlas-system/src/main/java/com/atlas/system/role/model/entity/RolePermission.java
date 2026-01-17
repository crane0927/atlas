/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.role.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 角色权限关联实体类
 *
 * <p>对应数据库表：sys_role_permission
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>id：关联ID，主键，自增
 *   <li>roleId：角色ID，外键，非空
 *   <li>permissionId：权限ID，外键，非空
 *   <li>createdAt：创建时间，非空，默认 CURRENT_TIMESTAMP
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@TableName("sys_role_permission")
public class RolePermission {

  /** 关联ID，主键，自增 */
  @TableId(type = IdType.AUTO)
  private Long id;

  /** 角色ID，外键，非空 */
  private Long roleId;

  /** 权限ID，外键，非空 */
  private Long permissionId;

  /** 创建时间，非空，默认 CURRENT_TIMESTAMP */
  private LocalDateTime createdAt;
}
