/*\n * Copyright (c) 2025 Atlas. All rights reserved.\n */
package com.atlas.system.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.atlas.common.feature.core.exception.BusinessException;
import com.atlas.system.api.v1.model.dto.UserDTO;
import com.atlas.system.api.v1.model.enums.UserStatus;
import com.atlas.system.constant.SystemErrorCode;
import com.atlas.system.role.mapper.RoleMapper;
import com.atlas.system.role.model.entity.Role;
import com.atlas.system.user.mapper.UserMapper;
import com.atlas.system.user.mapper.UserRoleMapper;
import com.atlas.system.user.model.dto.UserCreateDTO;
import com.atlas.system.user.model.entity.User;
import com.atlas.system.user.model.entity.UserRole;
import com.atlas.system.user.service.impl.UserServiceImpl;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * UserService 单元测试
 *
 * <p>测试用户服务的业务逻辑。
 *
 * @author Atlas Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserMapper userMapper;

  @Mock private UserRoleMapper userRoleMapper;

  @Mock private RoleMapper roleMapper;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserServiceImpl userService;

  private User user;
  private UserDTO expectedUserDTO;

  @BeforeEach
  void setUp() {
    // 准备测试数据
    user = new User();
    user.setUserId(1L);
    user.setUsername("testuser");
    user.setPassword("encoded-password");
    user.setNickname("测试用户");
    user.setEmail("test@example.com");
    user.setPhone("13800138000");
    user.setStatus("ACTIVE");
    user.setAvatar("http://example.com/avatar.jpg");
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());

    expectedUserDTO = new UserDTO();
    expectedUserDTO.setUserId(1L);
    expectedUserDTO.setUsername("testuser");
    expectedUserDTO.setNickname("测试用户");
    expectedUserDTO.setEmail("test@example.com");
    expectedUserDTO.setPhone("13800138000");
    expectedUserDTO.setStatus(UserStatus.ACTIVE);
    expectedUserDTO.setAvatar("http://example.com/avatar.jpg");
  }

  @Test
  void testGetUserByIdSuccess() {
    // Mock 服务调用
    when(userMapper.selectById(1L)).thenReturn(user);

    // 执行查询
    UserDTO result = userService.getUserById(1L);

    // 验证结果
    assertNotNull(result);
    assertEquals(expectedUserDTO.getUserId(), result.getUserId());
    assertEquals(expectedUserDTO.getUsername(), result.getUsername());
    assertEquals(expectedUserDTO.getNickname(), result.getNickname());
    assertEquals(expectedUserDTO.getEmail(), result.getEmail());
    assertEquals(expectedUserDTO.getPhone(), result.getPhone());
    assertEquals(expectedUserDTO.getStatus(), result.getStatus());
    assertEquals(expectedUserDTO.getAvatar(), result.getAvatar());

    // 验证服务调用
    verify(userMapper).selectById(1L);
  }

  @Test
  void testGetUserByIdNotFound() {
    // Mock 服务调用（用户不存在）
    when(userMapper.selectById(1L)).thenReturn(null);

    // 执行查询并验证异常
    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.getUserById(1L));

    assertEquals(SystemErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    assertEquals("用户不存在", exception.getMessage());

    // 验证服务调用
    verify(userMapper).selectById(1L);
  }

  @Test
  void testGetUserByUsernameSuccess() {
    // Mock 服务调用
    when(userMapper.selectByUsername("testuser")).thenReturn(user);

    // 执行查询
    UserDTO result = userService.getUserByUsername("testuser");

    // 验证结果
    assertNotNull(result);
    assertEquals(expectedUserDTO.getUserId(), result.getUserId());
    assertEquals(expectedUserDTO.getUsername(), result.getUsername());

    // 验证服务调用
    verify(userMapper).selectByUsername("testuser");
  }

  @Test
  void testGetUserByUsernameNotFound() {
    // Mock 服务调用（用户不存在）
    when(userMapper.selectByUsername("testuser")).thenReturn(null);

    // 执行查询并验证异常
    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.getUserByUsername("testuser"));

    assertEquals(SystemErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    assertEquals("用户不存在", exception.getMessage());

    // 验证服务调用
    verify(userMapper).selectByUsername("testuser");
  }

  @Test
  void testCreateUserSuccess() {
    // 准备测试数据
    UserCreateDTO userCreateDTO = new UserCreateDTO();
    userCreateDTO.setUsername("newuser");
    userCreateDTO.setPassword("password123");
    userCreateDTO.setNickname("新用户");
    userCreateDTO.setEmail("newuser@example.com");
    userCreateDTO.setPhone("13900139000");

    // Mock 服务调用
    when(userMapper.selectByUsername("newuser")).thenReturn(null);
    when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
    when(userMapper.insert(any(User.class)))
        .thenAnswer(
            invocation -> {
              User u = invocation.getArgument(0);
              u.setUserId(2L);
              return 1;
            });

    // 执行创建
    UserDTO result = userService.createUser(userCreateDTO);

    // 验证结果
    assertNotNull(result);
    assertEquals(2L, result.getUserId());
    assertEquals("newuser", result.getUsername());
    assertEquals("新用户", result.getNickname());
    assertEquals("newuser@example.com", result.getEmail());
    assertEquals("13900139000", result.getPhone());

    // 验证服务调用
    verify(userMapper).selectByUsername("newuser");
    verify(passwordEncoder).encode("password123");
    verify(userMapper).insert(any(User.class));
  }

  @Test
  void testCreateUserWithDuplicateUsername() {
    // 准备测试数据
    UserCreateDTO userCreateDTO = new UserCreateDTO();
    userCreateDTO.setUsername("testuser");
    userCreateDTO.setPassword("password123");

    // Mock 服务调用（用户名已存在）
    when(userMapper.selectByUsername("testuser")).thenReturn(user);

    // 执行创建并验证异常
    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.createUser(userCreateDTO));

    assertEquals(SystemErrorCode.USERNAME_ALREADY_EXISTS, exception.getErrorCode());
    assertEquals("用户名已存在", exception.getMessage());

    // 验证服务调用
    verify(userMapper).selectByUsername("testuser");
    verify(userMapper, never()).insert(any(User.class));
  }

  @Test
  void testAssignRoleToUserSuccess() {
    // 准备测试数据
    Role role = new Role();
    role.setRoleId(1L);
    role.setRoleCode("admin");
    role.setStatus("ACTIVE");

    // Mock 服务调用
    when(userMapper.selectById(1L)).thenReturn(user);
    when(roleMapper.selectById(1L)).thenReturn(role);
    when(userRoleMapper.selectOne(any())).thenReturn(null);
    when(userRoleMapper.insert(any(UserRole.class))).thenReturn(1);

    // 执行分配
    userService.assignRoleToUser(1L, 1L);

    // 验证服务调用
    verify(userMapper).selectById(1L);
    verify(roleMapper).selectById(1L);
    verify(userRoleMapper).insert(any(UserRole.class));
  }

  @Test
  void testAssignRoleToUserWithUserNotFound() {
    // Mock 服务调用（用户不存在）
    when(userMapper.selectById(1L)).thenReturn(null);

    // 执行分配并验证异常
    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.assignRoleToUser(1L, 1L));

    assertEquals(SystemErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    assertEquals("用户不存在", exception.getMessage());

    // 验证服务调用
    verify(userMapper).selectById(1L);
    verify(roleMapper, never()).selectById(any());
    verify(userRoleMapper, never()).insert(any(UserRole.class));
  }

  @Test
  void testAssignRoleToUserWithRoleNotFound() {
    // Mock 服务调用（角色不存在）
    when(userMapper.selectById(1L)).thenReturn(user);
    when(roleMapper.selectById(1L)).thenReturn(null);

    // 执行分配并验证异常
    BusinessException exception =
        assertThrows(BusinessException.class, () -> userService.assignRoleToUser(1L, 1L));

    assertEquals(SystemErrorCode.ROLE_NOT_FOUND, exception.getErrorCode());
    assertEquals("角色不存在", exception.getMessage());

    // 验证服务调用
    verify(userMapper).selectById(1L);
    verify(roleMapper).selectById(1L);
    verify(userRoleMapper, never()).insert(any(UserRole.class));
  }
}
