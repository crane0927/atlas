/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlas.auth.config.JwtConfig;
import com.atlas.auth.constant.AuthErrorCode;
import com.atlas.auth.model.dto.TokenInfoDTO;
import com.atlas.auth.model.vo.LoginRequestVO;
import com.atlas.auth.model.vo.LoginResponseVO;
import com.atlas.auth.service.impl.AuthServiceImpl;
import com.atlas.auth.util.PasswordUtil;
import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.feature.core.result.Result;
import com.atlas.system.api.v1.feign.PermissionQueryApi;
import com.atlas.system.api.v1.feign.UserQueryApi;
import com.atlas.system.api.v1.model.dto.UserAuthoritiesDTO;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.api.v1.model.enums.UserStatus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * AuthService 单元测试
 *
 * <p>测试认证服务的业务逻辑。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserQueryApi userQueryApi;

  @Mock private PermissionQueryApi permissionQueryApi;

  @Mock private PasswordUtil passwordUtil;

  @Mock private TokenService tokenService;

  @Mock private SessionService sessionService;

  @Mock private JwtConfig jwtConfig;

  @InjectMocks private AuthServiceImpl authService;

  private UserDTO userDTO;
  private UserAuthoritiesDTO authoritiesDTO;

  @BeforeEach
  void setUp() {
    // 准备测试数据
    userDTO = new UserDTO();
    userDTO.setUserId(1L);
    userDTO.setUsername("admin");
    userDTO.setNickname("管理员");
    userDTO.setEmail("admin@example.com");
    userDTO.setStatus(UserStatus.ACTIVE);

    authoritiesDTO = new UserAuthoritiesDTO();
    authoritiesDTO.setUserId(1L);
    authoritiesDTO.setRoles(Arrays.asList("admin", "user"));
    authoritiesDTO.setPermissions(Arrays.asList("user:read", "user:write"));

    // Mock 配置
    when(jwtConfig.getExpire()).thenReturn(7200L);
  }

  @Test
  void testLoginSuccess() {
    // 准备测试数据
    LoginRequestVO loginRequest = new LoginRequestVO("admin", "password123");

    // Mock 服务调用
    when(userQueryApi.getUserByUsername("admin"))
        .thenReturn(Result.success(userDTO));
    when(permissionQueryApi.getUserAuthorities(1L))
        .thenReturn(Result.success(authoritiesDTO));
    when(passwordUtil.matches("password123", "encoded-password")).thenReturn(true);

    TokenInfoDTO tokenInfo = new TokenInfoDTO();
    tokenInfo.setTokenId("token-id-123");
    tokenInfo.setUserId(1L);
    tokenInfo.setUsername("admin");
    tokenInfo.setRoles(Arrays.asList("admin", "user"));
    tokenInfo.setPermissions(Arrays.asList("user:read", "user:write"));
    tokenInfo.setIssuedAt(System.currentTimeMillis() / 1000);
    tokenInfo.setExpiresAt(System.currentTimeMillis() / 1000 + 7200);

    when(tokenService.generateToken(any(TokenInfoDTO.class))).thenReturn("test-token");
    when(tokenService.parseToken("test-token")).thenReturn(tokenInfo);

    // 执行登录
    LoginResponseVO response = authService.login(loginRequest);

    // 验证结果
    assertNotNull(response);
    assertEquals("test-token", response.getToken());
    assertEquals("Bearer", response.getTokenType());
    assertEquals(7200L, response.getExpiresIn());
    assertNotNull(response.getUser());
    assertEquals(1L, response.getUser().getUserId());
    assertEquals("admin", response.getUser().getUsername());

    // 验证服务调用
    verify(userQueryApi).getUserByUsername("admin");
    verify(permissionQueryApi).getUserAuthorities(1L);
    verify(tokenService).generateToken(any(TokenInfoDTO.class));
    verify(sessionService).saveSession(1L, any(TokenInfoDTO.class), anyLong());
  }

  @Test
  void testLoginWithUserNotFound() {
    // 准备测试数据
    LoginRequestVO loginRequest = new LoginRequestVO("admin", "password123");

    // Mock 服务调用（用户不存在）
    when(userQueryApi.getUserByUsername("admin"))
        .thenReturn(Result.error("012001", "用户不存在"));

    // 执行登录并验证异常
    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.login(loginRequest));
    assertEquals(AuthErrorCode.USER_NOT_FOUND, exception.getErrorCode());

    // 验证后续服务未被调用
    verify(permissionQueryApi, never()).getUserAuthorities(anyLong());
    verify(tokenService, never()).generateToken(any(TokenInfoDTO.class));
  }

  @Test
  void testLoginWithInactiveUser() {
    // 准备测试数据
    LoginRequestVO loginRequest = new LoginRequestVO("admin", "password123");
    userDTO.setStatus(UserStatus.INACTIVE);

    // Mock 服务调用
    when(userQueryApi.getUserByUsername("admin"))
        .thenReturn(Result.success(userDTO));

    // 执行登录并验证异常
    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.login(loginRequest));
    assertEquals(AuthErrorCode.USER_NOT_ACTIVE, exception.getErrorCode());
  }

  @Test
  void testLoginWithLockedUser() {
    // 准备测试数据
    LoginRequestVO loginRequest = new LoginRequestVO("admin", "password123");
    userDTO.setStatus(UserStatus.LOCKED);

    // Mock 服务调用
    when(userQueryApi.getUserByUsername("admin"))
        .thenReturn(Result.success(userDTO));

    // 执行登录并验证异常
    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.login(loginRequest));
    assertEquals(AuthErrorCode.USER_LOCKED, exception.getErrorCode());
  }

  @Test
  void testLogoutSuccess() {
    // 准备测试数据
    String token = "test-token";
    TokenInfoDTO tokenInfo = new TokenInfoDTO();
    tokenInfo.setTokenId("token-id-123");
    tokenInfo.setUserId(1L);
    tokenInfo.setUsername("admin");

    // Mock 服务调用
    when(tokenService.parseToken(token)).thenReturn(tokenInfo);
    when(tokenService.validateToken(token)).thenReturn(tokenInfo);

    // 执行登出
    authService.logout(token);

    // 验证服务调用
    verify(tokenService).parseToken(token);
    verify(tokenService).validateToken(token);
    verify(sessionService).addToBlacklist("token-id-123", 1L, 7200L);
    verify(sessionService).deleteSession(1L);
  }

  @Test
  void testLogoutWithInvalidToken() {
    // 准备测试数据
    String token = "invalid-token";

    // Mock 服务调用（Token 解析失败）
    when(tokenService.parseToken(token))
        .thenThrow(new RuntimeException("Token 无效"));

    // 执行登出并验证异常
    BusinessException exception =
        assertThrows(BusinessException.class, () -> authService.logout(token));
    assertEquals(AuthErrorCode.TOKEN_INVALID, exception.getErrorCode());

    // 验证后续服务未被调用
    verify(sessionService, never()).addToBlacklist(any(String.class), anyLong(), anyLong());
    verify(sessionService, never()).deleteSession(anyLong());
  }
}

