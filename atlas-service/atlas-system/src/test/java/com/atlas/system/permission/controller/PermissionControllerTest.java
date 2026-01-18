/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.permission.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.atlas.system.api.v1.model.dto.UserAuthoritiesDTO;
import com.atlas.system.permission.service.PermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * PermissionController 集成测试
 *
 * <p>测试权限控制器的接口功能。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@WebMvcTest(PermissionController.class)
class PermissionControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private PermissionService permissionService;

  @Test
  void testGetUserRolesSuccess() throws Exception {
    // Mock 服务方法
    when(permissionService.getRolesByUserId(1L)).thenReturn(Arrays.asList("admin", "user"));

    // 执行请求
    mockMvc
        .perform(get("/api/v1/users/1/roles"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0]").value("admin"))
        .andExpect(jsonPath("$.data[1]").value("user"));
  }

  @Test
  void testGetUserRolesEmpty() throws Exception {
    // Mock 服务方法（用户无角色）
    when(permissionService.getRolesByUserId(1L)).thenReturn(Collections.emptyList());

    // 执行请求
    mockMvc
        .perform(get("/api/v1/users/1/roles"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  void testGetUserPermissionsSuccess() throws Exception {
    // Mock 服务方法
    when(permissionService.getPermissionsByUserId(1L))
        .thenReturn(Arrays.asList("user:read", "user:write", "admin:delete"));

    // 执行请求
    mockMvc
        .perform(get("/api/v1/users/1/permissions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data[0]").value("user:read"))
        .andExpect(jsonPath("$.data[1]").value("user:write"))
        .andExpect(jsonPath("$.data[2]").value("admin:delete"));
  }

  @Test
  void testGetUserPermissionsEmpty() throws Exception {
    // Mock 服务方法（用户无权限）
    when(permissionService.getPermissionsByUserId(1L)).thenReturn(Collections.emptyList());

    // 执行请求
    mockMvc
        .perform(get("/api/v1/users/1/permissions"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  void testGetUserAuthoritiesSuccess() throws Exception {
    // 准备测试数据
    UserAuthoritiesDTO authoritiesDTO = new UserAuthoritiesDTO();
    authoritiesDTO.setUserId(1L);
    authoritiesDTO.setRoles(Arrays.asList("admin", "user"));
    authoritiesDTO.setPermissions(Arrays.asList("user:read", "user:write"));

    // Mock 服务方法
    when(permissionService.getAuthoritiesByUserId(1L)).thenReturn(authoritiesDTO);

    // 执行请求
    mockMvc
        .perform(get("/api/v1/users/1/authorities"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.data.userId").value(1L))
        .andExpect(jsonPath("$.data.roles").isArray())
        .andExpect(jsonPath("$.data.roles[0]").value("admin"))
        .andExpect(jsonPath("$.data.roles[1]").value("user"))
        .andExpect(jsonPath("$.data.permissions").isArray())
        .andExpect(jsonPath("$.data.permissions[0]").value("user:read"))
        .andExpect(jsonPath("$.data.permissions[1]").value("user:write"));
  }

  @Test
  void testGetUserAuthoritiesEmpty() throws Exception {
    // 准备测试数据（用户无角色和权限）
    UserAuthoritiesDTO authoritiesDTO = new UserAuthoritiesDTO();
    authoritiesDTO.setUserId(1L);
    authoritiesDTO.setRoles(Collections.emptyList());
    authoritiesDTO.setPermissions(Collections.emptyList());

    // Mock 服务方法
    when(permissionService.getAuthoritiesByUserId(1L)).thenReturn(authoritiesDTO);

    // 执行请求
    mockMvc
        .perform(get("/api/v1/users/1/authorities"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("000000"))
        .andExpect(jsonPath("$.data.userId").value(1L))
        .andExpect(jsonPath("$.data.roles").isArray())
        .andExpect(jsonPath("$.data.roles").isEmpty())
        .andExpect(jsonPath("$.data.permissions").isArray())
        .andExpect(jsonPath("$.data.permissions").isEmpty());
  }
}
