/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.controller;

import com.atlas.auth.config.JwtConfig;
import com.atlas.auth.model.vo.IntrospectRequestVO;
import com.atlas.auth.model.vo.IntrospectResponseVO;
import com.atlas.auth.model.vo.LoginRequestVO;
import com.atlas.auth.model.vo.LoginResponseVO;
import com.atlas.auth.model.vo.PublicKeyResponseVO;
import com.atlas.auth.model.dto.TokenInfoDTO;
import com.atlas.auth.service.AuthService;
import com.atlas.auth.service.TokenService;
import com.atlas.common.feature.core.result.Result;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final JwtConfig jwtConfig;
  private final TokenService tokenService;

  public AuthController(AuthService authService, JwtConfig jwtConfig, TokenService tokenService) {
    this.authService = authService;
    this.jwtConfig = jwtConfig;
    this.tokenService = tokenService;
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
   * 获取 JWT 公钥接口
   *
   * <p>为 Gateway 提供 JWT 公钥，支持 Gateway 自主验证 Token。
   *
   * <p>请求示例：
   * <pre>{@code
   * GET /api/v1/auth/public-key
   * }</pre>
   *
   * <p>响应示例：
   * <pre>{@code
   * {
   *   "code": "000000",
   *   "message": "操作成功",
   *   "data": {
   *     "algorithm": "RS256",
   *     "publicKey": "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...\n-----END PUBLIC KEY-----",
   *     "keyId": "key-2025-01-06"
   *   },
   *   "timestamp": 1704542400000,
   *   "traceId": "abc123"
   * }
   * }</pre>
   *
   * @return 公钥响应（算法、公钥、密钥ID）
   */
  @GetMapping("/public-key")
  public Result<PublicKeyResponseVO> getPublicKey() {
    log.debug("获取 JWT 公钥请求");
    PublicKeyResponseVO response = new PublicKeyResponseVO();
    response.setAlgorithm(jwtConfig.getAlgorithm());
    response.setPublicKey(jwtConfig.getPublicKeyPem());
    response.setKeyId(jwtConfig.getKeyId());
    return Result.success(response);
  }

  /**
   * Token Introspection 接口
   *
   * <p>为 Gateway 提供 Token 验证接口，Gateway 通过调用此接口验证 Token。
   *
   * <p>请求示例：
   * <pre>{@code
   * POST /api/v1/auth/introspect
   * Content-Type: application/json
   *
   * {
   *   "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
   * }
   * }</pre>
   *
   * <p>响应示例（Token 有效）：
   * <pre>{@code
   * {
   *   "code": "000000",
   *   "message": "操作成功",
   *   "data": {
   *     "active": true,
   *     "userId": 1,
   *     "username": "admin",
   *     "roles": ["admin", "user"],
   *     "permissions": ["user:read", "user:write"],
   *     "expiresAt": 1704549600
   *   },
   *   "timestamp": 1704542400000,
   *   "traceId": "abc123"
   * }
   * }</pre>
   *
   * <p>响应示例（Token 无效）：
   * <pre>{@code
   * {
   *   "code": "000000",
   *   "message": "操作成功",
   *   "data": {
   *     "active": false
   *   },
   *   "timestamp": 1704542400000,
   *   "traceId": "abc123"
   * }
   * }</pre>
   *
   * @param request Introspection 请求（Token）
   * @return Introspection 响应（Token 验证结果和用户信息）
   */
  @PostMapping("/introspect")
  public Result<IntrospectResponseVO> introspect(@Valid @RequestBody IntrospectRequestVO request) {
    log.debug("Token Introspection 请求: tokenId={}", request.getToken().substring(0, Math.min(20, request.getToken().length())));

    // 验证 Token
    TokenInfoDTO tokenInfo = tokenService.validateToken(request.getToken());

    IntrospectResponseVO response = new IntrospectResponseVO();
    if (tokenInfo != null) {
      // Token 有效
      response.setActive(true);
      response.setUserId(tokenInfo.getUserId());
      response.setUsername(tokenInfo.getUsername());
      response.setRoles(tokenInfo.getRoles());
      response.setPermissions(tokenInfo.getPermissions());
      response.setExpiresAt(tokenInfo.getExpiresAt());
    } else {
      // Token 无效
      response.setActive(false);
    }

    return Result.success(response);
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

