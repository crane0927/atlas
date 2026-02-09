/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.user.model.entity;

import com.atlas.common.infra.db.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户实体类
 *
 * <p>对应数据库表：sys_user
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>userId：用户ID，主键，自增
 *   <li>username：用户名，唯一，非空
 *   <li>password：密码（加密），非空
 *   <li>nickname：昵称，可空
 *   <li>email：邮箱，可空
 *   <li>phone：手机号，可空
 *   <li>status：用户状态，非空，默认 'ACTIVE'
 *   <li>avatar：头像URL，可空
 * </ul>
 *
 * <p>审计与逻辑删除字段来自 {@link BaseEntity}。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

  /** 用户ID，主键，自增 */
  @TableId(type = IdType.AUTO)
  private Long userId;

  /** 用户名，唯一，非空 */
  private String username;

  /** 密码（加密），非空 */
  private String password;

  /** 昵称，可空 */
  private String nickname;

  /** 邮箱，可空 */
  private String email;

  /** 手机号，可空 */
  private String phone;

  /** 用户状态，非空，默认 'ACTIVE' */
  private String status;

  /** 头像URL，可空 */
  private String avatar;
}
