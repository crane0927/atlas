/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.auth.model.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求 VO
 *
 * <p>用户登录请求参数：用户名、RSA 加密密码（Base64）、验证码（当验证码开启时必填）。
 *
 * <p>字段说明：
 *
 * <ul>
 *   <li>username：用户名，必填
 *   <li>encryptedPassword：使用 GET /api/v1/auth/public-key 公钥加密后的密码（Base64），必填
 *   <li>captchaKey：GET /api/v1/auth/captcha 返回的 key，验证码开启时必填
 *   <li>captchaCode：用户输入的验证码，验证码开启时必填
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

  /** RSA 加密后的密码（Base64），前端使用公钥加密 */
  @NotBlank(message = "密码不能为空")
  private String encryptedPassword;

  /** 验证码 key（验证码开启时必填） */
  private String captchaKey;

  /** 用户输入的验证码（验证码开启时必填） */
  private String captchaCode;
}
