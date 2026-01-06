/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.service;

import com.atlas.auth.model.dto.TokenInfoDTO;
import java.util.Map;

/**
 * 会话服务接口
 *
 * <p>提供用户会话和 Token 黑名单的管理功能。
 *
 * <p>功能特性：
 *
 * <ul>
 *   <li>存储用户会话信息到 Redis
 *   <li>从 Redis 获取用户会话信息
 *   <li>删除用户会话信息
 *   <li>将 Token 加入黑名单
 *   <li>检查 Token 是否在黑名单中
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public interface SessionService {

  /**
   * 保存用户会话信息
   *
   * <p>将用户会话信息存储到 Redis，Key 格式：`session:{userId}`。
   *
   * @param userId 用户ID
   * @param tokenInfo Token 信息
   * @param expireSeconds 过期时间（秒）
   */
  void saveSession(Long userId, TokenInfoDTO tokenInfo, Long expireSeconds);

  /**
   * 获取用户会话信息
   *
   * <p>从 Redis 获取用户会话信息。
   *
   * @param userId 用户ID
   * @return 会话信息（Map 格式），如果不存在则返回 null
   */
  Map<String, Object> getSession(Long userId);

  /**
   * 删除用户会话信息
   *
   * <p>从 Redis 删除用户会话信息。
   *
   * @param userId 用户ID
   */
  void deleteSession(Long userId);

  /**
   * 将 Token 加入黑名单
   *
   * <p>将 Token 加入黑名单，Key 格式：`token:blacklist:{tokenId}`。
   *
   * @param tokenId Token ID
   * @param userId 用户ID
   * @param expireSeconds 过期时间（秒）
   */
  void addToBlacklist(String tokenId, Long userId, Long expireSeconds);

  /**
   * 检查 Token 是否在黑名单中
   *
   * <p>检查 Token 是否在黑名单中。
   *
   * @param tokenId Token ID
   * @return true 表示 Token 在黑名单中，false 表示不在黑名单中
   */
  boolean isBlacklisted(String tokenId);
}

