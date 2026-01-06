/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.atlas.auth.config.JwtConfig;
import com.atlas.auth.model.dto.TokenInfoDTO;
import com.atlas.auth.model.vo.IntrospectRequestVO;
import com.atlas.auth.model.vo.IntrospectResponseVO;
import com.atlas.auth.model.vo.LoginRequestVO;
import com.atlas.auth.model.vo.LoginResponseVO;
import com.atlas.auth.model.vo.PublicKeyResponseVO;
import com.atlas.auth.model.vo.UserVO;
import com.atlas.auth.service.AuthService;
import com.atlas.auth.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * AuthController 单元测试
 *
 * <p>测试认证控制器的接口功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AuthService authService;

  @MockBean private JwtConfig jwtConfig;

  @MockBean private TokenService tokenService;

  @Test
  void testLoginSuccess() throws Exception {
    // 准备测试数据
    LoginRequestVO loginRequest = new LoginRequestVO("admin", "password123");

    LoginResponseVO loginResponse = new LoginResponseVO();
    loginResponse.setToken("test-token");
    loginResponse.setTokenType("Bearer");
    loginResponse.setExpiresIn(7200L);

    UserVO userVO = new UserVO();
    userVO.setUserId(1L);
    userVO.setUsername("admin");
    userVO.setNickname("管理员");
    userVO.setEmail("admin@example.com");
    loginResponse.setUser(userVO);

    // Mock 服务方法
    when(authService.login(any(LoginRequestVO.class))).thenReturn(loginResponse);

    // 执行请求
    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.message").value("登录成功"))
        .andExpect(jsonPath("$.data.token").value("test-token"))
        .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.data.expiresIn").value(7200L))
        .andExpect(jsonPath("$.data.user.userId").value(1L))
        .andExpect(jsonPath("$.data.user.username").value("admin"));
  }

  @Test
  void testLoginWithEmptyUsername() throws Exception {
    // 准备测试数据
    LoginRequestVO loginRequest = new LoginRequestVO("", "password123");

    // 执行请求
    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("051001"));
  }

  @Test
  void testLogoutSuccess() throws Exception {
    // Mock 服务方法（无返回值）
    when(authService.logout(any(String.class))).thenReturn(null);

    // 执行请求
    mockMvc
        .perform(
            post("/api/v1/auth/logout")
                .header("Authorization", "Bearer test-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.message").value("登出成功"));
  }

  @Test
  void testLogoutWithoutToken() throws Exception {
    // 执行请求（不提供 Token）
    mockMvc
        .perform(post("/api/v1/auth/logout"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("013004")); // TOKEN_MISSING
  }

  @Test
  void testGetPublicKey() throws Exception {
    // Mock 配置
    when(jwtConfig.getAlgorithm()).thenReturn("RS256");
    when(jwtConfig.getPublicKeyPem()).thenReturn("-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...\n-----END PUBLIC KEY-----");
    when(jwtConfig.getKeyId()).thenReturn("key-2025-01-06");

    // 执行请求
    mockMvc
        .perform(get("/api/v1/auth/public-key"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.data.algorithm").value("RS256"))
        .andExpect(jsonPath("$.data.publicKey").exists())
        .andExpect(jsonPath("$.data.keyId").value("key-2025-01-06"));
  }

  @Test
  void testIntrospectWithValidToken() throws Exception {
    // 准备测试数据
    IntrospectRequestVO request = new IntrospectRequestVO("valid-token");

    TokenInfoDTO tokenInfo = new TokenInfoDTO();
    tokenInfo.setTokenId("token-id-123");
    tokenInfo.setUserId(1L);
    tokenInfo.setUsername("admin");
    tokenInfo.setRoles(java.util.Arrays.asList("admin", "user"));
    tokenInfo.setPermissions(java.util.Arrays.asList("user:read", "user:write"));
    tokenInfo.setExpiresAt(System.currentTimeMillis() / 1000 + 7200);

    // Mock 服务方法
    when(tokenService.validateToken("valid-token")).thenReturn(tokenInfo);

    // 执行请求
    mockMvc
        .perform(
            post("/api/v1/auth/introspect")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.data.active").value(true))
        .andExpect(jsonPath("$.data.userId").value(1L))
        .andExpect(jsonPath("$.data.username").value("admin"));
  }

  @Test
  void testIntrospectWithInvalidToken() throws Exception {
    // 准备测试数据
    IntrospectRequestVO request = new IntrospectRequestVO("invalid-token");

    // Mock 服务方法（Token 无效）
    when(tokenService.validateToken("invalid-token")).thenReturn(null);

    // 执行请求
    mockMvc
        .perform(
            post("/api/v1/auth/introspect")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.data.active").value(false));
  }
}

