/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 信息 DTO
 *
 * <p>Token 信息数据传输对象，用于内部传递 Token 相关的用户信息。
 *
 * <p>字段说明：
 * <ul>
 *   <li>tokenId：Token ID（JWT 的 jti），用于黑名单管理</li>
 *   <li>userId：用户ID</li>
 *   <li>username：用户名</li>
 *   <li>roles：角色列表</li>
 *   <li>permissions：权限列表</li>
 *   <li>issuedAt：签发时间戳（秒）</li>
 *   <li>expiresAt：过期时间戳（秒）</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfoDTO {

  /** Token ID（JWT 的 jti） */
  private String tokenId;

  /** 用户ID */
  private Long userId;

  /** 用户名 */
  private String username;

  /** 角色列表 */
  private List<String> roles;

  /** 权限列表 */
  private List<String> permissions;

  /** 签发时间戳（秒） */
  private Long issuedAt;

  /** 过期时间戳（秒） */
  private Long expiresAt;
}

