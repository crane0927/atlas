/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.api.v1.model.dto;

import com.atlas.system.api.v1.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户基本信息 DTO
 *
 * <p>用户基本信息数据传输对象，用于用户查询接口的响应。
 *
 * <p>字段说明：
 * <ul>
 *   <li>userId：用户ID，必填字段，不能为 null</li>
 *   <li>username：用户名，必填字段，不能为 null 或空字符串</li>
 *   <li>nickname：昵称，可选字段，可以为 null（向后兼容）</li>
 *   <li>email：邮箱，可选字段，可以为 null（向后兼容）</li>
 *   <li>phone：手机号，可选字段，可以为 null（向后兼容）</li>
 *   <li>status：用户状态，必填字段，不能为 null</li>
 *   <li>avatar：头像URL，可选字段，可以为 null（向后兼容）</li>
 * </ul>
 *
 * <p>向后兼容性：
 * <ul>
 *   <li>新增字段必须可空或提供默认值</li>
 *   <li>不允许删除或修改现有字段</li>
 *   <li>不允许修改字段类型或语义</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

  /** 用户ID */
  private Long userId;

  /** 用户名 */
  private String username;

  /** 昵称（可选，向后兼容） */
  private String nickname;

  /** 邮箱（可选，向后兼容） */
  private String email;

  /** 手机号（可选，向后兼容） */
  private String phone;

  /** 用户状态 */
  private UserStatus status;

  /** 头像URL（可选，向后兼容） */
  private String avatar;
}

