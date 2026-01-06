/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户基本信息 VO
 *
 * <p>用户基本信息视图对象，用于登录响应。
 *
 * <p>字段说明：
 * <ul>
 *   <li>userId：用户ID</li>
 *   <li>username：用户名</li>
 *   <li>nickname：昵称（可选）</li>
 *   <li>email：邮箱（可选）</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

  /** 用户ID */
  private Long userId;

  /** 用户名 */
  private String username;

  /** 昵称（可选） */
  private String nickname;

  /** 邮箱（可选） */
  private String email;
}

