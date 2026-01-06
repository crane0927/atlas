/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.service;

import com.atlas.auth.model.dto.TokenInfoDTO;

/**
 * Token 服务接口
 *
 * <p>提供 Token 的生成、解析和验证功能。
 *
 * <p>功能特性：
 *
 * <ul>
 *   <li>生成 JWT Token（包含用户信息）
 *   <li>解析 JWT Token（提取用户信息）
 *   <li>验证 Token 的有效性（签名、过期时间、黑名单）
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public interface TokenService {

  /**
   * 生成 JWT Token
   *
   * <p>根据用户信息生成 JWT Token，包含用户ID、用户名、角色、权限等信息。
   *
   * @param tokenInfo Token 信息（用户ID、用户名、角色、权限）
   * @return JWT Token 字符串
   */
  String generateToken(TokenInfoDTO tokenInfo);

  /**
   * 解析 JWT Token
   *
   * <p>解析 JWT Token 并提取用户信息，验证签名和过期时间。
   *
   * @param token JWT Token 字符串
   * @return Token 信息（用户ID、用户名、角色、权限等）
   * @throws RuntimeException 如果 Token 无效、过期或签名错误
   */
  TokenInfoDTO parseToken(String token);

  /**
   * 验证 Token 的有效性
   *
   * <p>验证 Token 的格式、签名、过期时间和黑名单状态。
   *
   * @param token JWT Token 字符串
   * @return Token 信息（如果有效），否则返回 null
   */
  TokenInfoDTO validateToken(String token);
}

