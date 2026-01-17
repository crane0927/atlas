/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户创建 DTO
 *
 * <p>用于创建用户的请求数据传输对象。
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>username：用户名，必填，3-50 个字符
 *   <li>password：密码，必填，至少 8 个字符
 *   <li>nickname：昵称，可选
 *   <li>email：邮箱，可选
 *   <li>phone：手机号，可选
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
public class UserCreateDTO {

  /** 用户名，必填，3-50 个字符 */
  @NotBlank(message = "用户名不能为空")
  @Size(min = 3, max = 50, message = "用户名长度必须在 3-50 个字符之间")
  private String username;

  /** 密码，必填，至少 8 个字符 */
  @NotBlank(message = "密码不能为空")
  @Size(min = 8, message = "密码长度至少 8 个字符")
  private String password;

  /** 昵称，可选 */
  private String nickname;

  /** 邮箱，可选 */
  private String email;

  /** 手机号，可选 */
  private String phone;
}
