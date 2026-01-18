/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.permission.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.system.api.v1.model.dto.UserAuthoritiesDTO;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.permission.mapper.PermissionMapper;
import com.atlas.system.permission.model.dto.PermissionCreateDTO;
import com.atlas.system.permission.model.entity.Permission;
import com.atlas.system.permission.service.impl.PermissionServiceImpl;
import com.atlas.system.role.mapper.RolePermissionMapper;
import com.atlas.system.user.mapper.UserMapper;
import com.atlas.system.user.mapper.UserRoleMapper;
import com.atlas.system.user.model.entity.User;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * PermissionService 单元测试
 *
 * <p>测试权限服务的业务逻辑。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

  @Mock private UserMapper userMapper;

  @Mock private UserRoleMapper userRoleMapper;

  @Mock private RolePermissionMapper rolePermissionMapper;

  @Mock private PermissionMapper permissionMapper;

  @InjectMocks private PermissionServiceImpl permissionService;

  private User user;

  @BeforeEach
  void setUp() {
    // 准备测试数据
    user = new User();
    user.setUserId(1L);
    user.setUsername("testuser");
    user.setStatus("ACTIVE");
  }

  @Test
  void testGetRolesByUserIdSuccess() {
    // Mock 服务调用
    when(userMapper.selectById(1L)).thenReturn(user);
    when(userRoleMapper.selectRoleCodesByUserId(1L)).thenReturn(Arrays.asList("admin", "user"));

    // 执行查询
    List<String> roles = permissionService.getRolesByUserId(1L);

    // 验证结果
    assertNotNull(roles);
    assertEquals(2, roles.size());
    assertTrue(roles.contains("admin"));
    assertTrue(roles.contains("user"));

    // 验证服务调用
    verify(userMapper).selectById(1L);
    verify(userRoleMapper).selectRoleCodesByUserId(1L);
  }

  @Test
  void testGetRolesByUserIdWithUserNotFound() {
    // Mock 服务调用（用户不存在）
    when(userMapper.selectById(1L)).thenReturn(null);

    // 执行查询
    List<String> roles = permissionService.getRolesByUserId(1L);

    // 验证结果（返回空列表）
    assertNotNull(roles);
    assertTrue(roles.isEmpty());

    // 验证服务调用
    verify(userMapper).selectById(1L);
    verify(userRoleMapper, never()).selectRoleCodesByUserId(any());
  }

  @Test
  void testGetPermissionsByUserIdSuccess() {
    // Mock 服务调用
    when(userMapper.selectById(1L)).thenReturn(user);
    when(userRoleMapper.selectRoleIdsByUserId(1L)).thenReturn(Arrays.asList(1L, 2L));
    when(rolePermissionMapper.selectPermissionCodesByRoleIds(Arrays.asList(1L, 2L)))
        .thenReturn(Arrays.asList("user:read", "user:write", "admin:delete"));

    // 执行查询
    List<String> permissions = permissionService.getPermissionsByUserId(1L);

    // 验证结果
    assertNotNull(permissions);
    assertEquals(3, permissions.size());
    assertTrue(permissions.contains("user:read"));
    assertTrue(permissions.contains("user:write"));
    assertTrue(permissions.contains("admin:delete"));

    // 验证服务调用
    verify(userMapper).selectById(1L);
    verify(userRoleMapper).selectRoleIdsByUserId(1L);
    verify(rolePermissionMapper).selectPermissionCodesByRoleIds(Arrays.asList(1L, 2L));
  }

  @Test
  void testGetPermissionsByUserIdWithUserNotFound() {
    // Mock 服务调用（用户不存在）
    when(userMapper.selectById(1L)).thenReturn(null);

    // 执行查询
    List<String> permissions = permissionService.getPermissionsByUserId(1L);

    // 验证结果（返回空列表）
    assertNotNull(permissions);
    assertTrue(permissions.isEmpty());

    // 验证服务调用
    verify(userMapper).selectById(1L);
    verify(userRoleMapper, never()).selectRoleIdsByUserId(any());
  }

  @Test
  void testGetAuthoritiesByUserIdSuccess() {
    // Mock 服务调用
    when(userMapper.selectById(1L)).thenReturn(user);
    when(userRoleMapper.selectRoleCodesByUserId(1L)).thenReturn(Arrays.asList("admin", "user"));
    when(userRoleMapper.selectRoleIdsByUserId(1L)).thenReturn(Arrays.asList(1L, 2L));
    when(rolePermissionMapper.selectPermissionCodesByRoleIds(Arrays.asList(1L, 2L)))
        .thenReturn(Arrays.asList("user:read", "user:write"));

    // 执行查询
    UserAuthoritiesDTO authorities = permissionService.getAuthoritiesByUserId(1L);

    // 验证结果
    assertNotNull(authorities);
    assertEquals(1L, authorities.getUserId());
    assertEquals(2, authorities.getRoles().size());
    assertEquals(2, authorities.getPermissions().size());
    assertTrue(authorities.getRoles().contains("admin"));
    assertTrue(authorities.getRoles().contains("user"));
    assertTrue(authorities.getPermissions().contains("user:read"));
    assertTrue(authorities.getPermissions().contains("user:write"));

    // 验证服务调用
    verify(userMapper).selectById(1L);
    verify(userRoleMapper).selectRoleCodesByUserId(1L);
    verify(userRoleMapper).selectRoleIdsByUserId(1L);
    verify(rolePermissionMapper).selectPermissionCodesByRoleIds(Arrays.asList(1L, 2L));
  }

  @Test
  void testGetAuthoritiesByUserIdWithUserNotFound() {
    // Mock 服务调用（用户不存在）
    when(userMapper.selectById(1L)).thenReturn(null);

    // 执行查询
    UserAuthoritiesDTO authorities = permissionService.getAuthoritiesByUserId(1L);

    // 验证结果（返回空列表）
    assertNotNull(authorities);
    assertEquals(1L, authorities.getUserId());
    assertTrue(authorities.getRoles().isEmpty());
    assertTrue(authorities.getPermissions().isEmpty());

    // 验证服务调用
    verify(userMapper).selectById(1L);
  }

  @Test
  void testCreatePermissionSuccess() {
    // 准备测试数据
    PermissionCreateDTO permissionCreateDTO = new PermissionCreateDTO();
    permissionCreateDTO.setPermissionCode("user:read");
    permissionCreateDTO.setPermissionName("用户读取");
    permissionCreateDTO.setDescription("读取用户信息");

    // Mock 服务调用
    when(permissionMapper.selectOne(any())).thenReturn(null);
    when(permissionMapper.insert(any(Permission.class)))
        .thenAnswer(
            invocation -> {
              Permission p = invocation.getArgument(0);
              p.setPermissionId(1L);
              return 1;
            });

    // 执行创建
    Long permissionId = permissionService.createPermission(permissionCreateDTO);

    // 验证结果
    assertNotNull(permissionId);
    assertEquals(1L, permissionId);

    // 验证服务调用
    verify(permissionMapper).selectOne(any());
    verify(permissionMapper).insert(any(Permission.class));
  }

  @Test
  void testCreatePermissionWithDuplicateCode() {
    // 准备测试数据
    PermissionCreateDTO permissionCreateDTO = new PermissionCreateDTO();
    permissionCreateDTO.setPermissionCode("user:read");

    Permission existingPermission = new Permission();
    existingPermission.setPermissionId(1L);
    existingPermission.setPermissionCode("user:read");
    existingPermission.setStatus("ACTIVE");

    // Mock 服务调用（权限代码已存在）
    when(permissionMapper.selectOne(any())).thenReturn(existingPermission);

    // 执行创建并验证异常
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> permissionService.createPermission(permissionCreateDTO));

    assertEquals(SystemErrorCode.PERMISSION_CODE_ALREADY_EXISTS, exception.getErrorCode());
    assertEquals("权限代码已存在", exception.getMessage());

    // 验证服务调用
    verify(permissionMapper).selectOne(any());
    verify(permissionMapper, never()).insert(any(Permission.class));
  }
}
