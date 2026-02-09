/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.infra.web.security;

import com.atlas.common.feature.security.user.LoginUser;
import com.atlas.common.feature.security.user.SimpleLoginUser;
import com.atlas.common.feature.security.validator.TokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 安全上下文过滤器
 *
 * <p>优先从 Gateway 下传的请求头（X-User-Id、X-Username、X-User-Roles、X-User-Permissions）构建用户并设置安全上下文；若无则从
 * Authorization Bearer Token 委托 {@link TokenValidator} 校验并设置。
 *
 * @author Atlas
 * @since 1.0.0
 */
@Slf4j
public class SecurityContextFilter extends OncePerRequestFilter {

  /** Gateway 下传的用户 ID 请求头（与 atlas-gateway JwtGatewayTokenValidator 一致） */
  public static final String HEADER_X_USER_ID = "X-User-Id";

  /** Gateway 下传的用户名请求头 */
  public static final String HEADER_X_USERNAME = "X-Username";

  /** Gateway 下传的角色列表请求头（逗号分隔） */
  public static final String HEADER_X_USER_ROLES = "X-User-Roles";

  /** Gateway 下传的权限列表请求头（逗号分隔） */
  public static final String HEADER_X_USER_PERMISSIONS = "X-User-Permissions";

  private final TokenValidator tokenValidator;
  private final SecurityContextImpl securityContext;

  public SecurityContextFilter(TokenValidator tokenValidator, SecurityContextImpl securityContext) {
    this.tokenValidator = tokenValidator;
    this.securityContext = securityContext;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      LoginUser loginUser = loginUserFromHeaders(request);
      if (loginUser == null) {
        String token = extractToken(request);
        if (token != null && !token.trim().isEmpty()) {
          loginUser = tokenValidator.validateToken(token);
        }
      }
      if (loginUser != null) {
        securityContext.setLoginUser(loginUser);
        log.debug(
            "设置安全上下文: userId={}, username={}", loginUser.getUserId(), loginUser.getUsername());
      } else {
        log.debug("未从请求头或 Token 解析到用户，不设置安全上下文");
      }
      filterChain.doFilter(request, response);
    } finally {
      securityContext.clear();
    }
  }

  /** 从 Gateway 下传的请求头构建 LoginUser；若无则返回 null。 */
  private LoginUser loginUserFromHeaders(HttpServletRequest request) {
    String userIdStr = request.getHeader(HEADER_X_USER_ID);
    if (userIdStr == null || userIdStr.trim().isEmpty()) {
      return null;
    }
    Object userId;
    try {
      userId = Long.parseLong(userIdStr.trim());
    } catch (NumberFormatException e) {
      userId = userIdStr.trim();
    }
    String username = request.getHeader(HEADER_X_USERNAME);
    if (username == null) {
      username = "";
    }
    String rolesHeader = request.getHeader(HEADER_X_USER_ROLES);
    List<String> roles =
        rolesHeader != null && !rolesHeader.trim().isEmpty()
            ? Arrays.stream(rolesHeader.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList())
            : Collections.emptyList();
    String permissionsHeader = request.getHeader(HEADER_X_USER_PERMISSIONS);
    List<String> permissions =
        permissionsHeader != null && !permissionsHeader.trim().isEmpty()
            ? Arrays.stream(permissionsHeader.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList())
            : Collections.emptyList();
    return new SimpleLoginUser(userId, username, roles, permissions);
  }

  private String extractToken(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");
    if (authorization == null || authorization.trim().isEmpty()) {
      return null;
    }
    String trimmed = authorization.trim();
    if (trimmed.startsWith("Bearer ")) {
      return trimmed.substring(7);
    }
    return trimmed;
  }
}
