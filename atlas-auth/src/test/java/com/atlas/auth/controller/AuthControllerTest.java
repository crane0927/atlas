/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.atlas.auth.model.vo.LoginRequestVO;
import com.atlas.auth.model.vo.LoginResponseVO;
import com.atlas.auth.model.vo.UserVO;
import com.atlas.auth.service.AuthService;
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
}

