/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.filter;

import com.atlas.auth.context.SecurityContextImpl;
import com.atlas.auth.model.dto.LoginUserImpl;
import com.atlas.auth.model.dto.TokenInfoDTO;
import com.atlas.auth.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 安全上下文过滤器
 *
 * <p>从请求头中提取 Token，验证 Token 并设置安全上下文，供下游服务使用。
 *
 * <p>功能特性：
 * <ul>
 *   <li>从 `Authorization: Bearer {token}` 请求头中提取 Token</li>
 *   <li>验证 Token 的有效性（签名、过期时间、黑名单）</li>
 *   <li>将用户信息封装为 `LoginUser` 对象</li>
 *   <li>设置 `SecurityContext`（使用 ThreadLocal）</li>
 *   <li>请求结束时清理 `SecurityContext`（避免内存泄漏）</li>
 * </ul>
 *
 * <p>处理流程：
 * <ol>
 *   <li>从请求头提取 Token（如果存在）</li>
 *   <li>验证 Token 有效性</li>
 *   <li>如果 Token 有效，设置 `SecurityContext`</li>
 *   <li>继续处理请求</li>
 *   <li>请求结束时清理 `SecurityContext`</li>
 * </ol>
 *
 * <p>注意：
 * <ul>
 *   <li>如果 Token 无效或缺失，不设置 `SecurityContext`，请求继续处理（由业务层决定是否拒绝）</li>
 *   <li>使用 `@Order` 注解确保在其他过滤器之后执行</li>
 *   <li>使用 `OncePerRequestFilter` 确保每个请求只执行一次</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(100)
public class SecurityContextFilter extends OncePerRequestFilter {

  private final TokenService tokenService;
  private final SecurityContextImpl securityContext;

  public SecurityContextFilter(TokenService tokenService, SecurityContextImpl securityContext) {
    this.tokenService = tokenService;
    this.securityContext = securityContext;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      // 1. 从请求头提取 Token
      String token = extractToken(request);

      if (token != null && !token.trim().isEmpty()) {
        // 2. 验证 Token
        TokenInfoDTO tokenInfo = tokenService.validateToken(token);

        if (tokenInfo != null) {
          // 3. 封装用户信息为 LoginUser
          LoginUserImpl loginUser = new LoginUserImpl();
          loginUser.setUserId(tokenInfo.getUserId());
          loginUser.setUsername(tokenInfo.getUsername());
          loginUser.setRoles(tokenInfo.getRoles());
          loginUser.setPermissions(tokenInfo.getPermissions());

          // 4. 设置 SecurityContext
          securityContext.setLoginUser(loginUser);
          log.debug("设置安全上下文: userId={}, username={}", tokenInfo.getUserId(), tokenInfo.getUsername());
        } else {
          log.debug("Token 验证失败，不设置安全上下文");
        }
      } else {
        log.debug("请求头中未找到 Token，不设置安全上下文");
      }

      // 5. 继续处理请求
      filterChain.doFilter(request, response);
    } finally {
      // 6. 请求结束时清理 SecurityContext
      securityContext.clear();
    }
  }

  /**
   * 从请求头中提取 Token
   *
   * <p>支持格式：`Authorization: Bearer {token}`
   *
   * @param request HTTP 请求
   * @return Token 字符串，如果不存在则返回 null
   */
  private String extractToken(HttpServletRequest request) {
    String authorization = request.getHeader("Authorization");
    if (authorization == null || authorization.trim().isEmpty()) {
      return null;
    }

    String trimmed = authorization.trim();
    if (trimmed.startsWith("Bearer ")) {
      return trimmed.substring(7);
    }

    // 如果不以 "Bearer " 开头，假设整个字符串就是 Token
    return trimmed;
  }
}

