/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.util;

import com.atlas.auth.config.JwtConfig;
import com.atlas.auth.model.dto.TokenInfoDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JWT 工具类
 *
 * <p>提供 JWT Token 的生成、解析和验证功能。
 *
 * <p>功能特性：
 *
 * <ul>
 *   <li>生成 JWT Token（使用 RSA 私钥签名）
 *   <li>解析 JWT Token（验证签名并提取 Claims）
 *   <li>验证 Token 的有效性（签名、过期时间）
 * </ul>
 *
 * <p>Token 结构：
 *
 * <ul>
 *   <li>Header: alg=RS256, typ=JWT, kid=密钥ID
 *   <li>Payload: userId, username, roles, permissions, iat, exp, jti
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Component
public class JwtUtil {

  private final JwtConfig jwtConfig;
  private final PrivateKey privateKey;
  private final PublicKey publicKey;

  public JwtUtil(JwtConfig jwtConfig, PrivateKey privateKey, PublicKey publicKey) {
    this.jwtConfig = jwtConfig;
    this.privateKey = privateKey;
    this.publicKey = publicKey;
  }

  /**
   * 生成 JWT Token
   *
   * <p>根据用户信息生成 JWT Token，包含用户ID、用户名、角色、权限等信息。
   *
   * @param tokenInfo Token 信息（用户ID、用户名、角色、权限）
   * @return JWT Token 字符串
   */
  public String generateToken(TokenInfoDTO tokenInfo) {
    try {
      Instant now = Instant.now();
      Instant expire = now.plusSeconds(jwtConfig.getExpire());

      String tokenId = UUID.randomUUID().toString();

      return Jwts.builder()
          .header()
          .keyId(jwtConfig.getKeyId())
          .and()
          .claim("userId", tokenInfo.getUserId())
          .claim("username", tokenInfo.getUsername())
          .claim("roles", tokenInfo.getRoles())
          .claim("permissions", tokenInfo.getPermissions())
          .id(tokenId)
          .issuedAt(Date.from(now))
          .expiration(Date.from(expire))
          .signWith(privateKey)
          .compact();
    } catch (Exception e) {
      log.error("生成 JWT Token 失败: userId={}", tokenInfo.getUserId(), e);
      throw new RuntimeException("生成 JWT Token 失败: " + e.getMessage(), e);
    }
  }

  /**
   * 解析 JWT Token
   *
   * <p>解析 JWT Token 并提取用户信息，验证签名和过期时间。
   *
   * @param token JWT Token 字符串
   * @return Token 信息（用户ID、用户名、角色、权限等）
   * @throws io.jsonwebtoken.JwtException 如果 Token 无效、过期或签名错误
   */
  public TokenInfoDTO parseToken(String token) {
    try {
      Claims claims =
          Jwts.parser()
              .verifyWith(publicKey)
              .build()
              .parseSignedClaims(token)
              .getPayload();

      TokenInfoDTO tokenInfo = new TokenInfoDTO();
      tokenInfo.setTokenId(claims.getId());
      tokenInfo.setUserId(claims.get("userId", Long.class));
      tokenInfo.setUsername(claims.get("username", String.class));
      tokenInfo.setRoles(claims.get("roles", List.class));
      tokenInfo.setPermissions(claims.get("permissions", List.class));
      tokenInfo.setIssuedAt(claims.getIssuedAt().getTime() / 1000);
      tokenInfo.setExpiresAt(claims.getExpiration().getTime() / 1000);

      return tokenInfo;
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
      log.warn("JWT Token 已过期: tokenId={}", e.getClaims().getId());
      throw new RuntimeException("Token 已过期", e);
    } catch (io.jsonwebtoken.security.SignatureException e) {
      log.warn("JWT Token 签名无效: {}", e.getMessage());
      throw new RuntimeException("Token 签名无效", e);
    } catch (Exception e) {
      log.error("解析 JWT Token 失败", e);
      throw new RuntimeException("解析 Token 失败: " + e.getMessage(), e);
    }
  }

  /**
   * 验证 Token 的有效性
   *
   * <p>验证 Token 的格式、签名和过期时间。
   *
   * @param token JWT Token 字符串
   * @return true 表示 Token 有效，false 表示 Token 无效
   */
  public boolean validateToken(String token) {
    try {
      parseToken(token);
      return true;
    } catch (Exception e) {
      log.debug("Token 验证失败: {}", e.getMessage());
      return false;
    }
  }
}

