/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.service.impl;

import com.atlas.auth.model.dto.TokenInfoDTO;
import com.atlas.auth.service.SessionService;
import com.atlas.auth.service.TokenService;
import com.atlas.auth.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Token 服务实现类
 *
 * <p>实现 Token 的生成、解析和验证功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

  private final JwtUtil jwtUtil;
  private final SessionService sessionService;

  public TokenServiceImpl(JwtUtil jwtUtil, SessionService sessionService) {
    this.jwtUtil = jwtUtil;
    this.sessionService = sessionService;
  }

  @Override
  public String generateToken(TokenInfoDTO tokenInfo) {
    return jwtUtil.generateToken(tokenInfo);
  }

  @Override
  public TokenInfoDTO parseToken(String token) {
    return jwtUtil.parseToken(token);
  }

  @Override
  public TokenInfoDTO validateToken(String token) {
    try {
      // 解析 Token（验证格式、签名、过期时间）
      TokenInfoDTO tokenInfo = jwtUtil.parseToken(token);

      // 检查 Token 是否在黑名单中
      if (sessionService.isBlacklisted(tokenInfo.getTokenId())) {
        log.warn("Token 在黑名单中: tokenId={}", tokenInfo.getTokenId());
        return null;
      }

      return tokenInfo;
    } catch (Exception e) {
      log.debug("Token 验证失败: {}", e.getMessage());
      return null;
    }
  }
}

