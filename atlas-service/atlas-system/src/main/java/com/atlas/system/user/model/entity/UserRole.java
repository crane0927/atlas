/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.model.entity;

import com.atlas.common.infra.db.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联实体类
 *
 * <p>对应数据库表：sys_user_role
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>id：关联ID，主键，雪花算法生成
 *   <li>userId：用户ID，外键，非空
 *   <li>roleId：角色ID，外键，非空
 * </ul>
 *
 * <p>审计与逻辑删除字段来自 {@link BaseEntity}。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_role")
public class UserRole extends BaseEntity {

  /** 关联ID，主键，雪花算法生成（String） */
  @TableId(type = IdType.ASSIGN_ID)
  private String id;

  /** 用户ID，外键，非空 */
  private String userId;

  /** 角色ID，外键，非空 */
  private String roleId;
}
