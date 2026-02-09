/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.filter;

import com.atlas.gateway.config.GatewayProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.security.PublicKey;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 基于 JWT 公钥的网关 Token 校验器
 *
 * <p>从 Authorization: Bearer 提取 Token，使用配置的公钥验签并校验过期；通过后写入 X-User-Id、X-Username、X-User-Roles、X-User-Permissions 到转发请求头。
 * 仅当配置了公钥时由 {@link com.atlas.gateway.config.GatewayJwtConfiguration} 注册为 Bean，未配置时不会存在此 Bean，由 DefaultGatewayTokenValidator 兜底。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
public class JwtGatewayTokenValidator implements GatewayTokenValidator {

  /** 转发请求头：用户 ID */
  public static final String HEADER_X_USER_ID = "X-User-Id";

  /** 转发请求头：用户名 */
  public static final String HEADER_X_USERNAME = "X-Username";

  /** 转发请求头：角色列表（逗号分隔） */
  public static final String HEADER_X_USER_ROLES = "X-User-Roles";

  /** 转发请求头：权限列表（逗号分隔） */
  public static final String HEADER_X_USER_PERMISSIONS = "X-User-Permissions";

  private final PublicKey publicKey;

  public JwtGatewayTokenValidator(PublicKey publicKey, GatewayProperties gatewayProperties) {
    this.publicKey = publicKey;
  }

  @Override
  public Mono<ServerWebExchange> validate(ServerWebExchange exchange) {
    String token = extractBearerToken(exchange.getRequest());
    if (token == null || token.isEmpty()) {
      log.debug("请求中无 Bearer Token");
      return Mono.empty();
    }
    try {
      Claims claims =
          Jwts.parser()
              .verifyWith(publicKey)
              .build()
              .parseSignedClaims(token)
              .getPayload();

      Long userId = claims.get("userId", Long.class);
      String username = claims.get("username", String.class);
      @SuppressWarnings("unchecked")
      List<String> roles = claims.get("roles", List.class);
      @SuppressWarnings("unchecked")
      List<String> permissions = claims.get("permissions", List.class);

      ServerHttpRequest requestWithHeaders =
          exchange
              .getRequest()
              .mutate()
              .header(HEADER_X_USER_ID, userId != null ? String.valueOf(userId) : "")
              .header(HEADER_X_USERNAME, username != null ? username : "")
              .header(
                  HEADER_X_USER_ROLES,
                  roles != null ? String.join(",", roles) : "")
              .header(
                  HEADER_X_USER_PERMISSIONS,
                  permissions != null ? String.join(",", permissions) : "")
              .build();

      return Mono.just(exchange.mutate().request(requestWithHeaders).build());
    } catch (Exception e) {
      log.debug("JWT 校验失败: {}", e.getMessage());
      return Mono.empty();
    }
  }

  private static String extractBearerToken(ServerHttpRequest request) {
    String authorization = request.getHeaders().getFirst("Authorization");
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      return null;
    }
    return authorization.substring(7).trim();
  }
}
