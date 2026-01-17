/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户角色关联实体类
 *
 * <p>对应数据库表：sys_user_role
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>id：关联ID，主键，自增
 *   <li>userId：用户ID，外键，非空
 *   <li>roleId：角色ID，外键，非空
 *   <li>createdAt：创建时间，非空，默认 CURRENT_TIMESTAMP
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@TableName("sys_user_role")
public class UserRole {

  /** 关联ID，主键，自增 */
  @TableId(type = IdType.AUTO)
  private Long id;

  /** 用户ID，外键，非空 */
  private Long userId;

  /** 角色ID，外键，非空 */
  private Long roleId;

  /** 创建时间，非空，默认 CURRENT_TIMESTAMP */
  private LocalDateTime createdAt;
}
