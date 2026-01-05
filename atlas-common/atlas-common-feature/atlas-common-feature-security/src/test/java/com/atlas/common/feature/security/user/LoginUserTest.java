/*
 * Copyright (c) 2025 Atlas. All rights reserved.
 */
package com.atlas.common.feature.security.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/** LoginUser 接口单元测试 */
class LoginUserTest {

  /** 测试实现类：DefaultLoginUser */
  static class DefaultLoginUser implements LoginUser {
    private final Long userId;
    private final String username;
    private final List<String> roles;
    private final List<String> permissions;

    public DefaultLoginUser(
        Long userId, String username, List<String> roles, List<String> permissions) {
      this.userId = userId;
      this.username = username;
      this.roles = roles != null ? roles : Collections.emptyList();
      this.permissions = permissions != null ? permissions : Collections.emptyList();
    }

    @Override
    public Object getUserId() {
      return userId;
    }

    @Override
    public String getUsername() {
      return username;
    }

    @Override
    public List<String> getRoles() {
      return roles;
    }

    @Override
    public List<String> getPermissions() {
      return permissions;
    }

    @Override
    public boolean hasRole(String role) {
      return roles.contains(role);
    }

    @Override
    public boolean hasPermission(String permission) {
      return permissions.contains(permission);
    }
  }

  /** 扩展实现类：BusinessLoginUser */
  static class BusinessLoginUser extends DefaultLoginUser {
    private final String department;
    private final String email;

    public BusinessLoginUser(
        Long userId,
        String username,
        List<String> roles,
        List<String> permissions,
        String department,
        String email) {
      super(userId, username, roles, permissions);
      this.department = department;
      this.email = email;
    }

    public String getDepartment() {
      return department;
    }

    public String getEmail() {
      return email;
    }
  }

  @Test
  void testGetUserId() {
    // Given
    Long userId = 1001L;
    LoginUser user = new DefaultLoginUser(userId, "testuser", null, null);

    // When
    Object result = user.getUserId();

    // Then
    assertNotNull(result);
    assertEquals(userId, result);
  }

  @Test
  void testGetUsername() {
    // Given
    String username = "testuser";
    LoginUser user = new DefaultLoginUser(1001L, username, null, null);

    // When
    String result = user.getUsername();

    // Then
    assertNotNull(result);
    assertEquals(username, result);
    assertFalse(result.isEmpty());
  }

  @Test
  void testGetRoles() {
    // Given
    List<String> roles = Arrays.asList("ADMIN", "USER");
    LoginUser user = new DefaultLoginUser(1001L, "testuser", roles, null);

    // When
    List<String> result = user.getRoles();

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains("ADMIN"));
    assertTrue(result.contains("USER"));
  }

  @Test
  void testGetRolesEmpty() {
    // Given
    LoginUser user = new DefaultLoginUser(1001L, "testuser", null, null);

    // When
    List<String> result = user.getRoles();

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetPermissions() {
    // Given
    List<String> permissions = Arrays.asList("user:read", "user:write");
    LoginUser user = new DefaultLoginUser(1001L, "testuser", null, permissions);

    // When
    List<String> result = user.getPermissions();

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains("user:read"));
    assertTrue(result.contains("user:write"));
  }

  @Test
  void testGetPermissionsEmpty() {
    // Given
    LoginUser user = new DefaultLoginUser(1001L, "testuser", null, null);

    // When
    List<String> result = user.getPermissions();

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testHasRole() {
    // Given
    List<String> roles = Arrays.asList("ADMIN", "USER");
    LoginUser user = new DefaultLoginUser(1001L, "testuser", roles, null);

    // When & Then
    assertTrue(user.hasRole("ADMIN"));
    assertTrue(user.hasRole("USER"));
    assertFalse(user.hasRole("MANAGER"));
  }

  @Test
  void testHasPermission() {
    // Given
    List<String> permissions = Arrays.asList("user:read", "user:write");
    LoginUser user = new DefaultLoginUser(1001L, "testuser", null, permissions);

    // When & Then
    assertTrue(user.hasPermission("user:read"));
    assertTrue(user.hasPermission("user:write"));
    assertFalse(user.hasPermission("user:delete"));
  }

  @Test
  void testLoginUserExtensibility() {
    // Given
    Long userId = 1001L;
    String username = "testuser";
    List<String> roles = Arrays.asList("ADMIN");
    List<String> permissions = Arrays.asList("user:read");
    String department = "IT";
    String email = "test@example.com";

    // When
    BusinessLoginUser user =
        new BusinessLoginUser(userId, username, roles, permissions, department, email);

    // Then
    assertEquals(userId, user.getUserId());
    assertEquals(username, user.getUsername());
    assertEquals(roles, user.getRoles());
    assertEquals(permissions, user.getPermissions());
    assertEquals(department, user.getDepartment());
    assertEquals(email, user.getEmail());
    assertTrue(user.hasRole("ADMIN"));
    assertTrue(user.hasPermission("user:read"));
  }
}

