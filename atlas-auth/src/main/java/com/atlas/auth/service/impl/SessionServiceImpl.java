/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.auth.service.impl;

import com.atlas.auth.model.dto.TokenInfoDTO;
import com.atlas.auth.service.SessionService;
import com.atlas.common.infra.redis.key.RedisKeyBuilder;
import com.atlas.common.infra.redis.util.CacheUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 会话服务实现类
 *
 * <p>实现用户会话和 Token 黑名单的管理功能，使用 Redis 存储。
 *
 * <p>Redis Key 设计（通过 RedisKeyBuilder，自动补齐前缀）：
 *
 * <ul>
 *   <li>会话信息：atlas:{service}:session:{userId} (String, JSON, 带过期时间)
 *   <li>Token 黑名单：atlas:{service}:blacklist:{tokenId} (String, JSON, 带过期时间)
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class SessionServiceImpl implements SessionService {

  private static final String BUSINESS_SESSION = "session";
  private static final String BUSINESS_BLACKLIST = "blacklist";


  @Override
  public void saveSession(String userId, TokenInfoDTO tokenInfo, Long expireSeconds) {
    try {
      String key =
          RedisKeyBuilder.builder()
              .business(BUSINESS_SESSION)
              .id(userId)
              .build();

      // 构建会话信息 Map
      Map<String, Object> sessionData = new HashMap<>();
      sessionData.put("userId", userId);
      sessionData.put("username", tokenInfo.getUsername());
      sessionData.put("token", tokenInfo.getTokenId()); // 存储 tokenId，不存储完整 Token
      sessionData.put("loginTime", Instant.now().toString());
      sessionData.put("expiresAt", tokenInfo.getExpiresAt());

      // 存储到 Redis，设置过期时间
      CacheUtil.set(key, sessionData, expireSeconds.intValue());
      log.debug("保存用户会话成功: userId={}, expireSeconds={}", userId, expireSeconds);
    } catch (Exception e) {
      log.error("保存用户会话失败: userId={}", userId, e);
      throw new RuntimeException("保存用户会话失败: " + e.getMessage(), e);
    }
  }

  @Override
  public Map<String, Object> getSession(String userId) {
    try {
      String key =
          RedisKeyBuilder.builder()
              .business(BUSINESS_SESSION)
              .id(userId)
              .build();
      Map<String, Object> sessionData = CacheUtil.get(key, Map.class);
      if (sessionData == null) {
        log.debug("用户会话不存在: userId={}", userId);
        return null;
      }
      return sessionData;
    } catch (Exception e) {
      log.error("获取用户会话失败: userId={}", userId, e);
      return null;
    }
  }

  @Override
  public void deleteSession(String userId) {
    try {
      String key =
          RedisKeyBuilder.builder()
              .business(BUSINESS_SESSION)
              .id(userId)
              .build();
      CacheUtil.delete(key);
      log.debug("删除用户会话成功: userId={}", userId);
    } catch (Exception e) {
      log.error("删除用户会话失败: userId={}", userId, e);
      // 删除失败不抛出异常，避免影响业务流程
    }
  }

  @Override
  public void addToBlacklist(String tokenId, String userId, Long expireSeconds) {
    try {
      String key =
          RedisKeyBuilder.builder()
              .business(BUSINESS_BLACKLIST)
              .id(tokenId)
              .build();

      // 构建黑名单信息 Map
      Map<String, Object> blacklistData = new HashMap<>();
      blacklistData.put("tokenId", tokenId);
      blacklistData.put("userId", userId);
      blacklistData.put("expiresAt", System.currentTimeMillis() / 1000 + expireSeconds);

      // 存储到 Redis，设置过期时间
      CacheUtil.set(key, blacklistData, expireSeconds.intValue());
      log.debug(
          "Token 加入黑名单成功: tokenId={}, userId={}, expireSeconds={}", tokenId, userId, expireSeconds);
    } catch (Exception e) {
      log.error("Token 加入黑名单失败: tokenId={}", tokenId, e);
      throw new RuntimeException("Token 加入黑名单失败: " + e.getMessage(), e);
    }
  }

  @Override
  public boolean isBlacklisted(String tokenId) {
    try {
      String key =
          RedisKeyBuilder.builder()
              .business(BUSINESS_BLACKLIST)
              .id(tokenId)
              .build();
      boolean exists = CacheUtil.exists(key);
      if (exists) {
        log.debug("Token 在黑名单中: tokenId={}", tokenId);
      }
      return exists;
    } catch (Exception e) {
      log.error("检查 Token 黑名单失败: tokenId={}", tokenId, e);
      // 检查失败时返回 false，避免误判
      return false;
    }
  }
}
