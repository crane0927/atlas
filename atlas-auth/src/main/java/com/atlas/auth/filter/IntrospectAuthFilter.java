/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.auth.filter;

import com.atlas.auth.config.AuthProperties;
import com.atlas.auth.constant.AuthErrorCode;
import com.atlas.common.feature.core.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Introspection 接口服务间认证过滤器
 *
 * <p>当配置了 {@code atlas.auth.introspect.api-key} 时，对 POST /api/v1/auth/introspect 请求校验 请求头
 * X-Introspect-Api-Key 与配置值一致，否则返回 401。未配置 api-key 时不校验。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
public class IntrospectAuthFilter extends OncePerRequestFilter {

  /** 仅对该路径生效 */
  private static final String INTROSPECT_PATH = "/api/v1/auth/introspect";

  /** 请求头：服务间认证 API Key（与 Gateway 等调用方配置一致） */
  public static final String HEADER_X_INTROSPECT_API_KEY = "X-Introspect-Api-Key";

  private final AuthProperties authProperties;
  private final ObjectMapper objectMapper;

  public IntrospectAuthFilter(AuthProperties authProperties, ObjectMapper objectMapper) {
    this.authProperties = authProperties;
    this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !INTROSPECT_PATH.equals(request.getRequestURI())
        || !"POST".equalsIgnoreCase(request.getMethod());
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String configuredKey =
        authProperties.getIntrospect() != null && authProperties.getIntrospect().getApiKey() != null
            ? authProperties.getIntrospect().getApiKey().trim()
            : "";
    if (configuredKey.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }
    String providedKey = request.getHeader(HEADER_X_INTROSPECT_API_KEY);
    if (providedKey == null || !configuredKey.equals(providedKey.trim())) {
      log.warn("Introspection 接口服务间认证失败: 缺少或错误的 X-Introspect-Api-Key");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json;charset=UTF-8");
      Result<Void> result =
          Result.error(AuthErrorCode.INTROSPECT_UNAUTHORIZED, "Introspection 接口需要服务间认证");
      byte[] body = objectMapper.writeValueAsString(result).getBytes(StandardCharsets.UTF_8);
      response.getOutputStream().write(body);
      return;
    }
    filterChain.doFilter(request, response);
  }
}
