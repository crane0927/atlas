/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.system.api.v1.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证密码请求 DTO
 *
 * <p>用于 POST /users/verify-password 请求体，避免密码出现在 URL 查询参数中。
 *
 * <ul>
 *   <li>username：用户名
 *   <li>password：明文密码
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPasswordRequest {

  /** 用户名 */
  private String username;

  /** 明文密码 */
  private String password;
}
