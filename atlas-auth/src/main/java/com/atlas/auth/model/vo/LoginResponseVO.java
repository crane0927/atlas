/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 VO
 *
 * <p>用户登录响应数据，包含 Token 和用户基本信息。
 *
 * <p>字段说明：
 * <ul>
 *   <li>token：JWT Token</li>
 *   <li>tokenType：Token 类型（固定为 "Bearer"）</li>
 *   <li>expiresIn：Token 过期时间（秒）</li>
 *   <li>user：用户基本信息</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseVO {

  /** JWT Token */
  private String token;

  /** Token 类型（固定为 "Bearer"） */
  private String tokenType = "Bearer";

  /** Token 过期时间（秒） */
  private Long expiresIn;

  /** 用户基本信息 */
  private UserVO user;
}

