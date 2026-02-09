/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.common.infra.web.security;

import com.atlas.common.feature.security.validator.TokenValidator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 安全上下文过滤器
 *
 * <p>从请求头提取 Token，委托 {@link TokenValidator} 校验并设置安全上下文。
 *
 * @author Atlas
 * @since 1.0.0
 */
@Slf4j
public class SecurityContextFilter extends OncePerRequestFilter {

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
      String token = extractToken(request);
      if (token != null && !token.trim().isEmpty()) {
        var loginUser = tokenValidator.validateToken(token);
        if (loginUser != null) {
          securityContext.setLoginUser(loginUser);
          log.debug("设置安全上下文: userId={}, username={}", loginUser.getUserId(), loginUser.getUsername());
        } else {
          log.debug("Token 验证失败，不设置安全上下文");
        }
      } else {
        log.debug("请求头中未找到 Token，不设置安全上下文");
      }
      filterChain.doFilter(request, response);
    } finally {
      securityContext.clear();
    }
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
