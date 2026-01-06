/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.controller;

import com.atlas.auth.model.vo.LoginRequestVO;
import com.atlas.auth.model.vo.LoginResponseVO;
import com.atlas.auth.service.AuthService;
import com.atlas.common.feature.core.result.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 *
 * <p>提供用户登录、登出等认证相关的 RESTful 接口。
 *
 * <p>接口列表：
 * <ul>
 *   <li>POST /api/v1/auth/login - 用户登录</li>
 *   <li>POST /api/v1/auth/logout - 用户登出</li>
 * </ul>
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * 用户登录接口
   *
   * <p>验证用户身份并签发 Token。
   *
   * <p>请求示例：
   * <pre>{@code
   * POST /api/v1/auth/login
   * Content-Type: application/json
   *
   * {
   *   "username": "admin",
   *   "password": "password123"
   * }
   * }</pre>
   *
   * <p>响应示例：
   * <pre>{@code
   * {
   *   "code": "000000",
   *   "message": "登录成功",
   *   "data": {
   *     "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
   *     "tokenType": "Bearer",
   *     "expiresIn": 7200,
   *     "user": {
   *       "userId": 1,
   *       "username": "admin",
   *       "nickname": "管理员",
   *       "email": "admin@example.com"
   *     }
   *   },
   *   "timestamp": 1704542400000,
   *   "traceId": "abc123"
   * }
   * }</pre>
   *
   * @param loginRequest 登录请求（用户名、密码）
   * @return 登录响应（Token、用户信息）
   */
  @PostMapping("/login")
  public Result<LoginResponseVO> login(@Valid @RequestBody LoginRequestVO loginRequest) {
    log.debug("用户登录请求: username={}", loginRequest.getUsername());
    LoginResponseVO response = authService.login(loginRequest);
    return Result.success("登录成功", response);
  }

  /**
   * 用户登出接口
   *
   * <p>使 Token 失效并清除会话。
   *
   * <p>请求示例：
   * <pre>{@code
   * POST /api/v1/auth/logout
   * Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
   * }</pre>
   *
   * <p>响应示例：
   * <pre>{@code
   * {
   *   "code": "000000",
   *   "message": "登出成功",
   *   "timestamp": 1704542400000,
   *   "traceId": "abc123"
   * }
   * }</pre>
   *
   * @param authorization Authorization 请求头（格式：Bearer {token}）
   * @return 登出响应
   */
  @PostMapping("/logout")
  public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
    // 从 Authorization 头中提取 Token
    String token = extractToken(authorization);
    if (token == null) {
      throw new com.atlas.common.feature.core.exception.BusinessException(
          com.atlas.auth.constant.AuthErrorCode.TOKEN_MISSING, "Token 不能为空");
    }

    log.debug("用户登出请求: tokenId={}", token.substring(0, Math.min(20, token.length())));
    authService.logout(token);
    return Result.success("登出成功");
  }

  /**
   * 从 Authorization 头中提取 Token
   *
   * <p>支持格式：Bearer {token}
   *
   * @param authorization Authorization 请求头
   * @return Token 字符串，如果格式不正确则返回 null
   */
  private String extractToken(String authorization) {
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

