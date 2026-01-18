/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.common.infra.web.exception.GlobalExceptionHandler;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.api.v1.model.enums.UserStatus;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * UserController 集成测试
 *
 * <p>测试用户控制器的接口功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@WebMvcTest(
    controllers = UserController.class,
    excludeAutoConfiguration = {
      org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    })
@Import(GlobalExceptionHandler.class)
@TestPropertySource(
    properties = {
      "spring.autoconfigure.exclude=com.alibaba.cloud.nacos.NacosConfigAutoConfiguration,com.alibaba.cloud.nacos.NacosDiscoveryAutoConfiguration,com.alibaba.cloud.nacos.endpoint.NacosConfigEndpointAutoConfiguration",
      "spring.cloud.nacos.config.enabled=false",
      "spring.cloud.nacos.discovery.enabled=false"
    })
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private UserService userService;

  @Test
  void testGetUserByIdSuccess() throws Exception {
    // 准备测试数据
    UserDTO userDTO = new UserDTO();
    userDTO.setUserId(1L);
    userDTO.setUsername("testuser");
    userDTO.setNickname("测试用户");
    userDTO.setEmail("test@example.com");
    userDTO.setPhone("13800138000");
    userDTO.setStatus(UserStatus.ACTIVE);
    userDTO.setAvatar("http://example.com/avatar.jpg");

    // Mock 服务方法
    when(userService.getUserById(1L)).thenReturn(userDTO);

    // 执行请求
    mockMvc
        .perform(get("/api/v1/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.data.userId").value(1L))
        .andExpect(jsonPath("$.data.username").value("testuser"))
        .andExpect(jsonPath("$.data.nickname").value("测试用户"))
        .andExpect(jsonPath("$.data.email").value("test@example.com"))
        .andExpect(jsonPath("$.data.phone").value("13800138000"))
        .andExpect(jsonPath("$.data.status").value("ACTIVE"));
  }

  @Test
  void testGetUserByIdNotFound() throws Exception {
    // Mock 服务方法（用户不存在）
    when(userService.getUserById(1L))
        .thenThrow(new BusinessException(SystemErrorCode.USER_NOT_FOUND, "用户不存在"));

    // 执行请求
    mockMvc
        .perform(get("/api/v1/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(SystemErrorCode.USER_NOT_FOUND))
        .andExpect(jsonPath("$.message").value("用户不存在"));
  }

  @Test
  void testGetUserByUsernameSuccess() throws Exception {
    // 准备测试数据
    UserDTO userDTO = new UserDTO();
    userDTO.setUserId(1L);
    userDTO.setUsername("testuser");
    userDTO.setNickname("测试用户");
    userDTO.setStatus(UserStatus.ACTIVE);

    // Mock 服务方法
    when(userService.getUserByUsername("testuser")).thenReturn(userDTO);

    // 执行请求
    mockMvc
        .perform(get("/api/v1/users/by-username").param("username", "testuser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.data.userId").value(1L))
        .andExpect(jsonPath("$.data.username").value("testuser"))
        .andExpect(jsonPath("$.data.nickname").value("测试用户"));
  }

  @Test
  void testGetUserByUsernameNotFound() throws Exception {
    // Mock 服务方法（用户不存在）
    when(userService.getUserByUsername("testuser"))
        .thenThrow(new BusinessException(SystemErrorCode.USER_NOT_FOUND, "用户不存在"));

    // 执行请求
    mockMvc
        .perform(get("/api/v1/users/by-username").param("username", "testuser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(SystemErrorCode.USER_NOT_FOUND))
        .andExpect(jsonPath("$.message").value("用户不存在"));
  }
}
