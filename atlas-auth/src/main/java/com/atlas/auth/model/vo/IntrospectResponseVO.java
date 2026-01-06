/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.model.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token Introspection 响应 VO
 *
 * <p>Token Introspection 响应数据，包含 Token 验证结果和用户信息。
 *
 * <p>字段说明：
 * <ul>
 *   <li>active：Token 是否有效</li>
 *   <li>userId：用户ID（如果 Token 有效）</li>
 *   <li>username：用户名（如果 Token 有效）</li>
 *   <li>roles：角色列表（如果 Token 有效）</li>
 *   <li>permissions：权限列表（如果 Token 有效）</li>
 *   <li>expiresAt：Token 过期时间戳（秒，如果 Token 有效）</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntrospectResponseVO {

  /** Token 是否有效 */
  private Boolean active;

  /** 用户ID（如果 Token 有效） */
  private Long userId;

  /** 用户名（如果 Token 有效） */
  private String username;

  /** 角色列表（如果 Token 有效） */
  private List<String> roles;

  /** 权限列表（如果 Token 有效） */
  private List<String> permissions;

  /** Token 过期时间戳（秒，如果 Token 有效） */
  private Long expiresAt;
}

