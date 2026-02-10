/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.user.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户更新 DTO
 *
 * <p>用于更新用户的请求数据传输对象。不包含 username、password，改密可单独接口扩展。
 *
 * <ul>
 *   <li>nickname：昵称，可选
 *   <li>email：邮箱，可选
 *   <li>phone：手机号，可选
 *   <li>status：用户状态，可选（ACTIVE/INACTIVE/DELETED）
 *   <li>avatar：头像URL，可选
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class UserUpdateDTO {

  /** 昵称，可选 */
  @Size(max = 100, message = "昵称长度不能超过 100 个字符")
  private String nickname;

  /** 邮箱，可选 */
  @Size(max = 255, message = "邮箱长度不能超过 255 个字符")
  private String email;

  /** 手机号，可选 */
  @Size(max = 20, message = "手机号长度不能超过 20 个字符")
  private String phone;

  /** 用户状态，可选（ACTIVE/INACTIVE/DELETED） */
  @Size(max = 20, message = "状态长度不能超过 20 个字符")
  private String status;

  /** 头像URL，可选 */
  @Size(max = 500, message = "头像URL长度不能超过 500 个字符")
  private String avatar;
}
