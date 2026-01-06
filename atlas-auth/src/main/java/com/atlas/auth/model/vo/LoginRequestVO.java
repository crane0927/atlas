/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.model.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求 VO
 *
 * <p>用户登录请求参数，包含用户名和密码。
 *
 * <p>字段说明：
 * <ul>
 *   <li>username：用户名，必填字段</li>
 *   <li>password：密码，必填字段</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestVO {

  /** 用户名 */
  @NotBlank(message = "用户名不能为空")
  private String username;

  /** 密码 */
  @NotBlank(message = "密码不能为空")
  private String password;
}

