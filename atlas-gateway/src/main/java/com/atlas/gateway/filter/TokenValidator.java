/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * Token 校验器接口
 *
 * <p>提供 Token 校验的扩展点，允许业务模块实现具体的 Token 校验逻辑。
 *
 * <p>使用场景：
 *
 * <ul>
 *   <li>JWT Token 校验
 *   <li>OAuth2 Token 校验
 *   <li>自定义 Token 校验逻辑
 * </ul>
 *
 * <p>实现示例：
 *
 * <pre>{@code
 * @Component
 * public class JwtTokenValidator implements TokenValidator {
 *     @Override
 *     public boolean validate(ServerHttpRequest request) {
 *         String token = request.getHeaders().getFirst("Authorization");
 *         if (token == null || !token.startsWith("Bearer ")) {
 *             return false;
 *         }
 *         // 实现 JWT Token 校验逻辑
 *         return jwtService.validateToken(token.substring(7));
 *     }
 * }
 * }</pre>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
public interface TokenValidator {

  /**
   * 校验 Token
   *
   * <p>从请求中提取 Token 并进行校验。
   *
   * <p>实现要求：
   *
   * <ul>
   *   <li>从请求头或请求参数中提取 Token
   *   <li>验证 Token 的有效性（格式、签名、过期时间等）
   *   <li>返回校验结果（true 表示校验通过，false 表示校验失败）
   * </ul>
   *
   * @param request 服务器 HTTP 请求对象
   * @return true 表示 Token 校验通过，false 表示 Token 校验失败
   */
  boolean validate(ServerHttpRequest request);
}

